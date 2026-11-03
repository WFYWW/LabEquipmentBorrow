package com.labequip.action;

import com.labequip.dao.UserDao;
import com.labequip.entity.User;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录/注销 Action（位于 public 包，不受登录拦截器保护）
 */
public class LoginAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    @Override
    public String execute() {
        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            addActionError("请输入用户名和密码！");
            return "fail";
        }
        User user = new UserDao().login(username.trim(), password);
        if (user == null) {
            addActionError("用户名或密码错误！");
            return "fail";
        }
        HttpServletRequest request = ServletActionContext.getRequest();
        request.getSession().setAttribute("loginUser", user);
        return "ok";
    }

    public String logout() {
        HttpServletRequest request = ServletActionContext.getRequest();
        request.getSession().invalidate();
        return "ok";
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
