/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting.fikenservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class FikenInvoiceCreateRequest {
    private String issueDate;
    private String dueDate;
    private String uuid;
    private String invoiceText;
    private String bankAccountUrl;
    private String ourReference;
    private String yourReference = "";
    private FikenInvoiceCreateRequest.Customer customer;
    private boolean cash = true;
    private String paymentAccount = "";
    private List<FikenInvoiceCreateRequest.OrderLine> lines = new ArrayList();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    public void setIssueDate(Date issueDate) {
        this.issueDate = sdf.format(issueDate);
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = sdf.format(dueDate);
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getDueDate() {
        return dueDate;
    }
    

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getInvoiceText() {
        return invoiceText;
    }

    public void setInvoiceText(String invoiceText) {
        this.invoiceText = invoiceText;
    }

    public String getBankAccountUrl() {
        return bankAccountUrl;
    }

    public void setBankAccountUrl(String bankAccountUrl) {
        this.bankAccountUrl = bankAccountUrl;
    }

    public String getOurReference() {
        return ourReference;
    }

    public void setOurReference(String ourReference) {
        this.ourReference = ourReference;
    }

    public String getYourReference() {
        return yourReference;
    }

    public void setYourReference(String yourReference) {
        this.yourReference = yourReference;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public boolean isCash() {
        return cash;
    }

    public void setCash(boolean cash) {
        this.cash = cash;
    }

    public String getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public void setLines(List<OrderLine> lines) {
        this.lines = lines;
    }

    class OrderLine {
        private int unitNetAmount;
        private int netAmount;
        private int vatAmount;
        private int discountPercent;
        private String vatType = "HIGH";
        private String description = "";
        private String productUrl = "";
        private int grossAmount;
        private int quantity;
        private int incomeAccount = 3000;

        public int getIncomeAccount() {
            return incomeAccount;
        }

        public void setIncomeAccount(int incomeAccount) {
            this.incomeAccount = incomeAccount;
        }

        public int getUnitNetAmount() {
            return unitNetAmount;
        }

        public void setUnitNetAmount(int unitNetAmount) {
            this.unitNetAmount = unitNetAmount;
        }

        public int getNetAmount() {
            return netAmount;
        }

        public void setNetAmount(int netAmount) {
            this.netAmount = netAmount;
        }

        public int getVatAmount() {
            return vatAmount;
        }

        public void setVatAmount(int vatAmount) {
            this.vatAmount = vatAmount;
        }

        public double getDiscountPercent() {
            return discountPercent;
        }

        public void setDiscountPercent(int discountPercent) {
            this.discountPercent = discountPercent;
        }

        public String getVatType() {
            return vatType;
        }

        public void setVatType(String vatType) {
            this.vatType = vatType;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getProductUrl() {
            return productUrl;
        }

        public void setProductUrl(String productUrl) {
            this.productUrl = productUrl;
        }

        public double getGrossAmount() {
            return grossAmount;
        }

        public void setGrossAmount(int grossAmount) {
            this.grossAmount = grossAmount;
        }  

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        
        
    }
    
    class Customer {
        private String url = "";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}