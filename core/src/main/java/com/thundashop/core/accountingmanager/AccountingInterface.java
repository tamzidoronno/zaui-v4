
package com.thundashop.core.accountingmanager;

import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.HashMap;
import java.util.List;

public interface AccountingInterface {
    public void setUserManager(UserManager manager);
    public void setInvoiceManager(InvoiceManager manager);
    public void setOrderManager(OrderManager manager);
    public List<String> createUserFile(List<User> users);
    public List<String> createOrderFile(List<Order> orders, String type);
}
