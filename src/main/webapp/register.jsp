<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>注册 - 校园实验设备借用系统</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="login-body">
    <div class="login-card login-card-wide">
        <div class="login-logo">📝</div>
        <h1>注册新账号</h1>
        <p class="login-sub">注册后即为学员账号，可浏览和借用设备；管理员由系统预置</p>

        <s:fielderror/>

        <s:form action="register-save" theme="simple">
            <label>用户名 <span class="req">*</span></label>
            <s:textfield name="username" cssClass="input" placeholder="3-20 位字母、数字或下划线"/>
            <s:fielderror fieldName="username"/>

            <label>真实姓名 <span class="req">*</span></label>
            <s:textfield name="realName" cssClass="input" placeholder="如：王小明"/>
            <s:fielderror fieldName="realName"/>

            <label>学号</label>
            <s:textfield name="studentNo" cssClass="input" placeholder="选填，如：2024001103"/>
            <s:fielderror fieldName="studentNo"/>

            <label>密码 <span class="req">*</span></label>
            <s:password name="password" cssClass="input" placeholder="6-20 位"/>
            <s:fielderror fieldName="password"/>

            <label>确认密码 <span class="req">*</span></label>
            <s:password name="confirmPassword" cssClass="input" placeholder="再输入一次密码"/>
            <s:fielderror fieldName="confirmPassword"/>

            <button type="submit" class="btn btn-primary btn-block">注　册</button>
        </s:form>

        <div class="reg-link">
            已有账号？<a href="login.jsp">返回登录</a>
        </div>
    </div>
</body>
</html>
