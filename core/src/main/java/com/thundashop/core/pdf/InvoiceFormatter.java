/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.data.AccountingDetails;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import org.joda.time.DateTime;

/**
 *
 * @author boggi
 */
public class InvoiceFormatter {

    private String base;
    private User user;
    private Order order;
    private AccountingDetails accountingDetails;
    
    public InvoiceFormatter(String base) {
        this.base = base;
    }
    
    public void replaceVariables() {
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        int tmpRest = (int)(order.getTotalAmount()*100-Math.floor(order.getTotalAmount())*100);
        String rest = tmpRest + "";
        if(tmpRest < 10) { rest = "0" + tmpRest; }
        
        if(user!=null) {
            this.base = this.base.replace("{fullName}", user.fullName);
        } else {
            this.base = this.base.replace("{fullName}", "");
        }
        if(accountingDetails.logo != null) {
            this.base = this.base.replace("{logo}", "<img src='"+accountingDetails.logo+"'></img>");
        } else {
            this.base = this.base.replace("{logo}", "");
        }
        if(accountingDetails.swift != null) {
            this.base = this.base.replace("{swift}", accountingDetails.swift);
        } else {
            this.base = this.base.replace("{swift}", "");
        }
        if(accountingDetails.iban != null) {
            this.base = this.base.replace("{iban}", accountingDetails.iban);
        } else {
            this.base = this.base.replace("{iban}", "");
        }
        
        if(order.cart.address != null) {
            if(order.cart.address.address != null && !order.cart.address.address.isEmpty()) {
                this.base = this.base.replace("{addressaddress}", order.cart.address.address);
            }
            if(order.cart.address.postCode != null && !order.cart.address.postCode.isEmpty()) {
                this.base = this.base.replace("{addresspostCode}", order.cart.address.postCode);
            }
            if(order.cart.address.co != null && !order.cart.address.co.isEmpty()) {
                this.base = this.base.replace("{co}", "C/O " + order.cart.address.co + "<br>");
            } else {
                this.base = this.base.replace("{co}", "");
            }
            
            if(order.cart.address.city != null && !order.cart.address.city.isEmpty()) {
                this.base = this.base.replace("{addresscity}", order.cart.address.city);
            }
            if(order.cart.address.countryname != null && !order.cart.address.countryname.isEmpty()) {
                this.base = this.base.replace("{addresscountryname}", order.cart.address.countryname);
            }
        }

        if(user != null && user.address != null) {
            if(user.address.address != null) { this.base = this.base.replace("{addressaddress}", user.address.address); }
            if(user.address.postCode != null) { this.base = this.base.replace("{addresspostCode}", user.address.postCode); }
            if(user.address.city != null) { this.base = this.base.replace("{addresscity}", user.address.city); }
            if(user.address.countryname != null) { this.base = this.base.replace("{addresscountryname}", user.address.countryname); }
        }
        
        this.base = this.base.replace("{addressaddress}", "");
        this.base = this.base.replace("{addresspostCode}", "");
        this.base = this.base.replace("{addresscity}", "");
        this.base = this.base.replace("{addresscountryname}", "");
        
        
        this.base = this.base.replace("{companyName}", accountingDetails.companyName);
        this.base = this.base.replace("{vatNumber}", accountingDetails.vatNumber);
        this.base = this.base.replace("{address}", accountingDetails.address);
        this.base = this.base.replace("{city}", accountingDetails.city);
        this.base = this.base.replace("{postCode}", accountingDetails.postCode);
        this.base = this.base.replace("{contactEmail}", accountingDetails.contactEmail);
        this.base = this.base.replace("{rowCreatedDate}", sdf.format(order.rowCreatedDate));
        this.base = this.base.replace("{dueDate}", sdf.format(order.getDueDate()));
        this.base = this.base.replace("{accountNumber}", accountingDetails.accountNumber);
        this.base = this.base.replace("{invoiceNote}", order.invoiceNote);
        this.base = this.base.replace("{incrementOrderId}", order.incrementOrderId + "");
        this.base = this.base.replace("{currency}", accountingDetails.currency);
        this.base = this.base.replace("{kid}", order.kid);
        this.base = this.base.replace("{netAmount}", ((double)(Math.round((order.getTotalAmount()-order.getTotalAmountVat())* 100)) / 100) + "");
        this.base = this.base.replace("{total}", ((double)(Math.round(order.getTotalAmount()*100))/100) + "");
        this.base = this.base.replace("{totalFloor}", (int)Math.floor(order.getTotalAmount()) + "");
        this.base = this.base.replace("{totalRest}", rest);
    }
    
