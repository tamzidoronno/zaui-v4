package com.thundashop.core.sendregning;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.webmanager.WebManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    
    @Autowired
    StoreApplicationPool storeApplicationPool;
    
    String root = "https://www.sendregning.no/";
    
    @Override
    public String sendOrder(String orderId) {
        if(createInvoiceAtSendRegning(orderId)) {
            return "";
        } else {
            return "failed to push to sendregning server.";
        }
    }

    private boolean createInvoiceAtSendRegning(String orderId) {
        Order order = orderManager.getOrderSecure(orderId);
        User user = userManager.getUserById(order.userId);
        createUpdateRecipient(user);
        String url = root+"invoices/";
        NewInvoice inv = createInvoice(orderId);
        Gson gson = new Gson();
        String data = gson.toJson(inv);
        
        return pushMessage(url, data, "POST");
        
    }

    private NewInvoice createInvoice(String orderId) {
        Order order = orderManager.getOrderSecure(orderId);
        User user = userManager.getUserById(order.userId);
        
        NewInvoice inv = new NewInvoice();
        inv.InvoiceDate = formatDate(order.rowCreatedDate);
        inv.deliveryAddress = createAddressOnOrder(order);
        inv.deliveryDate = formatDate(order.rowCreatedDate);
        inv.invoiceText = order.invoiceNote;
        inv.items = createItemsList(order);
        inv.orderDate = formatDate(order.rowCreatedDate);
        inv.orderNumber = order.incrementOrderId + "";
        inv.ourReference = order.incrementOrderId + "";
        inv.yourReference = "";
        inv.dueDate = formatDate(createDueDate(order));
        inv.recipient = createRecipient(user);
        return inv;
    }

    private String formatDate(Date date) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        return format1.format(date);
    }

    private NewInvoiceAddress createAddressOnOrder(Order order) {
        User user = userManager.getUserById(order.userId);
        NewInvoiceAddress addr = new NewInvoiceAddress();
        addr.address = user.address.address;
        addr.city = user.address.city;
        addr.country = user.address.countrycode;
        addr.zip = user.address.postCode;
        return addr;
    }

    private NewInvoiceRecipientAdress createRecipientAddressOnOrder(User user) {
        NewInvoiceRecipientAdress addr = new NewInvoiceRecipientAdress();
        addr.address1 = user.address.address;
        addr.city = user.address.city;
        addr.country = user.address.countrycode;
        addr.zip = user.address.postCode;
        return addr;
    }

    private List<NewInvoiceItem> createItemsList(Order order) {
        List<NewInvoiceItem> items = new ArrayList();
        for(CartItem cartItem : order.cart.getItems()) {
            NewInvoiceItem item = new NewInvoiceItem();
            item.description = cartItem.getProduct().name;
            item.number = cartItem.getCount();
            item.quantity = (double)cartItem.getCount();
            item.productCode = cartItem.getProduct().accountingSystemId;
            item.taxRate = cartItem.getProduct().taxgroup;
            item.unitPrice = cartItem.getProduct().price;
            items.add(item);
        }
        return items;
    }

    private NewInvoiceRecipient createRecipient(User user) {
        NewInvoiceRecipient recipient = new NewInvoiceRecipient();
        recipient.address = createRecipientAddressOnOrder(user);
        recipient.customerNumber = user.customerId + "";
        recipient.email = user.emailAddress;
        recipient.name = user.fullName;
        recipient.number = user.customerId;
        if(user.companyObject != null) {
            recipient.organisationNumber = user.companyObject.vatNumber;
        } else {
            recipient.organisationNumber = "";
        }
        return recipient;
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

    
    private boolean pushMessage(String url, String data, String type) {
        System.out.println("Pushing: ");
        System.out.println(data);
        Application sendRegningApp = storeApplicationPool.getApplication("e8cc6f68-d194-4820-adab-6052377b678d");
        if (sendRegningApp == null) {
            return false;
        }

        String usernamepassword = sendRegningApp.getSetting("email") + ":" + sendRegningApp.getSetting("password");
        String originatorid = sendRegningApp.getSetting("originatorid");
        
        HashMap<String, String> headerData = new HashMap();
        headerData.put("Originator-Id", originatorid);
        
        try {
            String res = webManager.htmlPostBasicAuth(url, data, true, "UTF-8", usernamepassword, "Basic", true, type);
            System.out.println(res);
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;        
    }

    private void createUpdateRecipient(User user) {
        String url = root + "recipients/";
        NewInvoiceRecipient recipient = createRecipient(user);
        Gson gson = new Gson();
        if(!user.createInSendRegning) {
            pushMessage(url, gson.toJson(recipient), "POST");
        } else {
            pushMessage(url, gson.toJson(recipient), "PUT");
        }
    }
}
