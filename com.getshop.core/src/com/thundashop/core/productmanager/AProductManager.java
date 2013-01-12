package com.thundashop.core.productmanager;

import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.IPageManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductCriteria;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author ktonder
 */
public class AProductManager extends ManagerBase {
    protected HashMap<String, Product> products = new HashMap();

    public AProductManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon object : data.data) {
            if (object instanceof Product) {
                Product product = (Product) object;
                products.put(product.id, product);
            }
        }
    }
    
    protected void createProductPage(Product product) throws ErrorException {
        if (product.page == null) {
            IPageManager pageManager = getManager(PageManager.class);
            product.page = pageManager.createPage(Page.PageType.HeaderFooterLeftMiddleRight, "");
            pageManager.addApplicationToPage(product.page.id, "ProductManager", "middle");
        }
    }
    
    protected Product getProduct(String productId) throws ErrorException {
        Product product = products.get(productId);
        if (product == null)
            throw new ErrorException(1011);
        
        return product;
    }   
    
    protected ArrayList<Product> randomProducts(String ignoreProductId, int fetchSize) {
        List<Product> randomProducts = new ArrayList<>();
        
        for (Product product : products.values()) {
            if (product.page.id.equals(ignoreProductId) || product.id.equals(ignoreProductId))
                continue;
            
            randomProducts.add(product);
        }
        
        Collections.shuffle(randomProducts);
        if (randomProducts.size() < fetchSize)
            fetchSize = randomProducts.size();
        
        return new ArrayList<>(randomProducts.subList(0, fetchSize));
    }

    protected List<Product> getProducts(ProductCriteria searchCriteria) {
        ArrayList<Product> retProducts = new ArrayList();
        for (Product product : products.values()) {
            if (product.check(searchCriteria))
                retProducts.add(product);
        }
        
        return retProducts;
    }
}