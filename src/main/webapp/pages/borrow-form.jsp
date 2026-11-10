<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>借用设备 - 校园实验设备借用系统</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<jsp:include page="/includes/header.jsp"/>

<div class="container container-narrow">
    <div class="page-title">
        <h1>📤 借用设备</h1>
        <a href="equipment-list.action" class="btn btn-text">← 返回列表</a>
    </div>

    <%-- 设备信息卡片 --%>
    <div class="panel equip-brief">
        <div class="panel-head">🧰 借用设备信息</div>
        <div class="brief-img">
            <img src="<s:property value='equipment.imageDisplay'/>" alt="<s:property value='equipment.name'/>">
        </div>
        <div class="brief-grid">
            <div><span class="brief-label">设备名称</span><strong><s:property value="equipment.name"/></strong></div>
            <div><span class="brief-label">规格型号</span><s:property value="equipment.model"/></div>
            <div><span class="brief-label">存放位置</span><s:property value="equipment.location"/></div>
            <div><span class="brief-label">当前状态</span><span class="badge b-green">在库</span></div>
        </div>
    </div>

    <%-- 借用表单 --%>
    <form action="borrow-create.action" method="post" class="form-card">
        <input type="hidden" name="equipmentId" value="<s:property value='equipmentId'/>">

        <div class="form-row">
            <label>借用人</label>
            <input type="text" class="input" disabled
                   value="<s:property value='loginUser.realName'/>（<s:property value='loginUser.studentNo'/>）">
            <div class="form-hint">借用记录将自动关联到当前登录用户</div>
        </div>
        <div class="form-row">
            <label>借出日期</label>
            <input type="text" class="input" disabled value="今天（提交时自动记录）">
        </div>
        <div class="form-row">
            <label>应还日期 <span class="req">*</span></label>
            <input type="date" name="dueDate" id="dueDate" class="input" required>
            <s:fielderror fieldName="dueDate"/>
            <div class="form-hint">单次借用最长 90 天；到期未还将触发逾期提醒并暂停借用资格</div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">确认借用</button>
            <a href="equipment-list.action" class="btn btn-text">取消</a>
        </div>
    </form>
</div>

<script>
    // 应还日期最早为明天
    document.getElementById('dueDate').min =
        new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().slice(0, 10);
</script>

<jsp:include page="/includes/footer.jsp"/>
</body>
</html>
