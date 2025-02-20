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
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.listmanager.data.JsTreeList;
import com.thundashop.core.listmanager.data.TreeNode;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.webmanager.WebManager;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
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
    
    @Autowired
    public ListManager listManager;

    public void syncStockQuantity(String hostname, String productId) {
        Product product = productManager.getProduct(productId);
        
        try {
            String endpoint = "http://" + hostname + "/api/Articles/" + product.sku;
            JsonObject jsonObj = webManager.htmlGetJson(endpoint + "/StockInfo?warehouseId=1");
            product.stockQuantity = (int) Float.parseFloat(jsonObj.get("UnitInStock").toString());
            JsonObject jsonObj2 = webManager.htmlGetJson(endpoint);
            product.price = Float.parseFloat(jsonObj2.get("Price1").toString());
            addTaxes(product);
        } catch (Exception ex) {
            logPrint(ex.toString());
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
        
        products
                .stream()
                .filter(product -> (product.sku == null || product.sku.isEmpty()))
                .forEach(product -> warnNoSkuForProduct(product));
    }
    
    @Override
    public void syncAllOrders(String hostname) {
        List<Order> orders = orderManager.getOrders(null, null, null);
        
        mainLoop:
        for(Order order : orders) {
            JsonObject jsonObject = new JsonObject();
            
            if(order.transferredToAccountingSystem ||  order.userId == null || userManager.getUserById(order.userId).accountingId == null || userManager.getUserById(order.userId).accountingId.isEmpty()) {
                continue;
            }
            
            jsonObject.addProperty("CustomerNo", userManager.getUserById(order.userId).accountingId);
            jsonObject.addProperty("OrderDate", new SimpleDateFormat("MM/dd/yyyy").format(order.createdDate));
            JsonArray orderProducts = new JsonArray();
            
            for(CartItem item : order.cart.getItems()) {
                Product orderProduct = item.getProduct();
                Map<String, String> map = item.getVariations();
                String sku = null;

                if(!map.isEmpty()) {
                    StringBuilder skuId = new StringBuilder("");
                
                    for(String value : map.values()) {
                        skuId.insert(0, value);
                    }

                    sku = productManager.getProduct(orderProduct.id).variationCombinations.get(skuId.toString());
                } else {
                    sku = orderProduct.sku;
                }
                
                if(sku == null || sku.isEmpty()) {
                    continue mainLoop;
                }
                
                JsonObject jsonOrderProduct = new JsonObject();
                        
                jsonOrderProduct.addProperty("ArticleNo", sku);
                jsonOrderProduct.addProperty("Quantity", item.getCount());
                        
                orderProducts.add(jsonOrderProduct);
            }
            
            jsonObject.add("Lines", orderProducts);
            JsonObject jsonResponse = null;
            
            try {
                jsonResponse = webManager.htmlPostJson("http://" + hostname + "/api/SalesOrders", jsonObject, null);
                
                if(jsonResponse != null && !jsonResponse.toString().isEmpty()) {
//                  May be needed for CustomerInvoiceNo instead of the current one
//                  String orderNo = jsonResponse.get("OrderNo").toString();
                    
                    JsonObject jsonVoucher = new JsonObject();
                    
                    if("ns_d02f8b7a_7395_455d_b754_888d7d701db8\\Dibs".equals(order.payment.paymentType)) {
                        JsonArray voucherLines = new JsonArray();
                        
                        JsonObject line = new JsonObject();
                        line.addProperty("VoucherDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(order.createdDate));
                        line.addProperty("VoucherTypeNo", 8);
                        line.addProperty("VoucherText", order.invoiceNote);
                        line.addProperty("SuppliersInvoiceNo", order.incrementOrderId);
                        line.addProperty("CustomerInvoiceNo", userManager.getUserById(order.userId).accountingId);
                        line.addProperty("Amount", order.cart.getTotal(false));
                        line.addProperty("EntryDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(order.createdDate));
                        
                        voucherLines.add(line);

                        jsonVoucher.add("Lines", voucherLines);
                        jsonVoucher.addProperty("EntryDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(order.createdDate));

                        jsonResponse = webManager.htmlPostJson("http://" + hostname + "/api/Vouchers", jsonVoucher, null);
                        logPrint(jsonResponse.toString());
                    }
                    
                    order.transferredToAccountingSystem = true;
                    orderManager.saveOrder(order);
                }
                
            } catch (Exception ex) {
                logPrint(ex.toString());
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
               user.emailAddress != null &&
               user.cellPhone != null) {
                
                if(user.address.countrycode == null) {
                    user.address.countrycode = "578";
                }
                
                if(user.address.address2 == null) {
                    user.address.address2 = "";
                }
                
                jsonObject.addProperty("Name", user.fullName);
                jsonObject.addProperty("Address1", user.address.address);
                jsonObject.addProperty("EmailAddress", user.emailAddress);
                jsonObject.addProperty("Address2", user.address.address2);
                jsonObject.addProperty("PostCode", user.address.postCode);
                jsonObject.addProperty("PostOffice", user.address.city);
                jsonObject.addProperty("CountryNo", user.address.countrycode);
                jsonObject.addProperty("Phone", user.cellPhone);
                
                try {
                    JsonObject jsonResponse = webManager.htmlPostJson("http://" + hostname + "/api/Customers", jsonObject, "iso-8859-1");
                    
                    if(jsonResponse != null && !jsonResponse.toString().isEmpty()) {
                        user.accountingId = jsonResponse.get("CustomerNo").toString();
                        user.isTransferredToAccountSystem = true;
                        userManager.saveUser(user);
                    }
                } catch (Exception ex) {
                    logPrint(ex.toString());
                }
            }          
        }
    }

    private void warnNoSkuForProduct(Product product) {
        logPrint("No sku (articlenumber) set for product: " + product.name);
    }

    private void addTaxes(Product product) {
        if(product.taxGroupObject != null && product.taxGroupObject.taxRate != null) {
            product.price = product.price * (1 + product.taxGroupObject.getTaxRate());
        }
    }
}
