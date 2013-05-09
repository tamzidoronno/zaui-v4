package com.thundashop.core.productmanager;

import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Events;
import com.thundashop.core.common.Logger;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.productmanager.data.AttributeGroup;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductCriteria;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * ProductManager is essential for a webshop. You can look at productmanager <br>
 * as a productcontainer, this container contains all the products for the webshop<br>
 * <br>
 * It has a set of API functions that can be used to receive data from it, and should<br>
 * be pretty straight forward to use.
 */
@Component
@Scope("prototype")
public class ProductManager extends AProductManager implements IProductManager {
 
    @Autowired
    public ProductManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    
    @Override
     public void onReady() {
        pool.initialize(credentials, storeId, databaseSaver);
     }

    @Override
    public Product saveProduct(Product product) throws ErrorException {
        if (product.id == null || product.id.equals(""))
            throw new ErrorException(87);        
        product.storeId = storeId;
        databaseSaver.saveObject(product, credentials);
        products.put(product.id, product);
        return product;
    }

    @Override
    public void changeStockQuantity(String productId, int count) throws ErrorException {
        Product product = products.get(productId);
        product.stockQuantity = product.stockQuantity + count;
        saveProduct(product);
    }

    @Override
    public List<Product> getProducts(ProductCriteria searchCriteria) throws ErrorException {
        return super.getProducts(searchCriteria);
    }
    
    @Override
    public void removeProduct(String productId) throws ErrorException {
        Product product = products.get(productId);
        
        if (product == null)
            throw new ErrorException(27);
        
        products.remove(product.id);
        databaseSaver.deleteObject(product, credentials);
        
        throwEvent(Events.PRODUCT_DELETED, productId);
        ListManager manager = getManager(ListManager.class);
        manager.removeProductFromListsIfExists(productId);

    }

    @Override
    public Product createProduct() throws ErrorException {
        Product product = new Product();
        product.storeId = storeId;
        product.id = UUID.randomUUID().toString();
        createProductPage(product);
        saveProduct(product);
        
        return product;
    }
    
    @Override
    public ArrayList<Product> getRandomProducts(Integer fetchSize, String ignoreProductId) throws ErrorException {
        return randomProducts(ignoreProductId, fetchSize);
    }

    @Override
    public HashMap<String, String> translateEntries(List<String> entryIds) throws ErrorException {
        HashMap result = new HashMap();
        for(String id : entryIds) {
            for(Product prod : products.values()) {
                if(prod.pageId.equals(id)) {
                    result.put(id, prod.name);
                }
            }
        }
        return result;
    }

    @Override
    public void setMainImage(String productId, String imageId) throws ErrorException {
        Product product = getProduct(productId);
        product.setMainImage(imageId);
        saveProduct(product);
    }
    
    @Override
    public void addImage(String productId, String productImageId, String description) throws ErrorException {
        Product product = getProduct(productId);
        product.addImage(productImageId, description);
        saveProduct(product);
    }
    
    @Override
    public Product getProduct(String productId) throws ErrorException {
        return super.getProduct(productId);
    }

    @Override
    public List<Product> getLatestProducts(int count) throws ErrorException {
        return super.latestProducts(count);
    }

    @Override
    public void addAttributeGroupToProduct(String productId, String attributeGroup, String attribute) throws ErrorException {
        if(attributeGroup.trim().length() == 0) {
            return;
        }
        Product product = getProduct(productId);
        String value = pool.getAttribute(attributeGroup, attribute);
        AttributeGroup group = pool.getAttributeGroup(attributeGroup);
        product.addAttribute(group.id, value);
        saveProduct(product);
    }

    @Override
    public void removeAttributeGroupFromProduct(String productId, String attributeGroupId) throws ErrorException {
        Product product = getProduct(productId);
        product.attributes.remove(attributeGroupId);
        saveProduct(product);
    }
    
}