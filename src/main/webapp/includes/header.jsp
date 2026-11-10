<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<header class="topbar">
    <div class="topbar-inner">
        <a class="brand" href="home.action">🔬 校园实验设备借用系统</a>
        <nav class="nav">
            <a href="home.action" class="nav-link">首页</a>
            <a href="equipment-list.action" class="nav-link">设备管理</a>
            <a href="borrow-list.action" class="nav-link">借用记录</a>
            <a href="borrow-list.action?filter=overdue" class="nav-link">
                逾期提醒
                <s:if test="overdueCount > 0"><span class="nav-badge"><s:property value="overdueCount"/></span></s:if>
            </a>
        </nav>
        <div class="user-box">
            <span class="welcome">👤 <s:property value="loginUser.realName"/>
                <s:if test="isAdmin()"><span class="badge b-amber">管理员</span></s:if>
                <s:else><span class="badge b-blue">学员</span></s:else>
            </span>
            <a href="logout.action" class="btn btn-logout">退出登录</a>
        </div>
    </div>
</header>
