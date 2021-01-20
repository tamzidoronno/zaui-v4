package com.thundashop.core.sendregning;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.FrameworkConfig;
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
import org.joda.time.DateTime;
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
    
    @Autowired
    private FrameworkConfig frameworkConfig;
    
    
    String root = "https://www.sendregning.no/";
    private String errorMessage = "";
    
    @Override
    public String sendOrder(String orderId, String email) {
        Order order = orderManager.getOrderSecure(orderId);
        
        if(!validateOrderToMakeSureItsOkay(order)) {
            return errorMessage;
        }
        
        if(!frameworkConfig.productionMode) {
            order.sendRegningId = 0;
        }
        if(order.sendRegningId > 0) {
            resendInvoice(order.sendRegningId, email);
            return "";
        } else {
            if(createInvoiceAtSendRegning(orderId, email) != null) {
                return "";
            } else {
                return "failed to push to sendregning server.";
            }
        }
    }

    private String createInvoiceAtSendRegning(String orderId, String email) {
        Order order = orderManager.getOrderSecure(orderId);
        User user = userManager.getUserById(order.userId);
        Integer sendRegningCustomerNumber = createUpdateRecipient(user);
        String url = root+"invoices/";
        NewInvoice inv = createInvoice(orderId, email);
        inv.recipient.number = sendRegningCustomerNumber;
        Gson gson = new Gson();
        String data = gson.toJson(inv);
        
        pushMessage(url, data, "POST");
        HashMap<String, String> latestResponseHeader = webManager.getLatestResponseHeader();
        String location = latestResponseHeader.get("Location");
        String number = location.replace("https://www.sendregning.no/invoices/", "");
        number = number.replace(",", "");
        order.sendRegningId = new Integer(number);
        return order.sendRegningId + "";
    }

    private NewInvoice createInvoice(String orderId, String email) {
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
        inv.recipient = createInvoiceRecipient(user);
        if(email != null && email.contains("@")) {
            NewInvoiceShipmentEmail e = new NewInvoiceShipmentEmail();
            e.address = email;
            inv.shipment.email.add(e);
        }
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
        int i = 1;
        for(CartItem cartItem : order.cart.getItems()) {
            NewInvoiceItem item = new NewInvoiceItem();
            item.description = convertToIso(cartItem, order);
            item.number = i;
            item.quantity = (double)cartItem.getCount();
            item.productCode = cartItem.getProduct().accountingSystemId;
            item.taxRate = getTaxRate(cartItem);
            item.unitPrice = cartItem.getProduct().price;
            items.add(item);
            i++;
        }
        return items;
    }

    private NewInvoiceRecipient createInvoiceRecipient(User user) {
        NewInvoiceRecipient recipient = new NewInvoiceRecipient();
        recipient.number = user.customerId;
        return recipient;
    }
    
    private NewRecipient createRecipient(User user) {
        NewRecipient recipient = new NewRecipient();
        recipient.contact.address = createRecipientAddressOnOrder(user);
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

    
    private String pushMessage(String url, String data, String type) {
        Application sendRegningApp = storeApplicationPool.getApplication("e8cc6f68-d194-4820-adab-6052377b678d");
        if (sendRegningApp == null) {
            return null;
        }

        String usernamepassword = sendRegningApp.getSetting("email") + ":" + sendRegningApp.getSetting("password");
        String originatorid = sendRegningApp.getSetting("originatorid");
        
        if(!frameworkConfig.productionMode) {
            usernamepassword = "post@getshop.com:1122T3st";
            originatorid = "79732";
        }
        
        HashMap<String, String> headerData = new HashMap();
        headerData.put("Originator-Id", originatorid);
        
        try {
            String res = webManager.htmlPostBasicAuth(url, data, true, "ISO-8859-1", usernamepassword, "Basic", true, type);
            return res;
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Integer createUpdateRecipient(User user) {
        Integer recipientNumber = findRecipient(user.customerId);
        String url = root + "recipients/";
        NewRecipient recipient = createRecipient(user);
        recipient.number = recipientNumber;
        Gson gson = new Gson();
        if(recipientNumber == 0) {
            pushMessage(url, gson.toJson(recipient), "POST");
        } else {
            url += recipientNumber;
            pushMessage(url, gson.toJson(recipient), "PUT");
        }
        return findRecipient(user.customerId);
    }

    private Integer findRecipient(Integer customerId) {
        String url = root + "recipients/?customerNumber=" + customerId;
        String res = pushMessage(url, "", "GET");
        if(res == null) {
            return 0;
        }
        Gson gson = new Gson();
        NewRecipient recipient = gson.fromJson(res, NewRecipient.class);
        return recipient.number;
    }

    private Integer getTaxRate(CartItem cartItem) {
        return cartItem.getProduct().taxGroupObject.taxRate.intValue();
    }

    private void resendInvoice(Integer sendRegningId, String email) {
        String url = root + "/invoices/"+sendRegningId+"/send";
        
        NewInvoiceShipment reciept = new NewInvoiceShipment();
        if(email != null && email.contains("@")) {
            NewInvoiceShipmentEmail mail = new NewInvoiceShipmentEmail();
            mail.address = email;
            reciept.email.add(mail);
        } else {
            reciept.email = null;
        }
        
        Gson gson = new Gson();
        pushMessage(url, gson.toJson(reciept), "POST");
    }

    private boolean validateOrderToMakeSureItsOkay(Order order) {
        User user = userManager.getUserById(order.userId);
        if(user.address == null) {
            errorMessage += "Adress object can not be empty";
            return false;
        }
        if(user.address.address == null || user.address.address.trim().isEmpty()) {
            errorMessage += "Adress field can not be empty";
            return false;
        }
        if(user.address.city == null || user.address.city.trim().isEmpty()) {
            errorMessage += "City field can not be empty";
            return false;
        }
        if(user.address.postCode == null || user.address.postCode.trim().isEmpty()) {
            errorMessage += "Postal code can not be empty";
            return false;
        }
        
        for(CartItem item : order.cart.getItems()) {
            int rate = getTaxRate(item);
            if(rate != 0 && rate != 10 && rate != 15 && rate != 25) {
                errorMessage += "Tax rate is not set correctly for product: " + item.getProduct().name;
                return false;
            }
        }
        
        return true;
    }

    private String convertToIso(CartItem item, Order order) {
        
        String lineText = "";
        String startDate = "";
        if(item.startDate != null) {
            DateTime start = new DateTime(item.startDate);
            startDate = start.toString("dd.MM.yy");
        }

        String endDate = "";
        if(item.endDate != null) {
            DateTime end = new DateTime(item.endDate);
            endDate = end.toString("dd.MM.yy");
        }
        
        String startEnd = "";
        if(startDate != null && endDate != null && !endDate.isEmpty() && !startDate.isEmpty()) {
            startEnd = " (" + startDate + " - " + endDate + ")";
        }
        
        if(!item.getProduct().additionalMetaData.isEmpty()) {
            lineText = item.getProduct().name + " " + item.getProduct().additionalMetaData + startEnd;
        } else {
            String mdata = item.getProduct().metaData;
            if(mdata != null && mdata.startsWith("114")) {
                mdata = "";
            } else {
                mdata = ", " + mdata;
            }
            lineText = item.getProduct().name + mdata + startEnd;
        }
                
        return lineText;
    }
}
