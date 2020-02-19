package com.thundashop.core.accountingmanager;

import com.thundashop.core.ftpmanager.FtpManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.paymentmanager.PaymentManager;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.webmanager.WebManagerSSLUnsafe;

public class AccountingManagers {
    public OrderManager orderManager;
    public UserManager userManager;
    public FtpManager ftpManager;
    public InvoiceManager invoiceManager; 
    public ProductManager productManager;
    public WebManagerSSLUnsafe webManager;
    public PaymentManager paymentManager;
    public boolean productMode = false;
}
