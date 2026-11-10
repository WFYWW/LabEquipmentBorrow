package com.labequip.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 实验设备实体类
 * 状态取值：在库 / 借出 / 维修 / 报废
 */
public class Equipment implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;         // 设备名称
    private String model;        // 规格型号
    private String location;     // 存放位置
    private String status;       // 设备状态
    private String description;  // 备注说明
    private String image;        // 设备图片（webapp 下相对路径，如 images/equip/xxx.jpg）
    private Timestamp createTime;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    /** 页面展示用：未设置图片时返回默认占位图 */
    public String getImageDisplay() {
        return (image == null || image.trim().isEmpty())
                ? "images/equip/default.jpg" : image.trim();
    }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }
}
