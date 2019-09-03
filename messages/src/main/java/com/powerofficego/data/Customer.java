package com.powerofficego.data;

import com.thundashop.core.usermanager.data.User;

public class Customer {
    public String code;
    public String name;
    public String vatNumber;
    public String emailAddress;
    public String id;
    public Address mailAddress;
    
    public void setUser(User user) {
        name = user.fullName;
        if(name == null || name.isEmpty()) {
            name = "Unkonwn name";
        }
        code = user.accountingId + "";
        emailAddress = user.emailAddress;
        if(user.emailAddressToInvoice != null && !user.emailAddressToInvoice.isEmpty()) {
            emailAddress = user.emailAddressToInvoice;
        }
        if(user.companyObject != null) {
            vatNumber = user.companyObject.vatNumber;
        }
        if(user.externalAccountingId != null && !user.externalAccountingId.isEmpty()) {
            id = user.externalAccountingId;
        }
        
        mailAddress = new Address();
        mailAddress.address1 = user.address.address;
        mailAddress.city = user.address.city;
        mailAddress.zipCode = user.address.postCode;
        mailAddress.countryCode = user.address.countrycode;
        mailAddress.id = 1;
        mailAddress.isPrimary = true;
        
        if(mailAddress.countryCode == null || mailAddress.countryCode.isEmpty()) {
            mailAddress.countryCode = "NO";
        }
        
    }
}
