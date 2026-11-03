<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>借用记录 - 校园实验设备借用系统</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<jsp:include page="/includes/header.jsp"/>

<div class="container">
    <div class="page-title">
        <h1>借用记录</h1>
    </div>

    <%-- 筛选页签 --%>
    <div class="tabs">
        <s:a action="borrow-list" cssClass="%{filter == 'all' ? 'tab active' : 'tab'}">全部</s:a>
        <s:a action="borrow-list" cssClass="%{filter == 'active' ? 'tab active' : 'tab'}">
            <s:param name="filter" value="'active'"/>借出中</s:a>
        <s:a action="borrow-list" cssClass="%{filter == 'returned' ? 'tab active' : 'tab'}">
            <s:param name="filter" value="'returned'"/>已归还</s:a>
        <s:a action="borrow-list" cssClass="%{filter == 'overdue' ? 'tab active' : 'tab'}">
            <s:param name="filter" value="'overdue'"/>⚠️ 逾期</s:a>
        <s:a action="borrow-list" cssClass="%{mine ? 'tab active' : 'tab'}">
            <s:param name="mine" value="true"/>我的借用</s:a>
    </div>

    <div class="panel">
        <table class="table">
            <tr>
                <th>设备</th><th>借用人</th><th>借出日期</th><th>应还日期</th>
                <th>归还日期</th><th>状态</th><th style="width:90px">操作</th>
            </tr>
            <s:if test="recordList != null && !recordList.isEmpty()">
                <s:iterator value="recordList">
                    <tr>
                        <td>
                            <strong><s:property value="equipmentName"/></strong>
                            <span class="text-muted">（<s:property value="equipmentModel"/>）</span>
                        </td>
                        <td><s:property value="borrowerName"/> <span class="text-muted"><s:property value="studentNo"/></span></td>
                        <td><s:property value="borrowDateStr"/></td>
                        <td><s:property value="dueDateStr"/></td>
                        <td><s:property value="returnDateStr"/></td>
                        <td>
                            <s:if test="isOverdue()">
                                <span class="badge b-red">已逾期 <s:property value="daysOverdue"/> 天</span>
                            </s:if>
                            <s:elseif test="isReturnedLate()">
                                <span class="badge b-orange">逾期归还</span>
                            </s:elseif>
                            <s:elseif test="status == '已归还'">
                                <span class="badge b-green">按期归还</span>
                            </s:elseif>
                            <s:else>
                                <span class="badge b-blue">借出中</span>
                            </s:else>
                        </td>
                        <td>
                            <s:if test="status == '借出中'">
                                <a href="borrow-return.action?recordId=<s:property value='id'/>"
                                   class="btn btn-sm btn-primary"
                                   onclick="return confirm('确认归还该设备吗？')">归还</a>
                            </s:if>
                        </td>
                    </tr>
                </s:iterator>
            </s:if>
            <s:else>
                <tr><td colspan="7"><div class="empty-state">暂无借用记录</div></td></tr>
            </s:else>
        </table>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
</body>
</html>
