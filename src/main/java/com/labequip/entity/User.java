package com.labequip.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 用户实体类（遵循 JavaBean 规范）
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;    // 登录名
    private String password;    // 密码（课程演示用明文）
    private String realName;    // 真实姓名
    private String studentNo;   // 学号
    private Timestamp createTime;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }
}
