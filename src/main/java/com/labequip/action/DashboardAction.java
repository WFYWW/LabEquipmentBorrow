package com.labequip.action;

import com.labequip.dao.BorrowRecordDao;
import com.labequip.dao.EquipmentDao;
import com.labequip.entity.BorrowRecord;

import java.util.List;

/**
 * 首页仪表盘：统计卡片 + 逾期提醒 + 即将到期 + 我的在借
 */
public class DashboardAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    private int equipmentCount;     // 设备总数
    private int availableCount;     // 在库数量
    private int borrowedCount;      // 借出数量
    private int myActiveCount;      // 我的在借数量

    private List<BorrowRecord> overdueList;   // 全站逾期未还
    private List<BorrowRecord> dueSoonList;   // 3天内即将到期
    private List<BorrowRecord> myRecords;     // 我的在借

    @Override
    public String execute() {
        EquipmentDao equipmentDao = new EquipmentDao();
        BorrowRecordDao recordDao = new BorrowRecordDao();

        equipmentCount = equipmentDao.countAll();
        availableCount = equipmentDao.countByStatus("在库");
        borrowedCount = equipmentDao.countByStatus("借出");
        myActiveCount = recordDao.countActiveByUser(getLoginUser().getId());

        overdueList = recordDao.listOverdue();
        dueSoonList = recordDao.listDueSoon(3);
        myRecords = recordDao.listActiveByUser(getLoginUser().getId());
        return SUCCESS;
    }

    // ---- getter（供 JSP / OGNL 访问） ----
    public int getEquipmentCount() { return equipmentCount; }
    public int getAvailableCount() { return availableCount; }
    public int getBorrowedCount() { return borrowedCount; }
    public int getMyActiveCount() { return myActiveCount; }

    public List<BorrowRecord> getOverdueList() { return overdueList; }
    public List<BorrowRecord> getDueSoonList() { return dueSoonList; }
    public List<BorrowRecord> getMyRecords() { return myRecords; }
}
