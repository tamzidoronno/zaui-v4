
package com.thundashop.api.examples;

import com.thundashop.api.managers.GetShopApi;
import com.thundashop.core.usermanager.data.User;

/**
 *
 * @author boggi
 */
public class SimpleLogin {
    
    public String sessionId = "somesessionidformynotsecure";
    
    public static void main(String[] args) throws Exception {
        SimpleLogin login = new SimpleLogin();
        login.someSimpleTests();
    }

    private void someSimpleTests() throws Exception {
        GetShopApi api = new GetShopApi(25554, "localhost", sessionId, "www.getshop.com");
        System.out.println("My store id : " + api.getStoreManager().getStoreId());
        api.getUserManager().logOn("****", "***");
        User user = api.getUserManager().getLoggedOnUser();
        System.out.println("Hello " + user.fullName + ", you where last logged on : " + user.lastLoggedIn);
    }
}
