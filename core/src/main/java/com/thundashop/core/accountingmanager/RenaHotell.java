package com.thundashop.core.accountingmanager;

import com.thundashop.core.common.ForStore;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@ForStore(storeId="87cdfab5-db67-4716-bef8-fcd1f55b770b")
public class RenaHotell implements AccountingInterface {

    private UserManager userManager;

    @Override
    public void setUserManager(UserManager manager) {
        this.userManager = manager;
    }

    @Override
    public List<String> createUserFile(List<User> users) {
        return new ArrayList();
    }

    @Override
    public List<String> createOrderFile(List<Order> orders) {
        HashMap<Integer, String> fieldsInLine = new HashMap();
        
        SimpleDateFormat format1 = new SimpleDateFormat("yyMMdd");
        
        for(Order order : orders) {
            fieldsInLine.put(1, "97");
            fieldsInLine.put(2, format1.format(order.rowCreatedDate));
            fieldsInLine.put(3, order.incrementOrderId + "");
            fieldsInLine.put(4, format1.format(order.rowCreatedDate));
            fieldsInLine.put(6, order.invoiceNote);
            fieldsInLine.put(9, "10"); //Debet konto
            fieldsInLine.put(10, "12"); //Kredit konto
            fieldsInLine.put(11, "25");
            fieldsInLine.put(12, "NOK"); //Valutta
        }
        
        return new ArrayList();
    }
    
}
