/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bambora;

import com.braintreegateway.Environment;
import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.dibs.IDibsManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.webmanager.WebManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class BamboraManager extends ManagerBase implements IBamboraManager  {
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    UserManager userManager;
    
    @Autowired
    WebManager webManager;
    
    @Autowired
    StoreApplicationPool storeApplicationPool;
    
    public void getCheckoutUrl(String orderId) {
        Order order = orderManager.getOrder(orderId);
        User user = userManager.getUserById(order.userId);
        
        BamboraData data = new BamboraData();
        data.setUser(user);
        data.setOrder(order);
        
        Double totalEx = orderManager.getTotalAmountExTaxes(order);
        Double total = orderManager.getTotalAmount(order);
        data.setTotal(totalEx, total);
        data.setCallbacks("webaddr.no", "a92d56c0-04c7-4b8e-a02d-ed79f020bcca", orderId, getMerchantId(), storeId);
        Gson test = new Gson();
        String url = createCheckoutUrl(data);
    }

    private String getAccessToken() {
        Application bamboraApp = storeApplicationPool.getApplicationWithSecuredSettings("a92d56c0-04c7-4b8e-a02d-ed79f020bcca");
        Setting setting = bamboraApp.settings.get("access_token");
        return setting.value;
    }

    private String getSecretToken() {
        Application bamboraApp = storeApplicationPool.getApplicationWithSecuredSettings("a92d56c0-04c7-4b8e-a02d-ed79f020bcca");
        Setting setting = bamboraApp.settings.get("secret_token");
        return setting.value;
    }
    
    private String getMerchantId() {
        Application bamboraApp = storeApplicationPool.getApplicationWithSecuredSettings("a92d56c0-04c7-4b8e-a02d-ed79f020bcca");
        Setting setting = bamboraApp.settings.get("merchant_id");
        return setting.value;
    }

    private String createCheckoutUrl(BamboraData data) {
        String endpoint = "https://api.v1.checkout.bambora.com/checkout";
        String access_token = getAccessToken();
        String secret_token = getSecretToken();
        String merchant_id = getMerchantId();
        data.customer.email = "test@jaaj.no";
        
        Gson gson = new Gson();
        String toPost = gson.toJson(data);
        System.out.println(toPost);
        try {
            String res = webManager.htmlPostBasicAuth(endpoint, toPost, true, "UTF-8", access_token);
            System.out.println(res);
        }catch(Exception ex) {
            logPrintException(ex);
            return "";
        }
        return "";
    }

}
