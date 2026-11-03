package com.labequip.action;

import com.labequip.dao.EquipmentDao;
import com.labequip.entity.Equipment;

import java.util.List;

/**
 * 设备管理：列表/搜索、新增/编辑、删除
 * URL 映射（struts.xml 通配符）：equipment-list / equipment-form / equipment-save / equipment-delete
 */
public class EquipmentAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    private String keyword;                 // 搜索关键字
    private String status;                  // 状态筛选
    private Equipment equipment = new Equipment();   // 表单对象（含 equipment.id 参数）
    private List<Equipment> equipmentList;

    /** 设备列表（含搜索） */
    public String list() {
        equipmentList = new EquipmentDao().list(keyword, status);
        return "list";
    }

    /** 新增/编辑表单（equipment.id>0 时为编辑，预填数据） */
    public String form() {
        if (equipment.getId() > 0) {
            Equipment db = new EquipmentDao().findById(equipment.getId());
            if (db != null) {
                equipment = db;
            }
        }
        return "form";
    }

    /** 保存（新增或更新） */
    public String save() {
        if (equipment.getName() == null || equipment.getName().trim().isEmpty()) {
            addFieldError("equipment.name", "设备名称不能为空！");
            return INPUT;
        }
        EquipmentDao dao = new EquipmentDao();
        if (equipment.getId() > 0) {
            dao.update(equipment);
        } else {
            dao.insert(equipment);
        }
        return SUCCESS;   // redirectAction -> equipment-list
    }

    /** 删除（存在借用记录时给出友好错误提示） */
    public String delete() {
        EquipmentDao dao = new EquipmentDao();
        try {
            dao.delete(equipment.getId());
        } catch (RuntimeException e) {
            addActionError(e.getMessage());
        }
        list();   // 重新加载数据后转发回列表页（转发才能显示错误信息）
        return "list";
    }

    // ---- getter / setter ----
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public List<Equipment> getEquipmentList() { return equipmentList; }
    public void setEquipmentList(List<Equipment> equipmentList) { this.equipmentList = equipmentList; }
}
