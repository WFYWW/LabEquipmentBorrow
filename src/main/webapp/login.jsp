<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 - 校园实验设备借用系统</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="login-body">
    <div class="login-card">
        <div class="login-logo">🔬</div>
        <h1>校园实验设备借用系统</h1>
        <p class="login-sub">Java EE 课程设计 · Struts 2 + MySQL</p>

        <s:actionerror/>

        <form action="login.action" method="post">
            <label>用户名</label>
            <input type="text" name="username" class="input" placeholder="请输入用户名" autofocus>
            <label>密码</label>
            <input type="password" name="password" class="input" placeholder="请输入密码">
            <button type="submit" class="btn btn-primary btn-block">登　录</button>
        </form>

        <div class="login-tip">
            演示账号：<br>
            admin / 123456（管理员：管理设备、查看全部借用记录）<br>
            zhangsan / 123456（学员：有逾期未还，可演示"禁止再借"）<br>
            lisi / 123456（学员：正常用户）
        </div>
    </div>
</body>
</html>
