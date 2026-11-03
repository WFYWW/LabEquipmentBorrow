<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>提示 - 校园实验设备借用系统</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<jsp:include page="/includes/header.jsp"/>

<div class="container container-narrow">
    <div class="msg-card">
        <div class="msg-icon">⚠️</div>
        <h2>操作无法完成</h2>
        <s:actionerror/>
        <div class="form-actions" style="justify-content:center">
            <a href="javascript:history.back()" class="btn btn-outline">← 返回上页</a>
            <a href="home.action" class="btn btn-primary">回首页</a>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
</body>
</html>
