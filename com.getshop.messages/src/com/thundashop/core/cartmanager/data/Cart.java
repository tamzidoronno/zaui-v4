/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.cartmanager.data;

import com.thundashop.core.common.CartCompositeKey;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.Address;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Cart extends DataCommon {
    public Address address;
    public HashMap<CartCompositeKey, Product> products = new HashMap();
    public HashMap<CartCompositeKey, Integer> counter = new HashMap();
    
    public double getTotal() {
        double price = 0;
        
        for (CartCompositeKey productKey : products.keySet()) {
            Product product = products.get(productKey);
            price += product.getPrice(productKey.getVariations()) * getProductCount(productKey);
        }

        return price;
    }
    
    public void addProduct(Product product, List<String> variations) {
        CartCompositeKey compkey = new CartCompositeKey(product.id, variations);
        products.put(compkey, product);
        int count = getProductCount(compkey);
        counter.put(compkey, ++count);
    }
    
    public void removeProduct(String productId, List<String> variations) {
        CartCompositeKey key = new CartCompositeKey(productId, variations);
        products.remove(key);
        counter.remove(key);
    }

    public int getProductCount(CartCompositeKey productid) {
        return (counter.get(productid) != null) ? counter.get(productid) : 0;
    }

    public void setProductCount(String productId, List<String> variations, int count) {
        CartCompositeKey cartKey = new CartCompositeKey(productId, variations);
        counter.put(cartKey, count);
    }

    public HashMap<CartCompositeKey, Integer> getProducts() {
        HashMap<CartCompositeKey, Integer> returns = new HashMap<CartCompositeKey, Integer>();
        for (CartCompositeKey productKey : products.keySet()) {
            returns.put(productKey, getProductCount(productKey));
        }
        return returns;
    }

    public int getProductCount(Product product) {
        return 1;
    }

    public void clear() {
        products.clear();
        counter.clear();
    }
}
