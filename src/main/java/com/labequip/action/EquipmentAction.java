package com.labequip.action;

import com.labequip.dao.EquipmentDao;
import com.labequip.entity.Equipment;
import org.apache.struts2.ServletActionContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备管理：列表/搜索、新增/编辑、删除
 * URL 映射（struts.xml 通配符）：equipment-list / equipment-form / equipment-save / equipment-delete
 * 权限：列表所有人可见；新增/编辑/删除仅管理员（save 支持图片上传）
 */
public class EquipmentAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    private String keyword;                 // 搜索关键字
    private String status;                  // 状态筛选
    private Equipment equipment = new Equipment();   // 表单对象（含 equipment.id 参数）
    private List<Equipment> equipmentList;

    // ---- 设备图片：Struts 文件上传拦截器按字段名注入 ----
    private File upload;                    // 表单 <input type="file" name="upload">
    private String uploadFileName;          // 上传文件的原始文件名
    private String uploadContentType;       // 上传文件的 MIME 类型

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;   // 单张图片上限 5MB
    private static final String[] ALLOWED_EXT = {"jpg", "jpeg", "png", "gif", "webp"};

    /** 可选预设图片（key=webapp 相对路径作为 option 值，value=显示文字），与 webapp/images/equip/ 内置图一致 */
    private static final Map<String, String> PRESET_IMAGES = new LinkedHashMap<>();
    static {
        PRESET_IMAGES.put("images/equip/oscilloscope.jpg",      "示波器");
        PRESET_IMAGES.put("images/equip/multimeter.jpg",        "万用表");
        PRESET_IMAGES.put("images/equip/signal-generator.jpg",  "信号发生器");
        PRESET_IMAGES.put("images/equip/power-supply.jpg",      "稳压电源");
        PRESET_IMAGES.put("images/equip/microscope.jpg",        "显微镜");
        PRESET_IMAGES.put("images/equip/3d-printer.jpg",        "3D打印机");
        PRESET_IMAGES.put("images/equip/spectrum-analyzer.jpg", "频谱分析仪");
    }

    /** 设备列表（含搜索） */
    public String list() {
        equipmentList = new EquipmentDao().list(keyword, status);
        return "list";
    }

    /** 新增/编辑表单（equipment.id>0 时为编辑，预填数据）。仅管理员可进入 */
    public String form() {
        if (!checkAdmin()) {
            return "deny";   // 全局结果 -> /pages/message.jsp
        }
        if (equipment.getId() > 0) {
            Equipment db = new EquipmentDao().findById(equipment.getId());
            if (db != null) {
                equipment = db;
            }
        }
        return "form";
    }

    /** 保存（新增或更新）。仅管理员可操作 */
    public String save() {
        if (!checkAdmin()) {
            return "deny";
        }
        if (equipment.getName() == null || equipment.getName().trim().isEmpty()) {
            addFieldError("equipment.name", "设备名称不能为空！");
            return INPUT;
        }
        equipment.setName(equipment.getName().trim());
        applyImage();                        // 处理图片：新上传 > 预设选择 > 保留原值
        if (hasFieldErrors()) {
            return INPUT;                    // 图片校验不通过，回表单提示
        }
        EquipmentDao dao = new EquipmentDao();
        if (equipment.getId() > 0) {
            dao.update(equipment);
        } else {
            dao.insert(equipment);
        }
        return SUCCESS;   // redirectAction -> equipment-list
    }

    /** 删除（存在借用记录时给出友好错误提示）。仅管理员可操作 */
    public String delete() {
        if (!checkAdmin()) {
            return "deny";
        }
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

    // ---- 图片上传 getter / setter（Struts 文件上传拦截器按名注入） ----
    public void setUpload(File upload) { this.upload = upload; }
    public void setUploadFileName(String uploadFileName) { this.uploadFileName = uploadFileName; }
    public void setUploadContentType(String uploadContentType) { this.uploadContentType = uploadContentType; }

    /**
     * 预设图片下拉框数据（s:select 传 Map 时默认 key 作值、value 作文字）。
     * 编辑时若当前图片不是预设图（如曾上传过），追加为"当前图片"选项，避免回显丢失。
     */
    public Map<String, String> getPresetImages() {
        Map<String, String> m = new LinkedHashMap<>(PRESET_IMAGES);
        String cur = equipment.getImage();
        if (cur != null && !cur.trim().isEmpty() && !PRESET_IMAGES.containsKey(cur.trim())) {
            m.put(cur.trim(), "当前图片");
        }
        return m;
    }

    // ---- 私有辅助 ----

    /** 管理员校验：非管理员设置错误信息并返回 false */
    private boolean checkAdmin() {
        if (!isAdmin()) {
            addActionError("普通学员无权修改设备信息，请联系管理员！");
            return false;
        }
        return true;
    }

    /**
     * 处理设备图片，优先级：新上传文件 > 表单选择的预设图 > 编辑时保留数据库原值。
     * 校验不通过时设置 fieldError（保存流程返回 INPUT）。
     */
    private void applyImage() {
        if (upload != null && upload.exists() && upload.length() > 0) {
            if (upload.length() > MAX_IMAGE_SIZE) {
                addFieldError("upload", "图片大小不能超过 5MB！");
                return;
            }
            String ext = extOf(uploadFileName);
            boolean allowed = false;
            for (String e : ALLOWED_EXT) {
                if (e.equals(ext)) { allowed = true; break; }
            }
            if (!allowed) {
                addFieldError("upload", "图片仅支持 jpg / jpeg / png / gif / webp 格式！");
                return;
            }
            String dirPath = ServletActionContext.getServletContext().getRealPath("/images/equip");
            File dir = new File(dirPath);
            if (!dir.exists() && !dir.mkdirs()) {
                addFieldError("upload", "图片目录创建失败，请检查服务器权限！");
                return;
            }
            // 文件名用时间戳避免重名与路径拼接问题；旧文件保留不删，便于回溯
            String savedName = System.currentTimeMillis() + "_img." + ext;
            try {
                Files.copy(upload.toPath(), new File(dir, savedName).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("图片保存失败：" + e.getMessage(), e);
            }
            equipment.setImage("images/equip/" + savedName);
        } else if (equipment.getId() > 0
                && (equipment.getImage() == null || equipment.getImage().trim().isEmpty())) {
            // 未上传新图也未选择预设时（如直接调接口），编辑场景保留数据库原值
            Equipment db = new EquipmentDao().findById(equipment.getId());
            if (db != null) {
                equipment.setImage(db.getImage());
            }
        }
    }

    /** 取文件扩展名（小写，不含点） */
    private String extOf(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}
