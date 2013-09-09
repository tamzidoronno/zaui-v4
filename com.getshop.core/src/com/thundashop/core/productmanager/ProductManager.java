package com.thundashop.core.productmanager;

import com.thundashop.core.productmanager.data.AttributeSummary;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Events;
import com.thundashop.core.common.ExchangeConvert;
import com.thundashop.core.common.Logger;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.pagemanager.PageManager;
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
    public HashMap<String, AttributeGroup> getAllAttributes() throws ErrorException {
        return pool.getAll();
    }
    
    @Override
    public void removeAttributeGroupFromProduct(String productId, String attributeGroupId) throws ErrorException {
        Product product = getProduct(productId);
        product.attributes.remove(attributeGroupId);
        saveProduct(product);
    }

    @Override
    public void renameAttributeGroupName(String oldName, String newName) throws ErrorException {
        pool.renameGroup(oldName, newName);
    }

    @Override
    public void renameAttribute(String groupName, String oldAttributeName, String newAttributeName) throws ErrorException {
        
        if(pool.attributeExists(groupName, newAttributeName)) {
            throw new ErrorException(96);
        }
        
        AttributeGroup group = pool.getAttributeGroup(groupName);
        oldAttributeName = pool.getAttribute(groupName, oldAttributeName);
        pool.renameAttribute(groupName, oldAttributeName, newAttributeName);
        
        //Check all products and rename them.
        for(Product prod : products.values()) {
            if(prod.attributes != null) {
                String value = prod.attributes.get(group.id);
                if(value != null && value.equals(oldAttributeName)) {
                    prod.attributes.put(group.id, newAttributeName);
                    saveProduct(prod);
                }
            }
        }
    }

    @Override
    public void deleteGroup(String groupName) throws ErrorException {
        AttributeGroup group = pool.getAttributeGroup(groupName);
        pool.deleteGroup(groupName);
        for(Product prod : products.values()) {
            if(prod.attributes != null) {
                if(prod.attributes.containsKey(group.id)) {
                    prod.attributes.remove(group.id);
                    saveProduct(prod);
                }
            }
        }
    }

    @Override
    public void deleteAttribute(String groupName, String attribute) throws ErrorException {
        AttributeGroup group = pool.getAttributeGroup(groupName);
        attribute = pool.getAttribute(groupName, attribute);
        pool.deleteAttribute(groupName, attribute);
        
        //Check all products and rename them.
        for(Product prod : products.values()) {
            if(prod.attributes != null) {
                String value = prod.attributes.get(group.id);
                if(value != null && value.equals(attribute)) {
                    prod.attributes.remove(group.id);
                    saveProduct(prod);
                }
            }
        }
    }

    @Override
    public AttributeSummary getAttributeSummary() throws ErrorException {
        return cachedResult;
    }

    @Override
    public Double getPrice(String productId, List<String> variations) throws ErrorException {
        Product product = getProduct(productId);
        if (variations == null || variations.isEmpty() || product == null) {
            return product.price;
        }
        
        double conversionRate = 1.0;
        if (getSession().currentUser == null || !getSession().currentUser.isAdministrator()) {
            conversionRate = ExchangeConvert.getExchangeRate(getSettings("Settings"));
        }
        
        return product.getPrice(variations, conversionRate);
    }

    @Override
    public Product getProductFromApplicationId(String app_uuid) throws ErrorException {
        PageManager pmgr = getManager(PageManager.class);
        List<String> uuidlist = new ArrayList();
        uuidlist.add(app_uuid);
        HashMap<String, List<String>> result = pmgr.getPagesForApplications(uuidlist);
        if(result.isEmpty()) {
            throw new ErrorException(1011);
        }
        ProductCriteria criteria = new ProductCriteria();
        criteria.pageIds.add(result.get(app_uuid).get(0));
        List<Product> foundresult = getProducts(criteria);
        if(foundresult.isEmpty()) {
            throw new ErrorException(1011);
        }
        return foundresult.get(0);
    }

    @Override
    public List<Product> getAllProducts() throws ErrorException {
        return new ArrayList(products.values());
    }

    @Override
    public void updateAttributePool(List<AttributeGroup> groups) throws ErrorException {
        //Adding all attributes.
        
        List<String> allvalues = new ArrayList();
        for(AttributeGroup group : groups) {
            for(String value : group.attributes) {
                pool.getAttribute(group.groupName, value);
            }
        }
        
        pool.compareAndRemove(groups);
        
    }
    
}