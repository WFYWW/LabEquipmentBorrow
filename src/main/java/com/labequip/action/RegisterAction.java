package com.labequip.action;

import com.labequip.dao.UserDao;
import com.labequip.entity.User;
import com.opensymphony.xwork2.ActionSupport;

import java.util.regex.Pattern;

/**
 * 用户注册（位于 public 包，无需登录即可访问）
 *
 * 规则：
 * 1. 注册用户的角色固定为学员（student），管理员账号由数据库脚本预置，不能通过注册获得
 * 2. 用户名 3-20 位字母 / 数字 / 下划线，全局唯一
 * 3. 密码 6-20 位，需两次输入一致（课程演示项目沿用明文存储约定）
 */
public class RegisterAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,20}$");

    private String username;
    private String password;
    private String confirmPassword;
    private String realName;
    private String studentNo;

    /** 显示注册表单 */
    @Override
    public String execute() {
        return INPUT;
    }

    /** 提交注册 */
    public String register() {
        validateInput();
        if (hasFieldErrors()) {
            return INPUT;
        }
        UserDao dao = new UserDao();
        if (dao.findByUsername(username) != null) {
            addFieldError("username", "该用户名已被注册，请换一个！");
            return INPUT;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName.trim());
        user.setStudentNo(studentNo == null ? "" : studentNo.trim());
        user.setRole("student");
        dao.insert(user);
        return SUCCESS;   // redirect -> login.jsp?registered=1
    }

    /** 字段校验：全部通过则不设置任何 fieldError */
    private void validateInput() {
        username = username == null ? null : username.trim();
        if (username == null || username.isEmpty()) {
            addFieldError("username", "请输入用户名！");
        } else if (!USERNAME_PATTERN.matcher(username).matches()) {
            addFieldError("username", "用户名须为 3-20 位字母、数字或下划线！");
        }

        if (realName == null || realName.trim().isEmpty()) {
            addFieldError("realName", "请输入真实姓名！");
        } else if (realName.trim().length() > 50) {
            addFieldError("realName", "姓名过长（最多 50 字）！");
        }

        if (studentNo != null && studentNo.trim().length() > 20) {
            addFieldError("studentNo", "学号过长（最多 20 位）！");
        }

        if (password == null || password.isEmpty()) {
            addFieldError("password", "请输入密码！");
        } else if (password.length() < 6 || password.length() > 20) {
            addFieldError("password", "密码长度须为 6-20 位！");
        }

        if (confirmPassword == null || !confirmPassword.equals(password)) {
            addFieldError("confirmPassword", "两次输入的密码不一致！");
        }
    }

    // ---- getter / setter ----
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
}
