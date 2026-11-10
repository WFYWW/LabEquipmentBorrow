package com.labequip.dao;

import com.labequip.entity.Equipment;
import com.labequip.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备表 DAO
 */
public class EquipmentDao {

    /** 设备列表：支持关键字（名称/型号/位置模糊匹配）与状态筛选，均可为空 */
    public List<Equipment> list(String keyword, String status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM `equipment` WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR model LIKE ? OR location LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        if (status != null && !status.trim().isEmpty() && !"全部".equals(status.trim())) {
            sql.append(" AND status=?");
            params.add(status.trim());
        }
        sql.append(" ORDER BY id");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Equipment> list = new ArrayList<>();
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
            throw new RuntimeException("设备查询失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    public Equipment findById(int id) {
        String sql = "SELECT * FROM `equipment` WHERE id=?";
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
            throw new RuntimeException("设备查询失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    /** 新增设备，返回自增ID */
    public int insert(Equipment e) {
        String sql = "INSERT INTO `equipment`(name,model,location,status,description,image) VALUES(?,?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            fill(ps, e);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            return keys.next() ? keys.getInt(1) : 0;
        } catch (Exception ex) {
            throw new RuntimeException("设备新增失败：" + ex.getMessage(), ex);
        } finally {
            DBUtil.close(null, ps, conn);
        }
    }

    /** 更新设备，返回受影响行数 */
    public int update(Equipment e) {
        String sql = "UPDATE `equipment` SET name=?,model=?,location=?,status=?,description=?,image=? WHERE id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            fill(ps, e);
            ps.setInt(7, e.getId());
            return ps.executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException("设备更新失败：" + ex.getMessage(), ex);
        } finally {
            DBUtil.close(null, ps, conn);
        }
    }

    /**
     * 删除设备。
     * 若设备存在借用记录（外键约束），转换为业务异常供 Action 提示用户。
     */
    public int delete(int id) {
        String sql = "DELETE FROM `equipment` WHERE id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            String msg = String.valueOf(ex.getMessage());
            if (ex instanceof java.sql.SQLIntegrityConstraintViolationException
                    || msg.contains("foreign key") || msg.contains("FOREIGN KEY")) {
                throw new RuntimeException("该设备存在借用记录，无法删除（可将状态改为“报废”）！");
            }
            throw new RuntimeException("设备删除失败：" + ex.getMessage(), ex);
        } finally {
            DBUtil.close(null, ps, conn);
        }
    }

    /** 设备总数 */
    public int countAll() {
        return count("SELECT COUNT(*) FROM `equipment`");
    }

    /** 按状态统计数量 */
    public int countByStatus(String status) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT COUNT(*) FROM `equipment` WHERE status=?")) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("设备统计失败：" + e.getMessage(), e);
        }
    }

    private int count(String sql) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("设备统计失败：" + e.getMessage(), e);
        }
    }

    private void fill(PreparedStatement ps, Equipment e) throws SQLException {
        ps.setString(1, e.getName());
        ps.setString(2, e.getModel());
        ps.setString(3, e.getLocation());
        ps.setString(4, e.getStatus() == null ? "在库" : e.getStatus());
        ps.setString(5, e.getDescription());
        ps.setString(6, e.getImage() == null ? "" : e.getImage().trim());
    }

    private Equipment map(ResultSet rs) throws SQLException {
        Equipment e = new Equipment();
        e.setId(rs.getInt("id"));
        e.setName(rs.getString("name"));
        e.setModel(rs.getString("model"));
        e.setLocation(rs.getString("location"));
        e.setStatus(rs.getString("status"));
        e.setDescription(rs.getString("description"));
        e.setImage(rs.getString("image"));
        e.setCreateTime(rs.getTimestamp("create_time"));
        return e;
    }
}
