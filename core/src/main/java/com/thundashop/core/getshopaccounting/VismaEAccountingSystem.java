package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thundashop.core.accountingmanager.SavedOrderFile;
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
    StoreManager storeManager;
    
    String endpointTest = "https://eaccountingapi-sandbox.test.vismaonline.com/v2/";
    String authTest = "https://identity-sandbox.test.vismaonline.com/connect/authorize";
    String tokenTest = "https://identity-sandbox.test.vismaonline.com/connect/token";
    String clientIdTest = "getshop";
    String clientSecretTest = "BhQ2172BnzvSxXIzzViuyIimGTKfnkD9IeyhyZmI5K7NzgQ86m7baOx6WfvEEhHaa4dFv9dDzHuS69";
    
    String endpointProd = "";
    String authProd = "https://identity.vismaonline.com/connect/authorize";
    String tokenProd = "";
    String clientIdProd = "getshop";
    String clientSecretProd = "dduj4kGnhy1tgSFXa179YnJAB1atauRDXkYxKDCpUWvSCgWLDFecGkt3bn6GdRv";
    
    
    
    String endpoint = "";
    String tokenpoint = "";
    String authpoint = "";
    String clientId = "";
    String clientSecret = "";
    private Date tokenExpires;
    private String token;
    
    
    public void setupEndpoints() {
        if(frameworkConfig.productionMode) {
            endpoint = endpointProd;
            tokenpoint = tokenProd;
            authpoint = authProd;
            clientId = clientIdProd;
            clientSecret = clientSecretProd;
        } else {
            endpoint = endpointTest;
            tokenpoint = tokenTest;
            authpoint = authTest;
            clientId = clientIdTest;
            clientSecret = clientSecretTest;
        }
        
        token = getConfig("current_token");
        if(getConfig("token_expire") != null && !getConfig("token_expire").isEmpty()) {
            tokenExpires = new Date();
            tokenExpires.setTime(new Long(getConfig("token_expire")));
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
        ret.put("code", "Api code");
        String clientId = getConfig("client_id");
        String webaddr = storeManager.getMyStore().getDefaultWebAddress();
        if(clientId != null && !clientId.isEmpty()) {
            String url = authpoint+"?client_id="+clientId+"&scope=ea:api+offline_access+ea:sales+ea:accounting+ea:purchase&nounce=asdasdasdas&state=10135467&redirect_uri=https://localhost:44300/callback&response_type=code";
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
            String token = getToken();
            String postFix= URLEncoder.encode("$filter=CustomerNumber eq '"+accountingId+"'", "UTF-8");
            
            String res = webManager.htmlPostBasicAuth(endpoint+"customers?"+postFix, "", true, "UTF-8",token,"Bearer",false,"GET",new HashMap());
            System.out.println(res);
        }catch(Exception e) {
            logPrintException(e);
            //If error is thrown, assume 404 not found.
            return null;
        }
        return null;
    }

    private String getToken() {
        if(token != null && !token.isEmpty() && new Date().before(tokenExpires)) {
            return token;
        }
        
        String refreshToken = getConfig("refresh_token");
        if(refreshToken != null && !refreshToken.isEmpty()) {
            refreshToken();
            return token;
        } else {
            HashMap<String, String> headerData = new HashMap();
            String toPost = "code="+getConfig("code") + "&grant_type=authorization_code&redirect_uri=https://localhost:44300/callback";
            String auth = clientId + ":" + clientSecret;
            try {
                String result = webManager.htmlPostBasicAuth(tokenpoint, toPost, false, "UTF-8", auth, "Basic", true, "POST", headerData);
                Gson gson = new Gson();
                VismaEaccountingTokenResponse res = gson.fromJson(result, VismaEaccountingTokenResponse.class);

                token = res.access_token;
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, res.expires_in);
                tokenExpires = cal.getTime();
                setConfig("refresh_token", res.refresh_token);
                setConfig("current_token", token);
                setConfig("token_expire", tokenExpires.getTime() + "");
                return token;
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void refreshToken() {
            HashMap<String, String> headerData = new HashMap();
            String toPost = "code="+getConfig("code") + "&grant_type=refresh_token&refresh_token="+getConfig("refresh_token")+"&redirect_uri=https://localhost:44300/callback";
            String auth = clientId + ":" + clientSecret;
            try {
                String result = webManager.htmlPostBasicAuth(tokenpoint, toPost, false, "UTF-8", auth, "Basic", true, "POST", headerData);
                Gson gson = new Gson();
                VismaEaccountingTokenResponse res = gson.fromJson(result, VismaEaccountingTokenResponse.class);

                token = res.access_token;
                setConfig("refresh_token", res.refresh_token);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, res.expires_in);
                tokenExpires = cal.getTime();
                setConfig("refresh_token", res.refresh_token);
                setConfig("current_token", token);
                setConfig("token_expire", tokenExpires.getTime() + "");
            }catch(Exception e) {
                e.printStackTrace();
            }
    }

    private String sendUserToVismaEaccounting(User user) {
        VismaeaccountingCustomer customer = new VismaeaccountingCustomer();
        customer.setUser(user);
        
        Gson gson = new Gson();
        HashMap<String, String> headerData = new HashMap();
        String toPost = gson.toJson(customer);
        try {
            String result = webManager.htmlPostBasicAuth(endpoint+"/customers", toPost, true, "UTF-8", token,"Bearer", false, "POST", headerData);
            System.out.println(result);
        }catch(Exception e) {
            addToLog(webManager.getLatestErrorMessage());
            addToLog("Regarding user:" + user.fullName + " userid: " + user.customerId);
        }
        return "";
    }
    
}
