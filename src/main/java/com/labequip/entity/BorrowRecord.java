package com.labequip.entity;

import com.labequip.util.DateUtil;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 设备借用记录实体类
 * status 取值：借出中 / 已归还
 * 逾期 = 未归还 且 应还日期早于今天（实时计算，不落库）
 */
public class BorrowRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int equipmentId;
    private int userId;
    private LocalDate borrowDate;   // 借出日期
    private LocalDate dueDate;      // 应还日期
    private LocalDate returnDate;   // 实际归还日期（null=未还）
    private String status;          // 借出中 / 已归还

    // ---- 以下为关联查询的展示字段（非表字段） ----
    private String equipmentName;   // 设备名称
    private String equipmentModel;  // 设备型号
    private String equipmentImage;  // 设备图片路径（关联查询）
    private String borrowerName;    // 借用人姓名
    private String studentNo;       // 借用人学号

    /** 是否逾期未还 */
    public boolean isOverdue() {
        return returnDate == null && dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    /** 归还时是否已逾期（逾期归还） */
    public boolean isReturnedLate() {
        return returnDate != null && dueDate != null && returnDate.isAfter(dueDate);
    }

    /** 列表展示用状态文字 */
    public String getDisplayStatus() {
        if ("已归还".equals(status)) {
            return isReturnedLate() ? "逾期归还" : "按期归还";
        }
        return isOverdue() ? "已逾期" : "借出中";
    }

    /** 逾期天数（仅逾期未还时 > 0） */
    public long getDaysOverdue() {
        return isOverdue() ? ChronoUnit.DAYS.between(dueDate, LocalDate.now()) : 0;
    }

    /** 距应还日剩余天数（负数=已过期） */
    public long getDaysLeft() {
        return dueDate == null ? 0 : ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    // ---- 日期显示字符串（供 JSP 使用） ----
    public String getBorrowDateStr() { return DateUtil.fmt(borrowDate); }
    public String getDueDateStr() { return DateUtil.fmt(dueDate); }
    public String getReturnDateStr() { return DateUtil.fmt(returnDate); }

    // ---- getter / setter ----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEquipmentId() { return equipmentId; }
    public void setEquipmentId(int equipmentId) { this.equipmentId = equipmentId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

    public String getEquipmentModel() { return equipmentModel; }
    public void setEquipmentModel(String equipmentModel) { this.equipmentModel = equipmentModel; }

    public String getEquipmentImage() { return equipmentImage; }
    public void setEquipmentImage(String equipmentImage) { this.equipmentImage = equipmentImage; }

    /** 页面展示用：未设置图片时返回默认占位图 */
    public String getEquipmentImageDisplay() {
        return (equipmentImage == null || equipmentImage.trim().isEmpty())
                ? "images/equip/default.jpg" : equipmentImage.trim();
    }

    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
}
