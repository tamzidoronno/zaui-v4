/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.accountingmanager;

import com.thundashop.core.ordermanager.data.Order;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author boggi
 */
public class AccountingSystemStatisticsResult {
    public TreeMap<String, AccountingSystemStatistics> dayresult = new TreeMap();
    public List<String> productsIncluded = new ArrayList();
    public HashMap<String, Double> allResult = new HashMap();
    public List<String> ordersUsedInReport = new ArrayList();
    public List<Long> ordersNotTransferredToAccounting = new ArrayList();

    public void generateProductsList() {
        for(AccountingSystemStatistics stat : dayresult.values()) {
            for(ProductStatiticsResult prodstat : stat.productPrices.values()) {
                if(!productsIncluded.contains(prodstat.productId)) {
                    productsIncluded.add(prodstat.productId);
                }
            }
        }
        for(String productId : productsIncluded) {
            Double total = 0.0;
            for(AccountingSystemStatistics stat : dayresult.values()) {
                for(ProductStatiticsResult prodstat : stat.productPrices.values()) {
                    if(prodstat.productId.equals(productId)) {
                        total += prodstat.totalValue;
                    }
                }
            }
            allResult.put(productId, total);
        }
    }

    public void generateListOfOrdersNotTransferred(List<Order> ordersToUse) {
        for(AccountingSystemStatistics ass : dayresult.values()) {
            for(ProductStatiticsResult prodres : ass.productPrices.values()) {
                for(String orderId : prodres.ordervalues.keySet()) {
                    if(!ordersUsedInReport.contains(orderId)) {
                        ordersUsedInReport.add(orderId);
                    }
                }
            }
        }
        
        HashMap<String, Order> orderIds = new HashMap();
        for(Order ord : ordersToUse) {
            orderIds.put(ord.id, ord);
        }
        
        for(String ordId : ordersUsedInReport) {
            Order ord = orderIds.get(ordId);
            if(!ord.transferredToAccountingSystem && ord.getTotalAmount() != 0.0) {
                ordersNotTransferredToAccounting.add(ord.incrementOrderId);
            }
        }
    }
}
