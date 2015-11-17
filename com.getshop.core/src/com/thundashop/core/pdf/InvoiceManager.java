/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

import com.thundashop.core.cartmanager.data.CartItem;
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
import com.thundashop.core.productmanager.data.Product;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class InvoiceManager extends ManagerBase implements IInvoiceManager {
    @Autowired
    public MailFactory mailFactory;
    
    @Autowired
    public InvoiceManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    
    @Override
    public void createInvoice(String orderId) throws ErrorException { 
        OrderManager orderManager = getManager(OrderManager.class);
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
        PageManager pageManager = getManager(PageManager.class);
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
    
    
     @Override
    public String getBase64EncodedInvoice(String orderId) throws ErrorException {
        OrderManager orderManager = getManager(OrderManager.class);
        Order order = orderManager.getOrder(orderId);

        AccountingDetails details = getAccountingDetails();

        order = order.jsonClone();
        InvoiceGenerator generator = new InvoiceGenerator(order, details);
        
        try {
            String file = generator.createInvoice();
            String base64 = encodeFileToBase64Binary(file);
            File javaFile = new File(file);
            javaFile.delete();
            return base64;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return "";
    }
    
    
    private String encodeFileToBase64Binary(String fileName) throws IOException {
 
        File file = new File(fileName);
        byte[] bytes = loadFile(file);
        byte[] encoded = Base64.encodeBase64(bytes);
        String encodedString = new String(encoded);

        return encodedString;
    }
    
    public static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
        }
        
        byte[] bytes = new byte[(int)length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    } 
}