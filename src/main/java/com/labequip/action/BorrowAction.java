package com.labequip.action;

import com.labequip.dao.BorrowRecordDao;
import com.labequip.dao.EquipmentDao;
import com.labequip.entity.BorrowRecord;
import com.labequip.entity.Equipment;
import com.labequip.util.DateUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * 设备借用与归还
 * URL 映射（struts.xml 通配符）：borrow-list / borrow-form / borrow-create / borrow-return
 *
 * 核心业务规则：
 * 1. 只能借用状态为"在库"的设备
 * 2. 有逾期未还记录的用户禁止再次借用
 * 3. 借用/归还与设备状态联动，DAO 层用事务保证一致
 */
public class BorrowAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    private Integer equipmentId;    // 目标设备
    private Integer recordId;       // 归还目标记录
    private String dueDate;         // 应还日期（表单字符串，yyyy-MM-dd）
    private String filter = "all";  // 列表筛选：all/active/returned/overdue
    private boolean mine;           // 只看我的借用
    private Equipment equipment;    // 借用表单展示的设备
    private List<BorrowRecord> recordList;

    /** 借用记录列表（支持状态筛选、只看我的） */
    public String list() {
        Integer uid = mine ? getLoginUser().getId() : null;
        recordList = new BorrowRecordDao().list(filter, uid);
        return "list";
    }

    /** 进入借用表单：校验设备可借 + 用户无逾期 */
    public String form() {
        equipment = new EquipmentDao().findById(equipmentId == null ? 0 : equipmentId);
        if (equipment == null) {
            addActionError("设备不存在或已被删除！");
            return "deny";
        }
        if (!"在库".equals(equipment.getStatus())) {
            addActionError("该设备当前不可借用（当前状态：" + equipment.getStatus() + "）！");
            return "deny";
        }
        String deny = overdueDenyReason();
        if (deny != null) {
            addActionError(deny);
            return "deny";
        }
        return "form";
    }

    /** 提交借用：二次校验后创建记录（DAO 事务） */
    public String create() {
        equipment = new EquipmentDao().findById(equipmentId == null ? 0 : equipmentId);
        if (equipment == null || !"在库".equals(equipment.getStatus())) {
            addActionError("该设备当前不可借用，请刷新列表后重试！");
            return "deny";
        }
        String deny = overdueDenyReason();
        if (deny != null) {
            addActionError(deny);
            return "deny";
        }
        LocalDate due = DateUtil.parse(dueDate);
        if (due == null) {
            addFieldError("dueDate", "请选择应还日期！");
            return INPUT;
        }
        if (!due.isAfter(LocalDate.now())) {
            addFieldError("dueDate", "应还日期必须晚于今天！");
            return INPUT;
        }
        if (due.isAfter(LocalDate.now().plusDays(90))) {
            addFieldError("dueDate", "单次借用最长不超过 90 天！");
            return INPUT;
        }
        BorrowRecord record = new BorrowRecord();
        record.setEquipmentId(equipmentId);
        record.setUserId(getLoginUser().getId());
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(due);

        boolean ok = new BorrowRecordDao().createBorrow(record);
        if (!ok) {
            addActionError("该设备刚被他人借走，请选择其他设备！");
            return "deny";
        }
        return SUCCESS;   // redirectAction -> borrow-list
    }

    /** 归还设备（DAO 事务），之后回到借用记录列表 */
    public String doReturn() {
        if (recordId != null) {
            new BorrowRecordDao().doReturn(recordId);
        }
        return SUCCESS;   // redirectAction -> borrow-list
    }

    /** 逾期禁止再借的校验，返回拒绝原因（null=通过） */
    private String overdueDenyReason() {
        if (new BorrowRecordDao().hasOverdue(getLoginUser().getId())) {
            return "您有逾期未还的设备，请先归还后再借用！";
        }
        return null;
    }

    // ---- getter / setter ----
    public Integer getEquipmentId() { return equipmentId; }
    public void setEquipmentId(Integer equipmentId) { this.equipmentId = equipmentId; }

    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer recordId) { this.recordId = recordId; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }

    public boolean isMine() { return mine; }
    public void setMine(boolean mine) { this.mine = mine; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public List<BorrowRecord> getRecordList() { return recordList; }
    public void setRecordList(List<BorrowRecord> recordList) { this.recordList = recordList; }
}
