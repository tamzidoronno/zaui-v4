package com.thundashop.core.sendregning;

import java.util.List;

public class NewInvoice {
    String orderNumber;
    String InvoiceDate;
    String dueDate;
    String orderDate;
    String ourReference;
    String yourReference;
    String invoiceText;
    String deliveryDate;
    NewInvoiceAddress deliveryAddress;
    NewInvoiceRecipient recipient;
    NewInvoiceShipment shipment = new NewInvoiceShipment();
    List<NewInvoiceItem> items;
    
}
