package com.thundashop.core.accountingmanager;

import com.thundashop.core.ftpmanager.FtpManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.usermanager.UserManager;

public class AccountingManagers {
    public OrderManager orderManager;
    public UserManager userManager;
    public FtpManager ftpManager;
    public InvoiceManager invoiceManager; 
    public ProductManager productManager; 
}
