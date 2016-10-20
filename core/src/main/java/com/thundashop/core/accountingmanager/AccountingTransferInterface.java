package com.thundashop.core.accountingmanager;

import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.User;
import java.util.List;

public interface AccountingTransferInterface {
    public void setManagers(AccountingManagers managers);
    public void setUsers(List<User> users);
    public void setOrders(List<Order> orders);
    public void setConfig(AccountingTransferConfig config);
    public SavedOrderFile generateFile();
}
