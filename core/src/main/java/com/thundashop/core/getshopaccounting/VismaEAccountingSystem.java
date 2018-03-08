package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.oauthmanager.OAuthManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.data.User;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class VismaEAccountingSystem extends AccountingSystemBase {

    @Autowired
    OAuthManager oAuthManager;
    
    @Autowired
    StoreManager storeManager;
    
    private String oAuthSource = "vismaeaccounting";
    
    @Override
    public List<SavedOrderFile> createFiles(List<Order> orders) {
        String transferId = transferOrders(orders);
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
        Order order = orderManager.getOrder(orderId);
        transferOrder(order);
    }

    private String transferOrders(List<Order> orders) {
        for(Order order : orders) {
            transferOrder(order);
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

    private User getUserFromOrder(Order order) {
        User user = userManager.getUserById(order.userId);
        if(user.externalAccountingId == null || user.externalAccountingId.isEmpty()) {
            String externalId = createUser(user);
            user.externalAccountingId = externalId;
            userManager.saveUser(user);
        }
        if(user.externalAccountingId == null || user.externalAccountingId.isEmpty()) {
            return null;
        }
        
        return user;
    }

    private String createUser(User user) {
        System.out.println("Create user:" + user.fullName);
        Integer accountingId = getAccountingAccountId(user.id);
        String externalAccountId = checkIfUserExistsInVismaEAccounting(accountingId);
        if(externalAccountId != null) {
            user.externalAccountingId = externalAccountId;
            userManager.saveUser(user);
            return externalAccountId;
        } else {
            System.out.println("CREATE USER, NEXT STEP FINALLY");
            String res = sendUserToVismaEaccounting(user);
            return res;
        }
    }

    private void transferOrder(Order order) {
        String uniqueId = getUniqueCustomerIdForOrder(order);
        String userId = "";
        if(uniqueId != null && !uniqueId.isEmpty()) {
            userId = uniqueId;
        } else {
            User user = getUserFromOrder(order);
            if(user == null) {
                return;
            }
            userId = user.externalAccountingId;
        }
    }
    
    private String checkIfUserExistsInVismaEAccounting(Integer accountingId) {
        try {
            String endpoint = oAuthManager.getEndPoint(oAuthSource);
            String token = oAuthManager.getToken(oAuthSource);
            String postFix= URLEncoder.encode("$filter=CustomerNumber eq '"+accountingId+"'", "UTF-8");
            String res = webManager.htmlPostBasicAuth(endpoint+"v2/customers?"+postFix, "", true, "UTF-8",token,"Bearer",false,"GET",new HashMap());
            System.out.println(res);
        }catch(Exception e) {
//            logPrintException(e);
            //If error is thrown, assume 404 not found.
            return null;
        }
        return null;
    }


    private String sendUserToVismaEaccounting(User user) {
        VismaeaccountingCustomer customer = new VismaeaccountingCustomer();
        customer.setUser(user);
        
        Gson gson = new Gson();
        HashMap<String, String> headerData = new HashMap();
        String toPost = gson.toJson(customer);
        
        getTermsOfPayment();
        
        String endpoint = oAuthManager.getEndPoint(oAuthSource);
        String token = oAuthManager.getToken(oAuthSource);

        try {
            String result = webManager.htmlPostBasicAuth(endpoint+"v2/customers", toPost, true, "UTF-8", token,"Bearer", false, "POST", headerData);
            System.out.println(result);
        }catch(Exception e) {
            addToLog(webManager.getLatestErrorMessage());
            addToLog("Regarding user:" + user.fullName + " userid: " + user.customerId);
        }
        return "";
    }

    private void getTermsOfPayment() {
        String endpoint = oAuthManager.getEndPoint(oAuthSource);
        String token = oAuthManager.getToken(oAuthSource);
        HashMap<String, String> headerData = new HashMap();
        try {
            String result = webManager.htmlPostBasicAuth(endpoint+"v2/termsofpayments", "", false, "UTF-8", token,"Bearer", false, "GET", headerData);
            System.out.println(result);
        }catch(Exception e) {
            System.out.println(webManager.getLatestErrorMessage());
            e.printStackTrace();
        }
    }
    
}
