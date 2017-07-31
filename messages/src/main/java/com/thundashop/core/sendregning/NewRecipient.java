
package com.thundashop.core.sendregning;

public class NewRecipient {
    public Integer number;
    public String customerNumber;
    public String name;
    public String email;
    public String organisationNumber;
    public NewInvoiceRecipientContact contact = new NewInvoiceRecipientContact();
    public NewInvoiceRecipientAdress address = new NewInvoiceRecipientAdress();
}
