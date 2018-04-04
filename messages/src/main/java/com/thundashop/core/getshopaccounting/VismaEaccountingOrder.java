package com.thundashop.core.getshopaccounting;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.User;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VismaEaccountingOrder {
    public String Id;
    public Double Amount;
    public Double VatAmount;
    public String customerId;
    public String currencyCode = "NOK";
    public String OrderDate;
    public Double RoundingsAmount = 0.0;
    public boolean EuThirdParty = true;
    public Integer status = 1;
    public List<VismaEaccountingOrderLine> Rows = new ArrayList();
    public boolean CustomerIsPrivatePerson = true;
    public Integer RotReducedInvoicingType = 0;
    public Integer RotPropertyType = 0;
    public boolean ReverseChargeOnConstructionServices = true;
    
    public String InvoiceCountryCode = "";
    public String InvoiceCustomerName = "";
    public String InvoicePostalCode = "";
    public String InvoiceAddress1 = "";
    public String InvoiceCity = "";
    public String DeliveryPostalCode = "NO";

    VismaEaccountingOrder(Order order, String customerId) {
        DecimalFormat df = new DecimalFormat("#.##"); 
        Amount = VismaEaccountingOrderLine.round(order.getTotalAmount(),2);
        VatAmount = VismaEaccountingOrderLine.round(order.getTotalAmountVat(),2);
        SimpleDateFormat dateFormatGmt1 = new SimpleDateFormat("yyyy-MM-dd");
        OrderDate = dateFormatGmt1.format(order.rowCreatedDate);
        this.customerId = customerId;
    }

    void setInvoiceUser(User user) {
        InvoiceCountryCode = "NO";
        InvoiceCustomerName = user.fullName;
        InvoicePostalCode = user.address.postCode;
        InvoiceAddress1 = user.address.address;
        InvoiceCity = user.address.city;
        if(InvoiceAddress1.length() > 50) {
            InvoiceAddress1 = InvoiceAddress1.substring(0, 49);
        }
        
    
        if(InvoiceAddress1 == null || InvoiceAddress1.trim().isEmpty()) {
            InvoiceAddress1 = "unknown";
        }
        if(InvoiceCity == null || InvoiceCity.trim().isEmpty()) {
            InvoiceCity = "Oslo";
        }
        if(InvoiceCountryCode == null || InvoiceCountryCode.trim().isEmpty()) {
            InvoiceCountryCode = "NO";
        }
        if(InvoicePostalCode == null || InvoicePostalCode.trim().isEmpty()) {
            InvoicePostalCode = "0001";
        }
        
        
    }
}
