/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf.data;

import java.io.Serializable;

/**
 *
 * @author ktonder
 */
public class AccountingDetails implements Serializable {
    public String accountNumber = "";
    public String vatNumber = "";
    public String address = "";
    public String companyName = "";
    public String postCode = "";
    public String city = "";
    public String contactEmail = "";
    public String webAddress = "";
    public int dueDays = 14;
    public String kidType = "";
    public Integer kidSize = 0;
    public String currency = "";
    public String type = "type1";
    public String iban = "";
    public String swift = "";
    public String useLanguage = "";
    public String bankName = "";
    public String logo = "";
    public String phoneNumber;
    public String language = "";
    public String unpaidinvoicetext = "";

    public boolean isTypeOne() {
        if (type == null || type.isEmpty() || type.equals("type1"))
            return true;
        
        return false;
    }

    public boolean isTypeTwo() {
        if (type != null && !type.isEmpty() && type.equals("type2"))
            return true;
        
        return false;
    }

    public String getPhpTemplate() {
        if (type == null) {
            return null;
        }
        
        if (type.startsWith("php")) {
            String[] templatearr = type.split("_");
            return templatearr[1];
        }
        
        return null;
    }
}
