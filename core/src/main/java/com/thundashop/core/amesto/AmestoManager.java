/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.amesto;

import com.getshop.scope.GetShopSession;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.webmanager.WebManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    @Override
    public void syncStockQuantity(String hostname, String productId) {
        Product product = productManager.getProduct(productId);
        
        JsonParser parser = new JsonParser();
        JsonObject jsonProduct = null;
        
        try {
            jsonProduct = parser.parse(webManager.htmlGet("http://" + hostname + "/api/Articles/" + product.sku + "/StockInfo")).getAsJsonObject();
        } catch (Exception ex) {
        }
        
        product.stockQuantity = (int) Float.parseFloat(jsonProduct.get("UnitInStock").toString());
        productManager.saveProduct(product);
    }
    
    @Override
    public void syncAllStockQuantity(String hostname) {
        List<Product> products = productManager.getAllProducts();
        Product testProduct = products.get(5);
        testProduct.sku = "221";
        products.set(5, testProduct);
        
        products
                .stream()
                .filter(product -> product.sku != null)
                .filter(product -> !product.sku.isEmpty())
                .forEach(product -> syncStockQuantity(hostname, product.id));
    }
}
