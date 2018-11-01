package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.oauthmanager.OAuthManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.data.User;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class VismaEAccountingSystem extends AccountingSystemBase {

    String apiEndPoint = "https://eaccountingapi-sandbox.test.vismaonline.com/";
    
    @Autowired
    OAuthManager oAuthManager;
    
    @Autowired
    StoreManager storeManager;
    
    private String oAuthSource = "vismaeaccounting";
    
    @Override
    public List<SavedOrderFile> createFiles(List<Order> orders, Date start, Date end) {
        try {
            Map<String, List<Order>> groupedOrders = groupOrders(orders);
            List<Order> otherOrders = groupedOrders.get("other");
            if(otherOrders != null && !otherOrders.isEmpty()) {
                String transferId = transferOrders(orders, "other");
            }

            List<Order> invoiceOrders = groupedOrders.get("invoice");
            if(invoiceOrders != null && !invoiceOrders.isEmpty()) {
                String transferId = transferOrders(orders, "other");
            }
        }catch(Exception e) {
            addToLog("Exception caught:" + e.getMessage());
            logPrint(e);
        }
        return null;
    }

    @Override
    public SystemType getSystemType() {
        return SystemType.VISMAEACCOUNTING_API;
    }

    @Override
    public String getSystemName() {
        return "Visma Eaccounting";
    }
    
    

    @Override
    public void handleDirectTransfer(String orderId) {
        try {
            Order order = orderManager.getOrder(orderId);
            transferOrder(order, "other");
        }catch(Exception e) {
            addToLog("Excpetion caught:" + e.getMessage());
            logPrintException(e);
        }
    }

    private String transferOrders(List<Order> orders, String type) throws Exception {
        for(Order order : orders) {
            transferOrder(order, type);
        }
        return "";
    }
    
    @Override
    public HashMap<String, String> getConfigOptions() {
       String endpoint = oAuthManager.getAuthrizeEndPoint(oAuthSource);
       String authState = oAuthManager.createState(oAuthSource, storeManager.getMyStore().getDefaultWebAddress());
       HashMap<String, String> ret = new HashMap();
        String clientId = oAuthManager.getClientId(oAuthSource);
        
        if(clientId != null && !clientId.isEmpty()) {
            String url = endpoint+"?client_id="+clientId+"&scope=ea:api+offline_access+ea:sales+ea:accounting+ea:purchase&nounce=asdasdasdas&state="+authState+"&redirect_uri=https://localhost:44300/callback&response_type=code";
            url = url.replace("[client_id]", clientId);
            ret.put("oAuthButton", url);
        }
        return ret;
    }

    private User getUserFromOrder(Order order) throws Exception {
        User user = userManager.getUserById(order.userId);
        if(user.externalAccountingId == null || user.externalAccountingId.isEmpty()) {
            String externalId = createUser(user);
            user.externalAccountingId = externalId;
            userManager.saveUser(user);
        }
        if(user.externalAccountingId == null || user.externalAccountingId.isEmpty()) {
            addToLog("Could not upload order: " + order.incrementOrderId + " due to no valid customer connected to it");
            return null;
        }
        
        return user;
    }

    private String createUser(User user) throws Exception {
        
        
        if(user == null) {
            throw new Exception("Null user");
        }
        if(user.externalAccountingId != null && !user.externalAccountingId.isEmpty()) {
            return user.externalAccountingId;
        }
        
        String externalAccountId = checkIfUserExistsInVismaEAccounting(user.customerId);
        if(user.externalAccountingId != null && !user.externalAccountingId.isEmpty()) {
            user.externalAccountingId = externalAccountId;
            userManager.saveUser(user);
            return externalAccountId;
        } else {
            System.out.println("CREATE USER, NEXT STEP FINALLY");
            String res = sendUserToVismaEaccounting(user);
            return res;
        }
    }

    private void transferOrder(Order order, String type) throws Exception {
        String uniqueId = getUniqueCustomerIdForOrder(order);
        String userId = "";
        if(uniqueId != null && !uniqueId.isEmpty()) {
            userId = checkIfUserExistsInVismaEAccounting(new Integer(uniqueId));
        } else {
            User user = getUserFromOrder(order);
            if(user == null) {
                return;
            }
            userId = user.externalAccountingId;
        }
        
        if(type.equals("invoice")) {
            uploadInvoiceOrder(order, userId);
        } else if(type.equals("other")) {
            uploadOtherOrder(order, userId);
        } else {
            addToLog("Failed to find type: " + type + " for order: " + order.incrementOrderId);
        }
        
    }
    
    private String checkIfUserExistsInVismaEAccounting(Integer accountingId) {
        try {
            String endpoint = getEndPoint();
            String token = oAuthManager.getToken(oAuthSource);
            String postFix= URLEncoder.encode("$filter=CustomerNumber eq '"+accountingId+"'", "UTF-8");
            String res = webManager.htmlPostBasicAuth(endpoint+"v2/customers?"+postFix, "", true, "UTF-8",token,"Bearer",false,"GET",new HashMap());
            Gson gson = new Gson();
            TermsDataResult rr = gson.fromJson(res, TermsDataResult.class);
            return rr.Data.get(0).Id;
        }catch(Exception e) {
//            logPrintException(e);
            //If error is thrown, assume 404 not found.
            return null;
        }
    }


    private String sendUserToVismaEaccounting(User user) {
        VismaeaccountingCustomer customer = new VismaeaccountingCustomer();
        customer.setUser(user);
        
        Gson gson = new Gson();
        HashMap<String, String> headerData = new HashMap();
        String termsId = getTermsOfPayment();
        customer.TermsOfPaymentId = termsId;
        String toPost = gson.toJson(customer);
        
        
        String endpoint = getEndPoint();
        String token = oAuthManager.getToken(oAuthSource);

        try {
            String result = webManager.htmlPostBasicAuth(endpoint+"v2/customers", toPost, true, "UTF-8", token,"Bearer", false, "POST", headerData);
            VismaEaccountingUserUploadResponse response = gson.fromJson(result, VismaEaccountingUserUploadResponse.class);
            return response.Id;
        }catch(Exception e) {
            addToLog("Sendusertovisma:" + webManager.getLatestErrorMessage());
            addToLog("Regarding user:" + user.fullName + " userid: " + user.customerId);
        }
        return "";
    }

    private String getTermsOfPayment() {
        String endpoint = getEndPoint();
        String token = oAuthManager.getToken(oAuthSource);
        HashMap<String, String> headerData = new HashMap();
        try {
            String result = webManager.htmlPostBasicAuth(endpoint+"v2/termsofpayments", "", false, "UTF-8", token,"Bearer", false, "GET", headerData);
            Gson gson = new Gson();
            TermsDataResult res = gson.fromJson(result, TermsDataResult.class);
            if(!res.Data.isEmpty()) {
                return res.Data.get(0).Id;
            }
        }catch(Exception e) {
            System.out.println(webManager.getLatestErrorMessage());
            e.printStackTrace();
        }
        return "";
    }

    private String getEndPoint() {
        return apiEndPoint;
    }    

    private void uploadOtherOrder(Order order, String customerId) {
        VismaEaccountingOrder toUpload = new VismaEaccountingOrder(order, customerId);
        User user = userManager.getUserById(order.userId);
        toUpload.setInvoiceUser(user);
        Integer lineNumber = 1;
        for(CartItem item : order.cart.getItems()) {
            String articleNumber = getProductNumber(item.getProduct());
            String text = createLineText(item);
            VismaEaccountingOrderLine line = new VismaEaccountingOrderLine(item, lineNumber, text, articleNumber);
            toUpload.Rows.add(line);
            lineNumber++;
        }

        
        String endpoint = getEndPoint();
        String token = oAuthManager.getToken(oAuthSource);
        HashMap<String, String> headerData = new HashMap();
        Gson gson = new Gson();
        String toPost = gson.toJson(toUpload);
        try {
            String result = webManager.htmlPostBasicAuth(endpoint+"v2/orders", toPost, true, "UTF-8", token,"Bearer", false, "POST", headerData);
            System.out.println(result);
        }catch(Exception e) {
            System.out.println(webManager.getLatestErrorMessage());
            addToLog("Uploadother: " + webManager.getLatestErrorMessage());
        }
    }

    private void uploadInvoiceOrder(Order order, String customerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    boolean isUsingProductTaxCodes() {
        return true;
    }
}
