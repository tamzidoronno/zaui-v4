/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.amesto;

import com.getshop.scope.GetShopSession;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.webmanager.WebManager;
import java.text.SimpleDateFormat;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author hung
 */
@Component
@GetShopSession
public class AmestoManager extends ManagerBase implements IAmestoManager {
    
    @Autowired
    public ProductManager productManager;
    
    @Autowired
    public WebManager webManager;
    
    @Autowired
    public OrderManager orderManager;
    
    @Autowired
    public UserManager userManager;
    
    @Override
    public void syncStockQuantity(String hostname, String productId) {
        Product product = productManager.getProduct(productId);
        
        try {
            JsonObject jsonObj = webManager.htmlGetJson("http://" + hostname + "/api/Articles/" + product.sku + "/StockInfo");
            product.stockQuantity = (int) Float.parseFloat(jsonObj.get("UnitInStock").toString());
        } catch (Exception ex) {
        }
        
        productManager.saveProduct(product);
    }
    
    @Override
    public void syncAllStockQuantity(String hostname) {
        List<Product> products = productManager.getAllProducts();
        
        products
                .stream()
                .filter(product -> product.sku != null)
                .filter(product -> !product.sku.isEmpty())
                .forEach(product -> syncStockQuantity(hostname, product.id));
    }
    
    @Override
    public void syncAllOrders(String hostname) {
        List<Order> orders = orderManager.getOrders(null, null, null);
        
        mainLoop:
        for(Order order : orders) {
            JsonObject jsonObject = new JsonObject();
            
            if(order.userId == null || userManager.getUserById(order.userId).accountingId == null || userManager.getUserById(order.userId).accountingId.isEmpty()) {
                continue;
            }
            
            jsonObject.addProperty("CustomerNo", userManager.getUserById(order.userId).accountingId);
            jsonObject.addProperty("OrderDate", new SimpleDateFormat("MM/dd/yyyy").format(order.createdDate));
            JsonArray orderProducts = new JsonArray();
            
            for(CartItem item : order.cart.getItems()) {
                Product orderProduct = item.getProduct();
                
                if(orderProduct.sku == null || orderProduct.sku.isEmpty()) {
                    continue mainLoop;
                }
                
                JsonObject jsonOrderProduct = new JsonObject();
                        
                jsonOrderProduct.addProperty("ArticleNo", orderProduct.sku);
                jsonOrderProduct.addProperty("Quantity", item.getCount());
                        
                orderProducts.add(jsonOrderProduct);
            }
            
            jsonObject.add("Lines", orderProducts);
            
            try {
                JsonObject jsonResponse = webManager.htmlPostJson("http://" + hostname + "/api/SalesOrders", jsonObject);
                
                if(jsonResponse != null && !jsonResponse.toString().isEmpty()) {
                    order.transferredToAccountingSystem = true;
                    orderManager.saveOrder(order);
                }
                
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
    }
    
    @Override
    public void syncAllCostumers(String hostname) {
        List<User> users = userManager.getAllUsers();
        
        for(User user : users) {
            JsonObject jsonObject = new JsonObject();
            
            if(user != null &&
               user.isTransferredToAccountSystem == false &&
               user.fullName != null &&
               user.address != null &&
               user.address.address != null &&
               user.address.city != null &&
               user.address.postCode != null &&
               user.address.countryname != null &&
               user.emailAddress != null) {
                
                jsonObject.addProperty("name", user.fullName);
                jsonObject.addProperty("Address1", user.address.address + ", " +  user.address.city + ", " + user.address.postCode + ", " + user.address.countryname);
                jsonObject.addProperty("EmailAddress", user.emailAddress);
                
                try {
                    JsonObject jsonResponse = webManager.htmlPostJson("http://" + hostname + "/api/Customers", jsonObject);
                    
                    if(jsonResponse != null && !jsonResponse.toString().isEmpty()) {
                        user.accountingId = jsonResponse.get("CustomerNo").toString();
                        user.isTransferredToAccountSystem = true;
                        userManager.saveUser(user);
                    }
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }          
        }
    }
}
