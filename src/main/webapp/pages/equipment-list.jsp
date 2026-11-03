<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>设备管理 - 校园实验设备借用系统</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<jsp:include page="/includes/header.jsp"/>

<div class="container">
    <div class="page-title">
        <h1>设备管理</h1>
        <a href="equipment-form.action" class="btn btn-primary">＋ 新增设备</a>
    </div>

    <s:actionerror/>

    <%-- 搜索栏 --%>
    <form action="equipment-list.action" method="get" class="toolbar">
        <input type="text" name="keyword" class="input" placeholder="按名称 / 型号 / 位置搜索…"
               value="<s:property value='keyword'/>">
        <s:select name="status" headerKey="" headerValue="全部状态"
                  list="{'在库','借出','维修','报废'}" cssClass="input"/>
        <button type="submit" class="btn btn-outline">🔍 搜索</button>
        <a href="equipment-list.action" class="btn btn-text">重置</a>
    </form>

    <div class="panel">
        <table class="table">
            <tr>
                <th>ID</th><th>设备名称</th><th>规格型号</th><th>存放位置</th>
                <th>状态</th><th>备注</th><th>录入时间</th><th style="width:210px">操作</th>
            </tr>
            <s:if test="equipmentList != null && !equipmentList.isEmpty()">
                <s:iterator value="equipmentList">
                    <tr>
                        <td><s:property value="id"/></td>
                        <td><strong><s:property value="name"/></strong></td>
                        <td><s:property value="model"/></td>
                        <td><s:property value="location"/></td>
                        <td>
                            <s:if test="status == '在库'"><span class="badge b-green">在库</span></s:if>
                            <s:elseif test="status == '借出'"><span class="badge b-blue">借出</span></s:elseif>
                            <s:elseif test="status == '维修'"><span class="badge b-amber">维修</span></s:elseif>
                            <s:else><span class="badge b-gray">报废</span></s:else>
                        </td>
                        <td class="text-muted"><s:property value="description"/></td>
                        <td class="text-muted"><s:property value="createTime"/></td>
                        <td class="ops">
                            <s:if test="status == '在库'">
                                <a href="borrow-form.action?equipmentId=<s:property value='id'/>"
                                   class="btn btn-sm btn-success">借用</a>
                            </s:if>
                            <a href="equipment-form.action?equipment.id=<s:property value='id'/>"
                               class="btn btn-sm btn-outline">编辑</a>
                            <a href="equipment-delete.action?equipment.id=<s:property value='id'/>"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('确定删除该设备吗？')">删除</a>
                        </td>
                    </tr>
                </s:iterator>
            </s:if>
            <s:else>
                <tr><td colspan="8"><div class="empty-state">没有符合条件的设备</div></td></tr>
            </s:else>
        </table>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
</body>
</html>
