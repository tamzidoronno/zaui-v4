package com.thundashop.core.productmanager;

import com.thundashop.app.content.ContentManager;
import com.thundashop.core.common.*;
import com.thundashop.core.common.ExchangeConvert;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.listmanager.data.Entry;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pagemanager.IPageManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.productmanager.data.AttributeData;
import com.thundashop.core.productmanager.data.AttributeSummary;
import com.thundashop.core.productmanager.data.AttributeValue;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductConfiguration;
import com.thundashop.core.productmanager.data.ProductCriteria;
import com.thundashop.core.productmanager.data.ProductImage;
import com.thundashop.core.productmanager.data.ProductList;
import com.thundashop.core.productmanager.data.SearchResult;
import com.thundashop.core.productmanager.data.TaxGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ktonder
 */
public abstract class AProductManager extends ManagerBase {

    HashMap<String, ProductList> productList = new HashMap();

    protected HashMap<String, Product> products = new HashMap();
    AttributeData pool = new AttributeData();
    AttributeSummary cachedResult;
    public ProductConfiguration productConfiguration = new ProductConfiguration();
    public HashMap<Integer, TaxGroup> taxGroups = new HashMap();

    @Autowired
    public PageManager pageManager;

    @Autowired
    private ContentManager contentManager;

    @Autowired
    public ListManager listManager;

    protected Product finalize(Product product) throws ErrorException {
        if (product != null && product.pageId != null && product.page == null) {
            product.page = pageManager.getPage(product.pageId);
        }

        if (product.pageId == null) {
            product.pageId = pageManager.createPage().id;
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

        Page page = pageManager.getPage(product.pageId);

        if (product.original_price == null) {
            product.original_price = product.price;
        }
        if (product.campaing_start_date > 0 && product.campaing_end_date > 0) {
            Date startDate = new Date(product.campaing_start_date * 1000);
            Date endDate = new Date(product.campaing_end_date * 1000);
            Date now = new Date(System.currentTimeMillis());
            if (startDate.before(now) && endDate.after(now)) {
                product.price = product.campaign_price;
            } else {
                product.price = product.original_price;
            }

        }
        for (ProductImage image : product.images.values()) {
            if (!product.imagesAdded.contains(image.fileId)) {
                product.imagesAdded.add(image.fileId);
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
            if (object instanceof ProductList) {
                ProductList list = (ProductList) object;
                productList.put(list.id, list);
            }
            if (object instanceof TaxGroup) {
                TaxGroup group = (TaxGroup) object;
                taxGroups.put(group.groupNumber, group);
            }
        }
    }

    protected Product getProduct(String productId) throws ErrorException {
        Product product = products.get(productId);
        if (product == null) {
            return null;
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
            List<Entry> list = listManager.getList(searchCriteria.listId);
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
                if (found) {
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
        for (Product product : products.values()) {
            if (product.pageId != null && product.pageId.equals(id)) {
                return product;
            }
        }
        return null;
    }

    public SearchResult search(String searchWord, Integer pageSize, Integer page) {
        List<Product> filteredProducts = getProductIdsThatMatchSearchWord(searchWord);
        List<Product> retProducts = new ArrayList(filteredProducts);

        SearchResult result = new SearchResult();

        if (page != null && pageSize != null) {
            int from = (page - 1) * pageSize;
            int to = pageSize * page;

            if (to > filteredProducts.size()) {
                to = filteredProducts.size();
            }

            try {
                retProducts = filteredProducts.subList(from, to);
            } catch (IllegalArgumentException ex) {
                return null;
            }

            double pages = (double) filteredProducts.size() / (double) pageSize;
            if (pages == 0) {
                pages = 1;
            }

            result.pages = (int) Math.ceil(pages);
        }

        List<Product> finalizedProducts = new ArrayList();
        retProducts.stream().forEach(p -> finalizedProducts.add(finalize(p)));
        result.products = finalizedProducts;
        result.pageNumber = page;

        return result;
    }

    private List<Product> getProductIdsThatMatchSearchWord(String searchWord) {
        Set<String> filteredProductIds = new HashSet();
        if (searchWord == null || searchWord.isEmpty()) {
            products.values().stream().forEach(p -> filteredProductIds.add(p.id));
        }

        if (searchWord != null) {
            List<String> splittedSearchWord = Arrays.asList(searchWord.split(" "));

                products.values().stream()
                    .filter(p -> p.name != null && !p.name.isEmpty())
                    .filter(p -> matchSearchWords(p.name, splittedSearchWord))
                    .forEach(p -> filteredProductIds.add(p.id));
        }

        List<Product> retProducts = new ArrayList();
        for (String productId : filteredProductIds) {
            Product product = products.get(productId);
            if (product != null) {
                retProducts.add(product);
            }
        }

        return retProducts;
    }

    private boolean matchSearchWords(String name, List<String> splittedSearchWord) {
        for (String search : splittedSearchWord) {
            if (!name.toLowerCase().contains(search.toLowerCase())) {
                return false;
            }
        }
        
        return true;
    }
    
    public void setProductDynamicPrice(String productId, int count) {
    }
}
