/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.data.AccountingDetails;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private StoreApplicationPool storeApplicationPool;

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
        Application settings = storeApplicationPool.getApplication("70ace3f0-3981-11e3-aa6e-0800200c9a66");
        AccountingDetails details = new AccountingDetails();
        details.accountNumber = settings.getSetting("accountNumber");
        details.address = settings.getSetting("address");
        details.city = settings.getSetting("city");
        details.companyName = settings.getSetting("companyName");
        details.contactEmail = settings.getSetting("contactEmail");
        details.dueDays = Integer.parseInt(settings.getSetting("duedays"));
        details.vatNumber = settings.getSetting("vatNumber");
        details.webAddress = settings.getSetting("webAddress");

        return details;
    }

    @Override
    public String getBase64EncodedInvoice(String orderId) {
        Order order = orderManager.getOrder(orderId);

        AccountingDetails details = getAccountingDetails();

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

    private static byte[] loadFile(File file) throws IOException {
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
