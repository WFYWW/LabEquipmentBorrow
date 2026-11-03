package com.labequip.action;

import com.labequip.dao.BorrowRecordDao;
import com.labequip.entity.User;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

/**
 * 业务 Action 的公共基类：
 * 1. 提供当前登录用户获取方法
 * 2. 提供全局逾期数量（导航栏角标用，惰性加载，每次请求只查一次）
 */
public class BaseAction extends ActionSupport {

    private Integer overdueCount;

    /** 当前登录用户（由 LoginInterceptor 保证非空） */
    public User getLoginUser() {
        HttpServletRequest request = ServletActionContext.getRequest();
        return (User) request.getSession().getAttribute("loginUser");
    }

    /** 全站逾期未还数量（导航栏红点角标） */
    public int getOverdueCount() {
        if (overdueCount == null) {
            overdueCount = new BorrowRecordDao().countOverdue();
        }
        return overdueCount;
    }
}
