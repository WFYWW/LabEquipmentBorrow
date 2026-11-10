<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>首页 - 校园实验设备借用系统</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<jsp:include page="/includes/header.jsp"/>

<div class="container">
    <div class="page-title">
        <h1>概览</h1>
        <div>
            <a href="equipment-list.action" class="btn btn-outline">设备管理</a>
            <a href="borrow-list.action" class="btn btn-primary">借用记录</a>
        </div>
    </div>

    <%-- 统计卡片 --%>
    <div class="stat-grid">
        <div class="stat-card">
            <div class="stat-icon" style="background:#e0e7ff">📦</div>
            <div><div class="stat-num"><s:property value="equipmentCount"/></div><div class="stat-label">设备总数</div></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon" style="background:#dcfce7">✅</div>
            <div><div class="stat-num"><s:property value="availableCount"/></div><div class="stat-label">在库可借</div></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon" style="background:#dbeafe">📤</div>
            <div><div class="stat-num"><s:property value="borrowedCount"/></div><div class="stat-label">已借出</div></div>
        </div>
        <div class="stat-card">
            <div class="stat-icon" style="background:#fef3c7">🎒</div>
            <div><div class="stat-num"><s:property value="myActiveCount"/></div><div class="stat-label">我的在借</div></div>
        </div>
    </div>

    <%-- 逾期提醒（红色面板） --%>
    <div class="panel panel-danger">
        <div class="panel-head">🚨 <s:if test="isAdmin()">逾期提醒（全站）</s:if><s:else>我的逾期提醒</s:else></div>
        <s:if test="overdueList != null && !overdueList.isEmpty()">
            <table class="table">
                <tr><th>设备</th><th>借用人</th><th>学号</th><th>应还日期</th><th>逾期天数</th><th>操作</th></tr>
                <s:iterator value="overdueList">
                    <tr>
                        <td><s:property value="equipmentName"/></td>
                        <td><s:property value="borrowerName"/></td>
                        <td><s:property value="studentNo"/></td>
                        <td><s:property value="dueDateStr"/></td>
                        <td class="text-danger"><strong>已逾期 <s:property value="daysOverdue"/> 天</strong></td>
                        <td><a href="borrow-return.action?recordId=<s:property value='id'/>" class="btn btn-sm btn-primary">归还</a></td>
                    </tr>
                </s:iterator>
            </table>
        </s:if>
        <s:else>
            <div class="empty-state ok">✅ 暂无逾期未还记录，大家都很准时！</div>
        </s:else>
    </div>

    <%-- 即将到期（黄色面板） --%>
    <div class="panel panel-warn">
        <div class="panel-head">⏰ <s:if test="isAdmin()">3 天内到期（全站）</s:if><s:else>我的 3 天内到期</s:else></div>
        <s:if test="dueSoonList != null && !dueSoonList.isEmpty()">
            <table class="table">
                <tr><th>设备</th><th>借用人</th><th>应还日期</th><th>剩余</th></tr>
                <s:iterator value="dueSoonList">
                    <tr>
                        <td><s:property value="equipmentName"/></td>
                        <td><s:property value="borrowerName"/></td>
                        <td><s:property value="dueDateStr"/></td>
                        <td><s:if test="daysLeft == 0"><span class="badge b-red">今天到期</span></s:if>
                            <s:else><span class="badge b-amber">还剩 <s:property value="daysLeft"/> 天</span></s:else></td>
                    </tr>
                </s:iterator>
            </table>
        </s:if>
        <s:else>
            <div class="empty-state">暂无即将到期的设备</div>
        </s:else>
    </div>

    <%-- 我的在借 --%>
    <div class="panel">
        <div class="panel-head">🎒 我的在借（<s:property value="myActiveCount"/>）</div>
        <s:if test="myRecords != null && !myRecords.isEmpty()">
            <table class="table">
                <tr><th>设备</th><th>型号</th><th>借出日期</th><th>应还日期</th><th>状态</th><th>操作</th></tr>
                <s:iterator value="myRecords">
                    <tr>
                        <td><s:property value="equipmentName"/></td>
                        <td><s:property value="equipmentModel"/></td>
                        <td><s:property value="borrowDateStr"/></td>
                        <td><s:property value="dueDateStr"/></td>
                        <td>
                            <s:if test="isOverdue()"><span class="badge b-red">已逾期 <s:property value="daysOverdue"/> 天</span></s:if>
                            <s:else><span class="badge b-blue">借出中</span></s:else>
                        </td>
                        <td><a href="borrow-return.action?recordId=<s:property value='id'/>" class="btn btn-sm btn-outline">归还</a></td>
                    </tr>
                </s:iterator>
            </table>
        </s:if>
        <s:else>
            <div class="empty-state">还没有借用的设备，去 <a href="equipment-list.action">设备管理</a> 看看吧</div>
        </s:else>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
</body>
</html>
