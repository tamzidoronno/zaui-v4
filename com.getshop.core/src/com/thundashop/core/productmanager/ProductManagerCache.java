package com.thundashop.core.productmanager;

import com.thundashop.core.common.CachingKey;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.pagemanager.IPageManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductCriteria;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author boggi
 */
public class ProductManagerCache implements IProductManager {
    private ProductManager manager;
    private final String addr;

    public ProductManagerCache(ProductManager manager, String addr) {
        this.manager = manager;
        this.addr = addr;
    }
    
    @Override
    public void changeStockQuantity(String productId, int count) throws ErrorException {
    }

    @Override
    public Product saveProduct(Product product) throws ErrorException {
         return null;
   }

    @Override
    public List<Product> getProducts(ProductCriteria productCriteria) {
         return null;
   }

    @Override
    public void removeProduct(String productId) throws ErrorException {
    }

    @Override
    public Product createProduct() throws ErrorException {
         return null;
   }

    @Override
    public ArrayList<Product> getRandomProducts(Integer fetchSize, String ignoreProductId) {
         return null;
   }

    @Override
    public HashMap<String, String> translateEntries(List<String> entryIds) throws ErrorException {
        HashMap<String, String> result = manager.translateEntries(entryIds);
        
        CachingKey key = new CachingKey();
        
        LinkedHashMap<String,Object> keys = new LinkedHashMap();
        keys.put("entryIds", entryIds);
        key.args = keys;
        key.interfaceName = getInterfaceName();
        key.sessionId = "";
        key.method = "translateEntries";
        
        manager.getCacheManager().addToCache(key, result, manager.storeId, addr);
        
        return null;
    }

    @Override
    public Product getProduct(String id) throws ErrorException {
        return null;
    }

    @Override
    public void setMainImage(String productId, String imageId) throws ErrorException {
    }

    @Override
    public void addImage(String productId, String productImageId, String description) throws ErrorException {
    }

    private String getInterfaceName() {
        return this.getClass().getInterfaces()[0].getCanonicalName().replace("com.thundashop.", "");
    }
    

    public void updateTranslateEntries(String pageId) throws ErrorException {
        HashMap<CachingKey, Object> objects = manager.getCacheManager().getAllCachedObjects(manager.storeId);
        CachingKey toRemove = null;
        for(CachingKey key : objects.keySet()) {
            if(key.interfaceName.equals(getInterfaceName()) && key.method.equals("translateEntries")) {
                LinkedHashMap<String,Object> map = (LinkedHashMap<String,Object>) key.args;
                List<String> ids = (List<String>) map.get("entryIds");
                for(String tmpEntryId : ids) {
                    if(tmpEntryId.equals(pageId)) {
                        toRemove = key;
                        break;
                    }
                }
            }
        }
        
        if(toRemove != null) {
            manager.getCacheManager().removeFromCache(toRemove, manager.storeId, addr);
        }
        
    }    

    @Override
    public List<Product> getLatestProducts(int count) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
