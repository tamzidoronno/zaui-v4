/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pdf.data.AccountingDetails;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class InvoiceManager extends ManagerBase implements IInvoiceManager {
    @Autowired
    public MailFactory mailFactory;
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private PageManager pageManager;
    
    @Override
    public void createInvoice(String orderId) throws ErrorException { 
        Order order = orderManager.getOrder(orderId);
        
        AccountingDetails details = getAccountingDetails();
        
        InvoiceGenerator generator = new InvoiceGenerator(order, details);
        try {
            String file = generator.createInvoice();
            Map<String, String> files = new HashMap();
            files.put(file, "Faktura "+order.incrementOrderId+".pdf");
            mailFactory.sendWithAttachments("post@getshop.com", order.cart.address.emailAddress, "Faktura " + order.incrementOrderId, "Vedlagt ligger fakturaen for din bestilling.", files, true);
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }

    private AccountingDetails getAccountingDetails() throws ErrorException {
        Map<String, Setting> settings = pageManager.getApplicationSettings("InvoicePayment");
        
        AccountingDetails details = new AccountingDetails();
        details.accountNumber = settings.get("accountNumber").value;
        details.address = settings.get("address").value;
        details.city = settings.get("city").value;
        details.companyName = settings.get("companyName").value;
        details.contactEmail = settings.get("contactEmail").value;
        details.dueDays = Integer.parseInt(settings.get("duedays").value);
        details.vatNumber = settings.get("vatNumber").value;
        details.webAddress = settings.get("webAddress").value;
        
        return details;
    }
}