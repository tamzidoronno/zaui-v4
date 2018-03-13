/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.thundashop.core.usermanager.data.User;

/**
 *
 * @author boggi
 */
public class VismaeaccountingCustomer {
    public String Id;
    public String CustomerNumber;
    public String CorporateIdentityNumber;
    public String ContactPersonEmail;
    public String ContactPersonMobile;
    public String ContactPersonName;
    public String ContactPersonPhone;
    public String CurrencyCode = "NOK";
    public String GLN;
    public String EmailAddress;
    public String InvoiceAddress1;
    public String InvoiceAddress2;
    public String InvoiceCity;
    public String InvoiceCountryCode;
    public String InvoicePostalCode;
    public String DeliveryCustomerName;
    public String DeliveryAddress1;
    public String DeliveryAddress2;
    public String DeliveryCity;
    public String DeliveryCountryCode;
    public String DeliveryPostalCode;
    public String DeliveryMethodId;
    public String DeliveryTermId;
    public String Name;
    public String Note;
    public boolean ReverseChargeOnConstructionServices = false;
    public Integer WebshopCustomerNumber;
    public String MobilePhone;
    public String Telephone;
    public String TermsOfPaymentId = "F9168C5E-CEB2-4faa-B6BF-329BF39FA1E4";
    public String VatNumber;
    public boolean IsPrivatePerson;
    public boolean IsActive = true;
    public String OrgNr;
    public VismaTermsOfPayment termsOfPayment = new VismaTermsOfPayment();

    void setUser(User user) {
        CustomerNumber = user.customerId + "";
        EmailAddress = user.emailAddress;
        if(user.address != null) {
            InvoiceAddress1 = user.address.address;
            InvoiceCity = user.address.city;
            InvoiceCountryCode = user.address.countrycode;
            InvoicePostalCode = user.address.postCode;
        }
        Name = user.fullName;
        IsPrivatePerson = true;
        if(user.companyObject != null) {
            IsPrivatePerson = false;
            OrgNr = user.companyObject.vatNumber;
        }
        
        if(InvoiceCountryCode == null || InvoiceCountryCode.isEmpty()) {
            InvoiceCountryCode = "NO";
        }
        
        if(InvoiceAddress1 != null && InvoiceAddress1.length() >49) {
            InvoiceAddress1 = InvoiceAddress1.substring(0,49);
        }
        
        MobilePhone = user.cellPhone;
        
    }

    String getInvalidText() {
        String invalidText = "";
        if(InvoiceAddress1 == null || InvoiceAddress1.trim().isEmpty()) {
            invalidText += "Invoice address invalid ";
        }
        if(InvoiceCity == null || InvoiceCity.trim().isEmpty()) {
            invalidText += "Invoice city invalid ";
        }
        if(InvoicePostalCode == null || InvoicePostalCode.trim().isEmpty()) {
            invalidText += "Postal code is invalid ";
        }
        if(Name == null || Name.trim().isEmpty()) {
            invalidText += "Postal code is invalid ";
        }
        if(EmailAddress == null || EmailAddress.trim().isEmpty()) {
            invalidText += "Email code is invalid ";
        }
        return invalidText;
    }
}
