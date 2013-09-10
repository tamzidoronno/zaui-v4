package com.thundashop.core.productmanager;

import com.thundashop.core.productmanager.data.AttributeSummary;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Events;
import com.thundashop.core.common.ExchangeConvert;
import com.thundashop.core.common.Logger;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.productmanager.data.AttributeValue;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductCriteria;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * ProductManager is essential for a webshop. You can look at productmanager
 * <br>
 * as a productcontainer, this container contains all the products for the
 * webshop<br>
 * <br>
 * It has a set of API functions that can be used to receive data from it, and
 * should<br>
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
    }

    @Override
    public Product saveProduct(Product product) throws ErrorException {
        if (product.id == null || product.id.equals("")) {
            throw new ErrorException(87);
        }


        product.attributes = new ArrayList();
        for (String group : product.attributesToSave.keySet()) {
            AttributeValue value = pool.findAttributeValue(group, product.attributesToSave.get(group));
            product.attributes.add(value.id);

        }

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

        if (product == null) {
            throw new ErrorException(27);
        }

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
        for (String id : entryIds) {
            for (Product prod : products.values()) {
                if (prod.pageId.equals(id)) {
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
    public List<AttributeValue> getAllAttributes() throws ErrorException {
        return pool.getAll();
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
        if (result.isEmpty()) {
            throw new ErrorException(1011);
        }
        ProductCriteria criteria = new ProductCriteria();
        criteria.pageIds.add(result.get(app_uuid).get(0));
        List<Product> foundresult = getProducts(criteria);
        if (foundresult.isEmpty()) {
            throw new ErrorException(1011);
        }
        return foundresult.get(0);
    }

    @Override
    public List<Product> getAllProducts() throws ErrorException {
        ArrayList<Product> list = new ArrayList(products.values());
        Comparator<Product> comparator = new Comparator<Product>() {
            public int compare(Product c1, Product c2) {
                return c1.rowCreatedDate.compareTo(c2.rowCreatedDate);
            }
        };
        
        Collections.sort(list, comparator); 
        return list;
    }

    @Override
    public void updateAttributePool(List<AttributeValue> groups) throws ErrorException {
        List<String> valuesAdded = new ArrayList();
        for (AttributeValue val : groups) {
            val.storeId = storeId;
            databaseSaver.saveObject(val, credentials);
            pool.addAttributeValue(val);
            valuesAdded.add(val.id);
        }

        for (AttributeValue value : pool.getAll()) {
            if (!valuesAdded.contains(value.id)) {
                databaseSaver.deleteObject(value, credentials);
                pool.remove(value.id);

                for (Product product : products.values()) {
                    if (product.attributes != null && product.attributes.contains(value.id)) {
                        product.attributes.remove(value.id);
                        databaseSaver.saveObject(product, credentials);
                    }
                }
            }
        }

    }
}