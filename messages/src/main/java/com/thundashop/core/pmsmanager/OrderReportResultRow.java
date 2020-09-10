/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author boggi
 */
public class OrderReportResultRow {
    public String orderId;
    public long incrementOrderId;
    public Date day;
    public Double price;
    public int count;
    public double priceMatrixPrice;
    public Date start;
    public Date end;
    public double priceMatrixPriceExTax;
    public Date overrideDate;
}
