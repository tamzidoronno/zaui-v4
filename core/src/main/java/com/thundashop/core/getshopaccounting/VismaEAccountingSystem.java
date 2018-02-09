package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
import com.google.gson.JsonObject;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.data.User;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class VismaEAccountingSystem extends AccountingSystemBase {

    @Autowired
    StoreManager storeManager;
    
    String endpointTest = "https://eaccountingapi-sandbox.test.vismaonline.com/v2/";
    String endpointProd = "";
    String authTest = "https://identity-sandbox.test.vismaonline.com/connect/authorize";
    String authProd = "https://identity.vismaonline.com/connect/authorize";
    String tokenTest = "https://identity-sandbox.test.vismaonline.com/connect/token";
    String tokenProd = "";
    
    String endpoint = "";
    String tokenpoint = "";
    String authpoint = "";
    
    
    public void setupEndpoints() {
        if(frameworkConfig.productionMode) {
            endpoint = endpointProd;
            tokenpoint = tokenProd;
            authpoint = authProd;
        } else {
            endpoint = endpointTest;
            tokenpoint = tokenTest;
            authpoint = authTest;
        }
    }
    
    @Override
    public List<SavedOrderFile> createFiles(List<Order> orders) {
        setupEndpoints();
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
        setupEndpoints();
        HashMap<String, String> ret = new HashMap();
        
        ret.put("client_id", "Client id");
        ret.put("client_secret", "Client secret");
        
        String clientId = getConfig("client_id");
        String webaddr = storeManager.getMyStore().getDefaultWebAddress();
        if(clientId != null && !clientId.isEmpty()) {
            String url = authpoint+"?response_type=code&client_id=[client_id]&redirect_uri=https://localhost:44300/callback&state=Simplex&scope=ea:accounting+ea:api+ea:purchase+ea:sales+offline_access";
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
        }
        
        
        
        return null;
    }

    private void transferOrder(Order order) {
        String uniqueId = getUniqueCustomerIdForOrder(order);
        String userId = "";
        if(uniqueId != null && !uniqueId.isEmpty()) {
            userId = uniqueId;
        } else {
            User user = getUserFromOrder(order);
            if(user == null) {
                addToLog("Could not find user for order:" + order.incrementOrderId + ", we are probably not able to create it.");
                return;
            }
            userId = user.externalAccountingId;
        }
        
    }

    private String checkIfUserExistsInVismaEAccounting(Integer accountingId) {
        try {
            String postFix= URLEncoder.encode("$filter=CustomerNumber eq '"+accountingId+"'", "UTF-8");
            JsonObject res = webManager.htmlGetJson(endpoint+"customers?"+postFix);
            System.out.println(res);
        }catch(Exception e) {
            logPrintException(e);
            //If error is thrown, assume 404 not found.
            return null;
        }
        return null;
    }
    
}
