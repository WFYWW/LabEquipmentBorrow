package com.labequip.servlet;

import com.labequip.util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 部署健康检查端点（实验七部署加固）：
 * 检查应用进程存活 + 数据库连通性，部署后无需登录即可快速验证，
 * 也可供监控脚本或搭档部署验证使用。
 */
@WebServlet(name = "healthServlet", urlPatterns = "/health")
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String db = "DOWN";
        long users = -1;
        try (Connection conn = DBUtil.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM `user`")) {
            rs.next();
            users = rs.getLong(1);
            db = "UP";
        } catch (Exception ignored) {
            // 健康检查自身不能抛异常，数据库不可达时降级输出 db=DOWN
        }

        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.print("{\"app\":\"labequip\","
                    + "\"status\":\"" + ("UP".equals(db) ? "ok" : "degraded") + "\","
                    + "\"db\":\"" + db + "\","
                    + "\"userCount\":" + users + ","
                    + "\"time\":\"" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\"}");
        }
    }
}