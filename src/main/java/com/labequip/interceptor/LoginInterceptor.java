package com.labequip.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import java.util.Map;

/**
 * 登录校验拦截器：
 * 未登录的请求一律返回 "login"，由 struts.xml 的全局结果跳转到登录页。
 */
public class LoginInterceptor extends AbstractInterceptor {

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> session = invocation.getInvocationContext().getSession();
        if (session.get("loginUser") == null) {
            return "login";   // 命中 app 包的 global-results -> /login.jsp
        }
        return invocation.invoke();
    }
}
