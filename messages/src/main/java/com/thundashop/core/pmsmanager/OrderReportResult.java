/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author boggi
 */
public class OrderReportResult {
    
    
    List<OrderReportResultRow> rows = new ArrayList();
    
    public void addRow(Date date, long incId, double priceMatrixPrice, CartItem item, String orderId, Date overrideDate) {
        OrderReportResultRow row = new OrderReportResultRow();
        row.day = date;
        row.incrementOrderId = incId;
        row.price = item.getProductPrice();
        
        row.priceMatrixPriceExTax = priceMatrixPrice/((item.getProduct().taxGroupObject.taxRate/100)+1);
        row.count = item.getCount();
        row.priceMatrixPrice = priceMatrixPrice;
        row.start = item.startDate;
        row.end = item.endDate;
        row.orderId = orderId;
        row.overrideDate = overrideDate;
        rows.add(row);
    }
    
}
