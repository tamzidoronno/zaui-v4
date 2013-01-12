/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.productmanager;

//import com.thundashop.core.common.DatabaseSaver;
//import com.thundashop.core.common.ErrorException;
//import com.thundashop.core.common.Logger;
//import com.thundashop.core.databasemanager.data.Credentials;
//import com.thundashop.core.productmanager.data.Product;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author ktonder
 */
public class StoreProductManager {

//    private Logger logger;
//    private String storeId;
//    private DatabaseSaver databaseSaver;
//    private Credentials credentials;
//    
//
//    public StoreProductManager(Logger logger, String storeId, DatabaseSaver databaseSaver, Credentials credentials) {
//        this.logger = logger;
//        this.databaseSaver = databaseSaver;
//        this.storeId = storeId;
//        this.credentials = credentials;
//    }
//
//    public ArrayList<Product> getRandomProducts(int fetchSize, String ignoreProductId) {
//        List<Product> randomProducts = new ArrayList<>();
//        
//        for (Product product : products.values()) {
//            if (product.page.id.equals(ignoreProductId) || product.id.equals(ignoreProductId))
//                continue;
//            
//            randomProducts.add(product);
//        }
//        
//        Collections.shuffle(randomProducts);
//        if (randomProducts.size() < fetchSize)
//            fetchSize = randomProducts.size();
//        
//        return new ArrayList<>(randomProducts.subList(0, fetchSize));
//    }
//    
//    public List<Product> getProducts(List<String> productIds) throws ErrorException {
//        List<Product> retproducts = new ArrayList<Product>();
//
//        if (productIds == null || productIds.size() == 0) {
//            return retproducts;
//        }
//
//        for (String productId : productIds) {
//            if (productId == null) {
//                continue;
//            }
//            Product product = products.get(productId);
//            if (product != null) {
//                retproducts.add(product);
//            }
//        }
//
//        if (retproducts.isEmpty()) {
//            throw new ErrorException(28);
//        }
//
//        return retproducts;
//    }
//
//
//    public Product getProduct(String productId) {
//        return products.get(productId);
//    }
//
//   
//    public List<Product> getProductsByParentId(String parentId) {
//        List<Product> returnProducts = new ArrayList();
//        for (Product product : products.values()) {
//            if (product.page != null && product.page.parent != null && parentId.equals(product.page.parent.id)) {
//                returnProducts.add(product);
//            }
//        }
//        return returnProducts;
//    }
//    
//    void removeProduct(String productId) throws ErrorException {
//        
//    }
//
//    boolean productExists(String pageId) {
//        for (Product product : products.values()) {
//            if (product != null && product.page != null) {
//                if (pageId.equals(product.page.id)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//
//    List<Product> findProductOnText(String toSearchFor) {
//        List<Product> retprods = new ArrayList();
//
//        for (String key : products.keySet()) {
//            Product product = products.get(key);
//            if (product != null && product.name != null && product.name.toLowerCase().contains(toSearchFor.toLowerCase())) {
//                retprods.add(product);
//                continue;
//            }
//
//            if (product != null && product.shortDescription != null
//                    && product.shortDescription.toLowerCase().contains(toSearchFor.toLowerCase())) {
//                retprods.add(product);
//                continue;
//            }
//
//            if (product != null && product.description != null
//                    && product.description.toLowerCase().contains(toSearchFor.toLowerCase())) {
//                retprods.add(product);
//                continue;
//            }
//        }
//
//        return retprods;
//    }
}
