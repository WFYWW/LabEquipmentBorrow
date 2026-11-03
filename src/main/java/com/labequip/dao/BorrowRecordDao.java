package com.labequip.dao;

import com.labequip.entity.BorrowRecord;
import com.labequip.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 借用记录表 DAO
 * 核心规则：
 *   逾期 = return_date IS NULL AND due_date < CURDATE()（实时计算，不落库）
 *   借用/归还均涉及"记录 + 设备状态"两张表，使用事务保证一致性。
 */
public class BorrowRecordDao {

    private static final String JOIN_SQL =
            "SELECT r.*, e.name AS equipment_name, e.model AS equipment_model, "
            + "u.real_name AS borrower_name, u.student_no AS student_no "
            + "FROM `borrow_record` r "
            + "JOIN `equipment` e ON r.equipment_id = e.id "
            + "JOIN `user` u ON r.user_id = u.id";

    /** 借用记录列表：filter=all/active/returned/overdue；userId 非空则只看该用户 */
    public List<BorrowRecord> list(String filter, Integer userId) {
        StringBuilder sql = new StringBuilder(JOIN_SQL + " WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if ("active".equals(filter)) {
            sql.append(" AND r.return_date IS NULL");
        } else if ("returned".equals(filter)) {
            sql.append(" AND r.return_date IS NOT NULL");
        } else if ("overdue".equals(filter)) {
            sql.append(" AND r.return_date IS NULL AND r.due_date < CURDATE()");
        }
        if (userId != null) {
            sql.append(" AND r.user_id=?");
            params.add(userId);
        }
        // 在借的排前面且按应还日升序（最紧急的排最上），历史记录按借出时间倒序
        sql.append(" ORDER BY (r.return_date IS NULL) DESC, r.due_date ASC, r.id DESC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<BorrowRecord> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("借用记录查询失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    public BorrowRecord findById(int id) {
        String sql = JOIN_SQL + " WHERE r.id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("借用记录查询失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    /** 全站逾期未还列表（首页"逾期提醒"模块） */
    public List<BorrowRecord> listOverdue() {
        String sql = JOIN_SQL
                + " WHERE r.return_date IS NULL AND r.due_date < CURDATE()"
                + " ORDER BY r.due_date ASC";
        return queryList(sql);
    }

    /** 全站即将到期列表（withinDays 天内，含今天） */
    public List<BorrowRecord> listDueSoon(int withinDays) {
        String sql = JOIN_SQL
                + " WHERE r.return_date IS NULL"
                + " AND DATEDIFF(r.due_date, CURDATE()) BETWEEN 0 AND " + withinDays
                + " ORDER BY r.due_date ASC";
        return queryList(sql);
    }

    /** 某用户当前在借列表（我的在借） */
    public List<BorrowRecord> listActiveByUser(int userId) {
        String sql = JOIN_SQL
                + " WHERE r.user_id=? AND r.return_date IS NULL"
                + " ORDER BY r.due_date ASC";
        return queryList(sql, userId);
    }

    /** 全站逾期数量（导航角标） */
    public int countOverdue() {
        String sql = "SELECT COUNT(*) FROM `borrow_record` WHERE return_date IS NULL AND due_date < CURDATE()";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("逾期统计失败：" + e.getMessage(), e);
        }
    }

    /** 某用户在借数量 */
    public int countActiveByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM `borrow_record` WHERE user_id=? AND return_date IS NULL";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("在借统计失败：" + e.getMessage(), e);
        }
    }

    /** 该用户是否有逾期未还（逾期禁止再借的判断依据） */
    public boolean hasOverdue(int userId) {
        String sql = "SELECT COUNT(*) FROM `borrow_record` "
                + "WHERE user_id=? AND return_date IS NULL AND due_date < CURDATE()";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("逾期校验失败：" + e.getMessage(), e);
        }
    }

    /**
     * 创建借用（事务）：
     * 1. 插入借用记录
     * 2. 将设备状态改为"借出"（若设备已不是"在库"，说明被抢先借走，整体回滚）
     */
    public boolean createBorrow(BorrowRecord r) {
        String insSql = "INSERT INTO `borrow_record`(equipment_id,user_id,borrow_date,due_date,status) "
                + "VALUES(?,?,?,?,'借出中')";
        String updSql = "UPDATE `equipment` SET status='借出' WHERE id=? AND status='在库'";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(insSql);
            ps.setInt(1, r.getEquipmentId());
            ps.setInt(2, r.getUserId());
            ps.setDate(3, java.sql.Date.valueOf(r.getBorrowDate()));
            ps.setDate(4, java.sql.Date.valueOf(r.getDueDate()));
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement(updSql);
            ps.setInt(1, r.getEquipmentId());
            int updated = ps.executeUpdate();
            if (updated != 1) {           // 设备状态已变化，回滚
                conn.rollback();
                return false;
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            rollbackQuietly(conn);
            throw new RuntimeException("借用操作失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(null, ps, conn);
        }
    }

    /**
     * 归还设备（事务）：
     * 1. 校验记录处于"借出中"
     * 2. 写入归还日期=今天，状态改"已归还"
     * 3. 设备状态恢复"在库"
     */
    public boolean doReturn(int recordId) {
        String qSql = "SELECT equipment_id FROM `borrow_record` WHERE id=? AND return_date IS NULL FOR UPDATE";
        String uSql = "UPDATE `borrow_record` SET return_date=CURDATE(), status='已归还' WHERE id=?";
        String eSql = "UPDATE `equipment` SET status='在库' WHERE id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(qSql);
            ps.setInt(1, recordId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {             // 不存在或已归还
                conn.rollback();
                return false;
            }
            int equipmentId = rs.getInt("equipment_id");
            rs.close();
            ps.close();

            ps = conn.prepareStatement(uSql);
            ps.setInt(1, recordId);
            ps.executeUpdate();
            ps.close();

            ps = conn.prepareStatement(eSql);
            ps.setInt(1, equipmentId);
            ps.executeUpdate();

            conn.commit();
            return true;
        } catch (Exception e) {
            rollbackQuietly(conn);
            throw new RuntimeException("归还操作失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(null, ps, conn);
        }
    }

    // ---------------- 私有辅助 ----------------

    private List<BorrowRecord> queryList(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<BorrowRecord> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("借用记录查询失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    private void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ignored) { }
        }
    }

    private BorrowRecord map(ResultSet rs) throws SQLException {
        BorrowRecord r = new BorrowRecord();
        r.setId(rs.getInt("id"));
        r.setEquipmentId(rs.getInt("equipment_id"));
        r.setUserId(rs.getInt("user_id"));
        java.sql.Date b = rs.getDate("borrow_date");
        r.setBorrowDate(b == null ? null : b.toLocalDate());
        java.sql.Date d = rs.getDate("due_date");
        r.setDueDate(d == null ? null : d.toLocalDate());
        java.sql.Date ret = rs.getDate("return_date");
        r.setReturnDate(ret == null ? null : ret.toLocalDate());
        r.setStatus(rs.getString("status"));
        r.setEquipmentName(rs.getString("equipment_name"));
        r.setEquipmentModel(rs.getString("equipment_model"));
        r.setBorrowerName(rs.getString("borrower_name"));
        r.setStudentNo(rs.getString("student_no"));
        return r;
    }
}