    public void setOrderLines() {
        String lines = "";
        
        for(CartItem item : order.cart.getItems()) {
            String text = "";
            text += "<div class='orderitem'>";
            text += "<span class='orderitemdescription'>" + getItemText(item) + "</span>";
            text += "<span class='orderitemcount'>" + item.getCount() + "</span>";
            text += "<span class='orderitemprice'>" + Math.round(item.getProduct().priceExTaxes*100)/100 + "</span>";
            text += "<span class='orderitemtax'>" + item.getProduct().taxGroupObject.taxRate + "%</span>";
            text += "<span class='orderitemtotal'>" + item.getProduct().price + " " + accountingDetails.currency + "</span>";
            text += "</div>";
            lines += text;
        }
        if(order.cart.getItems().size() > 20) {
            this.base = this.base.replace("{itemLinesLarge}", "<div class='page2'>" + lines + "</div>");
            this.base = this.base.replace("{itemLines}", "See attachment(s) for more information.");
        } else {
            this.base = this.base.replace("{itemLinesLarge}", "");
            this.base = this.base.replace("{itemLines}", lines);
        }
    }
    
    public void setTaxesLines() {
        HashMap<Double, Double> taxes = new HashMap();
        for(CartItem item : order.cart.getItems()) {
            double current = 0.0;
            if(!taxes.containsKey(item.getProduct().taxGroupObject.taxRate)) {
                taxes.put(item.getProduct().taxGroupObject.taxRate, current);
            }
            current = taxes.get(item.getProduct().taxGroupObject.taxRate);
            current += (item.getProduct().price - item.getProduct().priceExTaxes) * item.getCount();
            taxes.put(item.getProduct().taxGroupObject.taxRate, current);
        }
        
        String lines = "";
        for(Double rate : taxes.keySet()) {
            Double amount = taxes.get(rate);
            amount = (double)Math.round(amount * 100) / 100;
            lines += "<div class='summaryline'><span class='summaryleft'>"+getTranslation("TAX")+ " " + rate + "%</span><span class='summaryright'>" + amount + " " + accountingDetails.currency + "</span></div>";
        }
        this.base = this.base.replace("{taxLines}", lines);
    }
    
    
    public String getBase() {
        return this.base;
    } 

    void setUser(User user) {
        this.user = user;
    }

    void setOrder(Order order) {
        this.order = order;
    }

    void setAccountingDetails(AccountingDetails accountingDetails) {
        this.accountingDetails = accountingDetails;
    }

    public String getItemText(CartItem item) {
        if(item == null) {
            return "";
        }
        
        String lineText = "";
        String startDate = "";
        if(item.startDate != null) {
            DateTime start = new DateTime(item.startDate);
            startDate = start.toString("dd.MM.yy");
        }

        String endDate = "";
        if(item.endDate != null) {
            DateTime end = new DateTime(item.endDate);
            endDate = end.toString("dd.MM.yy");
        }
        
        lineText = "";
        if(item.getProduct() != null && 
                item.getProduct().additionalMetaData != null && 
                !item.getProduct().additionalMetaData.isEmpty()) {
            lineText = item.getProduct().additionalMetaData; 
        }
        
        lineText += " " + item.getProduct().name;
        
        if(item.getProduct() != null && item.getProduct().metaData != null && !item.getProduct().metaData.isEmpty()) {
            lineText += " " + item.getProduct().metaData;
        }
        if(!startDate.isEmpty() && !endDate.isEmpty() && !item.hideDates) {
            lineText += " (" + startDate + " - " + endDate + ")";
        }
        
        return lineText;
    }

    private String getTranslation(String key) {
        return key;
    }

    void setTranslation(String lang) {
        InvoiceLanguages invoicelang = new InvoiceLanguages();
        HashMap<String, String> languageMatrix = invoicelang.getLanguageMatrix(lang);

        for(String key : languageMatrix.keySet()) {
            if(!languageMatrix.get(key).isEmpty()) {
                base = base.replace("<text>" + key + "</text>", languageMatrix.get(key));
            } else {
                base = base.replace("<text>" + key + "</text>", key);
            }
        }
    }

    void setTotalLines() {
        this.base = this.base.replace("{totalAmount}", ""+order.getTotalAmount());
    }

}
