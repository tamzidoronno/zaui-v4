package com.thundashop.core.pmsmanager;

import com.thundashop.core.ordermanager.data.Order;
import java.util.LinkedList;

public class PmsSubscriptionReportEntry {
    public String itemName = "";
    public String owner = "";
    public LinkedList<Order> orders = new LinkedList();
    
    @Override
    public String toString() {
        String res = itemName + "\t" + owner + "\t";
        for(Order ord : orders) {
            res += ord.incrementOrderId + "\t";
        }
        return res;
    }
}
