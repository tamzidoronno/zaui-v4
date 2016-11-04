package com.powerofficego.data;

import com.thundashop.core.usermanager.data.User;

public class Customer {
    public String code;
    public String name;
    public String vatNumber;
    public String id;
    public String emailAddress;
    
    public void setUser(User user) {
        name = user.fullName;
        code = user.customerId + "";
        if(user.accountingId != null && !user.accountingId.isEmpty()) {
            id = user.accountingId;
        }
        emailAddress = user.emailAddress;
        if(user.emailAddressToInvoice != null && !user.emailAddressToInvoice.isEmpty()) {
            emailAddress = user.emailAddressToInvoice;
        }
        if(user.companyObject != null) {
            vatNumber = user.companyObject.vatNumber;
        }
    }
}
