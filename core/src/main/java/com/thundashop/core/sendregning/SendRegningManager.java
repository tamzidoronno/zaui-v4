package com.thundashop.core.sendregning;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.webmanager.WebManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class SendRegningManager extends ManagerBase implements ISendRegningManager {
    @Autowired
    WebManager webManager;
    
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    UserManager userManager;
    
    @Override
    public String sendOrder(String orderId) {
        if(pushToSendRegning(orderId)) {
            return "";
        } else {
            return "failed to push to sendregning server.";
        }
    }

    private boolean pushToSendRegning(String orderId) {
        String url = "";
        NewInvoice inv = createInvoice(orderId);
        Gson gson = new Gson();
        String data = gson.toJson(inv);
        String usernamepassword = "";
        try {
            String res = webManager.htmlPostBasicAuth(url, data, true, "UTF-8", usernamepassword, "Basic", true, "POST");
        }catch(Exception e) {
            return false;
        }
        return false;
    }

    private NewInvoice createInvoice(String orderId) {
        Order order = orderManager.getOrderSecure(orderId);
        
        NewInvoice inv = new NewInvoice();
        inv.InvoiceDate = formatDate(order.rowCreatedDate);
        inv.deliveryAddress = getDeliveryAdress(order);
        inv.deliveryDate = formatDate(order.rowCreatedDate);
        inv.invoiceText = order.invoiceNote;
        inv.items = createItemsList(order);
        inv.orderDate = formatDate(order.rowCreatedDate);
        inv.orderNumber = order.incrementOrderId + "";
        inv.ourReference = order.incrementOrderId + "";
        inv.yourReference = "";
        inv.dueDate = formatDate(createDueDate(order));
        inv.recipient = createRecipient(order);
        return inv;
    }

    private String formatDate(Date date) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        return format1.format(date);
    }

    private NewInvoiceAddress getDeliveryAdress(Order order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private List<NewInvoiceItem> createItemsList(Order order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private NewInvoiceRecipient createRecipient(Order order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private Date createDueDate(Order order) {
        int dueDays = 14;
        Calendar cal = Calendar.getInstance();
        if(order.dueDays != null) {
            dueDays = order.dueDays;
        }
        cal.add(Calendar.DAY_OF_YEAR, dueDays);
        return cal.getTime();
    }    
}
