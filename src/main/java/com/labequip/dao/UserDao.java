package com.labequip.dao;

import com.labequip.entity.User;
import com.labequip.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 用户表 DAO
 */
public class UserDao {

    /** 登录校验：用户名+密码匹配返回用户对象，否则返回 null */
    public User login(String username, String password) {
        String sql = "SELECT * FROM `user` WHERE username=? AND password=? LIMIT 1";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("登录查询失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    public User findById(int id) {
        String sql = "SELECT * FROM `user` WHERE id=?";
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
            throw new RuntimeException("用户查询失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    /** 按用户名查询（注册时检查占用），不存在返回 null */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM `user` WHERE username=? LIMIT 1";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("用户查询失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    /** 新增用户（注册），返回自增ID；角色由调用方设置（注册固定为 student） */
    public int insert(User u) {
        String sql = "INSERT INTO `user`(username,password,real_name,student_no,role) VALUES(?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getRealName());
            ps.setString(4, u.getStudentNo() == null ? "" : u.getStudentNo());
            ps.setString(5, u.getRole() == null ? "student" : u.getRole());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            return keys.next() ? keys.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("用户注册失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(null, ps, conn);
        }
    }

    private User map(ResultSet rs) throws Exception {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRealName(rs.getString("real_name"));
        u.setStudentNo(rs.getString("student_no"));
        u.setRole(rs.getString("role"));
        u.setCreateTime(rs.getTimestamp("create_time"));
        return u;
    }
}
