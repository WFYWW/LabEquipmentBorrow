package com.labequip.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * 实验一：Servlet 基础 —— 生命周期演示 Servlet（JavaEE8 / javax.* 基线）
 *
 * 访问 /hello 输出当前服务器时间与自定义欢迎信息；
 * init / service / destroy 各打印一条日志，用于观察 Servlet 生命周期。
 */
@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(HelloServlet.class.getName());

    /** 生命周期第 1 步：首次访问（或容器启动）时初始化，仅执行一次 */
    @Override
    public void init() {
        LOG.info("[HelloServlet] init: Servlet 初始化，仅执行一次");
    }

    /**
     * 生命周期第 2 步：每个请求进入 service。
     * 此处直接覆写 service()，GET/POST 都会走到；同时演示请求参数、
     * 响应类型与字符编码设置（避免中文乱码）。
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");

        String name = req.getParameter("name");
        String greeting = (name == null || name.trim().isEmpty())
                ? "欢迎学习 Java EE（本项目基线：JavaEE8 / javax.* / Tomcat 9）"
                : "你好，" + name.trim() + "！欢迎学习 Java EE（基线：JavaEE8 / javax.* / Tomcat 9）";
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LOG.info("[HelloServlet] service: 处理一次 /hello 请求, name=" + name);

        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Hello Servlet</title></head>");
            out.println("<body style=\"font-family:'Segoe UI',Arial,sans-serif;background:#f6f8fb;color:#1f2937;margin:0;padding:48px;\">");
            out.println("<h2>Hello, Servlet!</h2>");
            out.println("<p>" + greeting + "</p>");
            out.println("<p>当前服务器时间：" + now + "</p>");
            out.println("<p>试试带参数访问：<a href='hello?name=LabEquipmentBorrow'>/hello?name=LabEquipmentBorrow</a></p>");
            out.println("</body></html>");
        }
    }

    /** 生命周期第 3 步：应用停止/重部署时销毁，仅执行一次 */
    @Override
    public void destroy() {
        LOG.info("[HelloServlet] destroy: Servlet 销毁，仅执行一次");
    }
}