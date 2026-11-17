package com.labequip.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC 连接工具类
 */
public class DBUtil {

    /**
     * 数据库连接配置（v1.2 部署加固）：
     * 优先读取环境变量（LABEQUIP_DB_URL / LABEQUIP_DB_USER / LABEQUIP_DB_PASSWORD），
     * 未设置时回退到本地开发默认值，部署环境无需修改源码即可替换数据库地址与口令。
     */
    private static final String URL = System.getenv().getOrDefault("LABEQUIP_DB_URL",
            "jdbc:mysql://localhost:3306/lab_equip"
            + "?useSSL=false&allowPublicKeyRetrieval=true"
            + "&characterEncoding=utf8&serverTimezone=Asia/Shanghai");
    private static final String USER = System.getenv().getOrDefault("LABEQUIP_DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("LABEQUIP_DB_PASSWORD", "123456");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("找不到 MySQL 驱动，请检查 pom.xml 依赖：" + e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /** 关闭资源（顺序：ResultSet -> Statement -> Connection） */
    public static void close(ResultSet rs, Statement st, Connection conn) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) { }
        }
        if (st != null) {
            try { st.close(); } catch (SQLException ignored) { }
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) { }
        }
    }

    public static void close(Statement st, Connection conn) {
        close(null, st, conn);
    }
}
