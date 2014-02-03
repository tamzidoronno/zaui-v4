package com.thundashop.core.productmanager;

import com.thundashop.app.content.ContentManager;
import com.thundashop.core.productmanager.data.AttributeSummary;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.listmanager.data.Entry;
import com.thundashop.core.pagemanager.IPageManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductCriteria;
import com.thundashop.core.common.ExchangeConvert;
import com.thundashop.core.productmanager.data.AttributeData;
import com.thundashop.core.productmanager.data.AttributeValue;
import com.thundashop.core.productmanager.data.TaxGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author ktonder
 */
public class AProductManager extends ManagerBase {

    protected HashMap<String, Product> products = new HashMap();
    AttributeData pool = new AttributeData();
    AttributeSummary cachedResult;
    public HashMap<Integer, TaxGroup> taxGroups = new HashMap();

    public AProductManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    public Product finalize(Product product) throws ErrorException {
        PageManager manager = getManager(PageManager.class);
        ContentManager content = getManager(ContentManager.class);

        if (product != null && product.pageId != null && product.page == null) {
            product.page = manager.getPage(product.pageId);
        }

        if (product != null) {
            product = product.clone();
            if (getSession().currentUser == null || !getSession().currentUser.isAdministrator()) {
                product.price = ExchangeConvert.calculateExchangeRate(getSettings("Settings"), product.price);
            }
        }

        product.attributesAdded = new HashMap();
        for (String attrid : product.attributes) {
            AttributeValue val = pool.getAttributeByValueId(attrid);
            if (val != null) {
                product.attributesAdded.put(val.groupName, val.value);
            }
        }
        
        if (taxGroups.get(1) != null && product.taxGroupObject == null && product.taxgroup == -1) {
            product.taxGroupObject = taxGroups.get(1);
            product.taxgroup = 1;
        } else {
            product.taxGroupObject = taxGroups.get(product.taxgroup);
        }
        
        HashMap<String, AppConfiguration> apps = product.page.getApplications();
        
        //Adding text
        if(product.page.pageType == 2) {
            for(AppConfiguration config : apps.values()) {
                if(config.appName.equals("ContentManager")) {
                    product.descriptions.add(content.getContent(config.id));
                }
            }

            //Adding the images.
            for(AppConfiguration config : apps.values()) {
                if(config.appName.equals("ImageDisplayer")) {
                    if(config.settings.get("image") != null) {
                        product.imagesAdded.add(config.settings.get("image").value);
                    }
                }
            }
        }
        
        return product;
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon object : data.data) {
            if (object instanceof Product) {
                Product product = (Product) object;
                products.put(product.id, product);
            }
            if (object instanceof AttributeValue) {
                pool.addAttributeValue((AttributeValue) object);
            }
            if (object instanceof TaxGroup) {
                TaxGroup group = (TaxGroup) object;
                taxGroups.put(group.groupNumber, group);
            }
        }
    }

    protected void createProductPage(Product product) throws ErrorException {
        if (product.page == null) {
            IPageManager pageManager = getManager(PageManager.class);
            product.page = pageManager.createPage(Page.LayoutType.HeaderMiddleFooter, "");
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
        if (product == null) {
            throw new ErrorException(1011);
        }
        product = finalize(product);

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

        cachedResult = new AttributeSummary(pool);

        if (searchCriteria.listId != null && searchCriteria.listId.trim().length() > 0) {
            ListManager manager = getManager(ListManager.class);
            List<Entry> list = manager.getList(searchCriteria.listId);
            for (Entry entry : list) {
                Product product = products.get(entry.productId);
                if (product == null) {
                    continue;
                }
                product = finalize(product);
                retProducts.add(product);

                if (searchCriteria.attributeFilter.isEmpty()) {
                    cachedResult.addToSummary(product);
                }

            }
        }


        if (searchCriteria.attributeFilter.size() > 0) {
            ArrayList<Product> filteredProducts = new ArrayList();
            for (Product prod : retProducts) {
                boolean found = true;
                for (String groupId : searchCriteria.attributeFilter.keySet()) {
                    if (prod.attributes.contains(groupId)) {
                        found = true;
                    } else {
                        found = false;
                        break;
                    }
                }
                if(found) {
                    filteredProducts.add(prod);
                    cachedResult.addToSummary(prod);
                }
            }
            retProducts = filteredProducts;
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
    
    public Product findProductByPage(String id) {
        for(Product product : products.values()) {
            if(product.pageId!= null && product.pageId.equals(id)) {
                return product;
            }
        }
        return null;
    }
}
