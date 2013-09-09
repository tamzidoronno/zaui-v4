package com.thundashop.core.productmanager;

import com.thundashop.core.productmanager.data.AttributeSummary;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.listmanager.data.Entry;
import com.thundashop.core.pagemanager.IPageManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.productmanager.data.AttributeGroup;
import com.thundashop.core.productmanager.data.AttributeSummaryEntry;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductCriteria;
import com.thundashop.core.common.ExchangeConvert;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author ktonder
 */
public class AProductManager extends ManagerBase {

    protected HashMap<String, Product> products = new HashMap();
    AttributePool pool;
    AttributeSummary cachedResult;

    public AProductManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    private Product finalize(Product product) throws ErrorException {
        PageManager manager = getManager(PageManager.class);
        
        if (product != null && product.pageId != null && product.page == null) {
            product.page = manager.getPage(product.pageId);
        }

        if (product != null && pool != null) {
            product.attributesList = pool.attributeGroups;
        }

        if (product != null) {
            product = product.clone();
            if (getSession().currentUser == null || !getSession().currentUser.isAdministrator()) {
                product.price = ExchangeConvert.calculateExchangeRate(getSettings("Settings"), product.price);
            }
        }
        
        return product;
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        pool = new AttributePool();
        for (DataCommon object : data.data) {
            if (object instanceof Product) {
                Product product = (Product) object;
                products.put(product.id, product);
            }
            if (object instanceof AttributeGroup) {
                pool.addFromDatabase((AttributeGroup) object);
            }
        }
    }

    protected void createProductPage(Product product) throws ErrorException {
        if (product.page == null) {
            IPageManager pageManager = getManager(PageManager.class);
            product.page = pageManager.createPage(Page.PageType.HeaderMiddleFooter, "");
            product.pageId = product.page.id;
            //
            AppConfiguration config = pageManager.addApplicationToPage(product.page.id, "dcd22afc-79ba-4463-bb5c-38925468ae26", "main_1");
            
            Setting setting = new Setting();
            setting.type = "productid";
            setting.secure = false;
            setting.value = product.id;
            config.settings.put(setting.type, setting);
            pageManager.saveApplicationConfiguration(config);
        }
    }

    protected Product getProduct(String productId) throws ErrorException {
        Product product = products.get(productId);
        product = finalize(product);
        if (product == null) {
            throw new ErrorException(1011);
        }

        return product;
    }

    protected ArrayList<Product> randomProducts(String ignoreProductId, int fetchSize) throws ErrorException {
        List<Product> randomProducts = new ArrayList<>();

        for (Product product : products.values()) {
            product = finalize(product);
            if (product.page.id.equals(ignoreProductId) || product.id.equals(ignoreProductId)) {
                continue;
            }

            randomProducts.add(product);
        }

        Collections.shuffle(randomProducts);
        if (randomProducts.size() < fetchSize) {
            fetchSize = randomProducts.size();
        }

        return new ArrayList<>(randomProducts.subList(0, fetchSize));
    }

    protected List<Product> getProducts(ProductCriteria searchCriteria) throws ErrorException {
        ArrayList<Product> retProducts = new ArrayList();
        for (Product product : products.values()) {
            if (product.check(searchCriteria) || searchCriteria.pageIds.contains(product.pageId)) {
                product = finalize(product);
                retProducts.add(product);
            }
        }

        if (searchCriteria.listId != null && searchCriteria.listId.trim().length() > 0) {
            ListManager manager = getManager(ListManager.class);
            List<Entry> list = manager.getList(searchCriteria.listId);
            for (Entry entry : list) {
                Product product = products.get(entry.productId);
                product = finalize(product);
                retProducts.add(product);
            }
        }

        buildAttributeCache(retProducts);

        if (searchCriteria.attributeFilter.size() > 0) {
            ArrayList<Product> filteredProducts = new ArrayList();
            for (Product prod : retProducts) {
                boolean found = true;
                for (String groupId : searchCriteria.attributeFilter.keySet()) {
                    String filterValue = searchCriteria.attributeFilter.get(groupId);
                    String prodValue = prod.attributes.get(groupId);
                    if (prodValue == null) {
                        found = false;
                        break;
                    }

                    if (!prodValue.equals(filterValue)) {
                        found = false;
                        break;
                    }
                }

                if (found) {
                    filteredProducts.add(prod);
                }
            }
            retProducts = filteredProducts;
            cleanFilterCache(retProducts);

        }

        return retProducts;
    }

    protected List<Product> latestProducts(int count) throws ErrorException {
        ArrayList<Product> result = new ArrayList();
        for (Product product : products.values()) {
            product = finalize(product);
            result.add(product);
        }

        Collections.sort(result);
        ArrayList<Product> limitedResult = new ArrayList();
        for (int i = 0; i < result.size(); i++) {
            if (i == count) {
                break;
            }
            limitedResult.add(result.get(i));
        }
        return limitedResult;
    }

    private void buildAttributeCache(ArrayList<Product> retProducts) throws ErrorException {
        AttributeSummary cache = new AttributeSummary();
        cache.attributeCount = new HashMap();
        for (Product prod : retProducts) {
            if (prod != null && prod.attributes != null) {
                for (String groupId : prod.attributes.keySet()) {
                    AttributeGroup group = pool.getAttributeGroup(groupId);
                    if(group == null) {
                        continue;
                    }
                    if (cache.attributeCount.get(groupId) == null) {
                        AttributeSummaryEntry entry = new AttributeSummaryEntry();
                        entry.groupName = group.groupName;
                        cache.attributeCount.put(groupId, entry);
                    }

                    String value = prod.attributes.get(group.groupName);

                    AttributeSummaryEntry attributeCount = cache.attributeCount.get(groupId);
                    if (!value.isEmpty()) {
                        attributeCount.totalCount++;
                    }
                    if (attributeCount.attributeCount.get(value) == null) {
                        attributeCount.attributeCount.put(value, 1);
                    } else {
                        attributeCount.attributeCount.put(value, attributeCount.attributeCount.get(value) + 1);
                    }
                }
            }
        }
        cachedResult = cache;
    }

    private void cleanFilterCache(ArrayList<Product> retProducts) {
        for (String groupId : cachedResult.attributeCount.keySet()) {
            AttributeSummaryEntry group = cachedResult.attributeCount.get(groupId);
            for (String attr : group.attributeCount.keySet()) {
                group.attributeCount.put(attr, 0);
            }
            group.totalCount = 0;
        }

        for (Product prod : retProducts) {
            for (String groupId : prod.attributes.keySet()) {
                String value = prod.attributes.get(groupId);
                if (cachedResult.attributeCount.get(groupId) == null) {
                    continue;
                }
                int count = cachedResult.attributeCount.get(groupId).attributeCount.get(value);
                cachedResult.attributeCount.get(groupId).attributeCount.put(value, count + 1);
                cachedResult.attributeCount.get(groupId).totalCount++;
            }
        }
    }
}
