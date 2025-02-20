package com.thundashop.core.productmanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.listmanager.data.Entry;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.productmanager.data.*;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.zauiactivity.ZauiActivityManager;
import com.thundashop.zauiactivity.dto.ZauiActivity;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ktonder
 */
public abstract class AProductManager extends ManagerBase {

    HashMap<String, ProductList> productList = new HashMap<>();
    HashMap<String, ProductCategory> categories = new HashMap<>();    

    private final Map<String, Product> products = new HashMap<>();
    public ProductConfiguration productConfiguration = new ProductConfiguration();
    HashMap<Integer, TaxGroup> taxGroups = new HashMap<>();
    
    HashMap<Integer, AccountingDetail> accountingAccountDetails = new HashMap<>();

    private Set<Product> sortedProducts;
    
    @Autowired
    PageManager pageManager;

    @Autowired
    ListManager listManager;

    @Autowired
    ZauiActivityManager zauiActivityManager;
    
    protected Product finalize(Product product) throws ErrorException {
        if (product == null) {
            return null;
        }
        
        if (product != null && product.pageId != null && product.page == null) {
            product.page = pageManager.getPage(product.pageId);
        }
        
        if (taxGroups.get(1) != null && product.taxGroupObject == null && product.taxgroup == -1) {
            product.taxGroupObject = taxGroups.get(1);
            product.taxgroup = 1;
        } else {
            product.taxGroupObject = taxGroups.get(product.taxgroup);
        }
        checkIncrementalProductId(product);
        
        setOriginalPriceIfNull(product);
        setGroupPrice(product);
        addSubProductsToTransientVariable(product);
        updateAdditionalTaxGroups(product);
        
        for (ProductImage image : product.images.values()) {
            if (!product.imagesAdded.contains(image.fileId)) {
                product.imagesAdded.add(image.fileId);
            }
        }
        
        product.uniqueName = product.name;
        ensureUniqueNameWhenDuplicate(product);
    
        if (product.pageId != null && !product.pageId.isEmpty() && !product.selectedProductTemplate.equals(product.currentSelectedProducTemplate)) {
            Page page = pageManager.getPage(product.pageId);
            if (page.isASlavePage() && !page.masterPageId.equals(product.selectedProductTemplate)) {
                pageManager.changeTemplateForPage(product.pageId, product.selectedProductTemplate);
            }
            
            product.currentSelectedProducTemplate = product.selectedProductTemplate;
            saveObject(product);
        }
        
        product.doFinalize();
        
        product.variations = listManager.getJsTree("variationslist_product_"+product.id);
        if (product.variations != null && product.variations.nodes.isEmpty()) {
            product.variations = null;
        }
        
        TaxGroup taxGroup = getTaxGroupAbstract(product.taxgroup);
        addOverrideTaxGroup(taxGroup, product);
        
        List<TaxGroup> additionalTaxGroups = new ArrayList<>(product.additionalTaxGroupObjects);
        for (TaxGroup additionalTaxGroup : additionalTaxGroups) {
            addOverrideTaxGroup(additionalTaxGroup, product);
        }
        
//        updateTranslation(product);
        return product;
    }

    private void addOverrideTaxGroup(TaxGroup taxGroup, Product product) {
        if (taxGroup != null && taxGroup.overrideTaxGroups != null && !taxGroup.overrideTaxGroups.isEmpty()) {
            for (OverrideTaxGroup overrideTaxGroup : taxGroup.overrideTaxGroups) {
                if (!containsTaxGroup(overrideTaxGroup, product)) {
                    TaxGroup toAdd = getTaxGroupAbstract(overrideTaxGroup.groupNumber);
                    if (toAdd != null) {
                        product.additionalTaxGroupObjects.add(toAdd);
                    }
                }
            }   
        }
    }
    
    public abstract TaxGroup getTaxGroupAbstract(int taxGroupNumber);

    private void setOriginalPriceIfNull(Product product) {
        if (product.original_price == null) {
            product.original_price = product.price;
        }
    }

    private void ensureUniqueNameWhenDuplicate(Product product) {
        Set<Product> sortedProducts = getSortedProducts();

        int i = 0;
        
        for (Product iproduct : sortedProducts) {
            
            if (iproduct.name != null
                    && product.name != null 
                    && iproduct.name.equalsIgnoreCase(product.name)) {
                
                i++;
                
                if (product.id.equals(iproduct.id) && i > 1) {
                    product.uniqueName = product.name + "_" + i;    
                }
            }
        }
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon object : data.data) {
            if (object instanceof Product) {
                Product product = (Product) object;
                finalize(product);
                setProductById(product.id, product);
            }
            if (object instanceof ProductList) {
                ProductList list = (ProductList) object;
                productList.put(list.id, list);
            }
            if (object instanceof ProductCategory) {
                ProductCategory cat = (ProductCategory) object;
                categories.put(cat.id, cat);
            }
            if (object instanceof ProductConfiguration) {
                productConfiguration = (ProductConfiguration)object;
            }
            if (object instanceof TaxGroup) {
                TaxGroup group = (TaxGroup) object;
                taxGroups.put(group.groupNumber, group);
            }
            if (object instanceof AccountingDetail) {
                AccountingDetail detail = (AccountingDetail) object;
                accountingAccountDetails.put(detail.accountNumber, detail);
            }
        }
    }

    protected Product getProduct(String productId) throws ErrorException {
        Product product = getProductById(productId);
        if (product == null) {
            ZauiActivity activity = zauiActivityManager.getZauiActivity(productId);
            if (activity != null) {
                return activity;
            }
            return null;
        }
        product = finalize(product);

        return product;
    }

    protected ArrayList<Product> randomProducts(String ignoreProductId, int fetchSize) throws ErrorException {
        List<Product> randomProducts = new ArrayList<>();

        for (Product product : getProducts()) {
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
        ArrayList<Product> retProducts = new ArrayList<>();
        for (Product product : getProducts()) {
            if (product.check(searchCriteria) || searchCriteria.pageIds.contains(product.pageId)) {
                product = finalize(product);
                retProducts.add(product);
            }
        }

        if (searchCriteria.listId != null && searchCriteria.listId.trim().length() > 0) {
            List<Entry> list = listManager.getList(searchCriteria.listId);
            for (Entry entry : list) {
                Product product = getProductById(entry.productId);
                if (product == null) {
                    continue;
                }
                product = finalize(product);
                retProducts.add(product);


            }
        }

        return retProducts;
    }

    protected List<Product> latestProducts(int count) throws ErrorException {
        ArrayList<Product> result = new ArrayList<>();
        for (Product product : getProducts()) {
            product = finalize(product);
            result.add(product);
        }

        Collections.sort(result);
        ArrayList<Product> limitedResult = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            if (i == count) {
                break;
            }
            limitedResult.add(result.get(i));
        }
        return limitedResult;
    }

    public Product findProductByPage(String id) {
        for (Product product : getProducts()) {
            if (product.pageId != null && product.pageId.equals(id)) {
                return product;
            }
        }
        return null;
    }

    public SearchResult search(String searchWord, Integer pageSize, Integer page) {
        List<Product> filteredProducts = getProductIdsThatMatchSearchWord(searchWord);
        List<Product> retProducts = new ArrayList<>(filteredProducts);

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

        List<Product> finalizedProducts = new ArrayList<>();
        retProducts.stream().forEach(p -> finalizedProducts.add(finalize(p)));
        result.products = finalizedProducts;

        if(page != null){
            result.pageNumber = page;
        }      

        return result;
    }

    private List<Product> getProductIdsThatMatchSearchWord(String searchWord) {
        Set<String> filteredProductIds = new HashSet<>();
        if (searchWord == null || searchWord.isEmpty()) {
            getProducts().forEach(p -> filteredProductIds.add(p.id));
        }

        if (searchWord != null) {
            List<String> splittedSearchWord = Arrays.asList(searchWord.split(" "));

                getProducts().stream()
                    .filter(p -> p.name != null && !p.name.isEmpty())
                    .filter(p -> matchSearchWords(p.name, splittedSearchWord))
                    .forEach(p -> filteredProductIds.add(p.id));
        }

        List<Product> retProducts = new ArrayList<>();
        for (String productId : filteredProductIds) {
            Product product = getProductById(productId);
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

    private void setGroupPrice(Product product) {
        if (getSession() == null || getSession().currentUser == null) {
            return;
        }
        
        User currentUser = getSession().currentUser;
        if (currentUser.groups == null) {
            return;
        }
        
        for (String groupId : currentUser.groups) {
            Double groupPrice = product.groupPrice.get(groupId);
            if (groupPrice != null) {
                product.price = groupPrice;
            }
        }
        
    }

    private void addSubProductsToTransientVariable(Product product) {
        product.subProducts.clear();
        
        for (String subProductId : product.subProductIds) {
            Product iproduct = getProductById(subProductId);
            finalize(iproduct);
            product.subProducts.add(iproduct);
        }
    }

    private void checkIncrementalProductId(Product product) {
        if(product.incrementalProductId == null) {
            productConfiguration.incrementalProductId++;
            product.incrementalProductId = productConfiguration.incrementalProductId;
            saveObject(productConfiguration);
            saveObject(product);
        }
    }
    
    public AccountingDetail getAccountingDetail(int accountNumber) {
        if (accountNumber == 2900) {
            AccountingDetail forskudd = new AccountingDetail();
            forskudd.accountNumber = 2900;
            forskudd.description = "Forskudd fra kunder";
            forskudd.taxgroup = 0;
            return forskudd;
        }
        return accountingAccountDetails.get(accountNumber);
    }
    
    public List<AccountingDetail> getAccountingAccounts() {
        ArrayList<AccountingDetail> result = new ArrayList<>(accountingAccountDetails.values());
        Collections.sort(result);
        return result;
    }
    
    public void saveAccountingDetail(AccountingDetail detail) {
        AccountingDetail alreadyExists = getAccountingDetail(detail.accountNumber);
        if (alreadyExists != null) {
            detail.id = alreadyExists.id;
        }
        
        saveObject(detail);
        accountingAccountDetails.put(detail.accountNumber, detail);
    }

    private void updateAdditionalTaxGroups(Product product) {
        if (product.additionalTaxGroupObjects.isEmpty())
            return;
        
        List<TaxGroup> groups = product.additionalTaxGroupObjects.stream()
                .filter(group -> group != null)
                .filter(group -> taxGroups.get(group.groupNumber) != null)
                .map(group -> taxGroups.get(group.groupNumber))
                .collect(Collectors.toList());
        
        if (product.additionalTaxGroupObjects.size() == groups.size()) {
            product.additionalTaxGroupObjects = groups;
        }
    }

    void doubleCheckAndCorrectAccounts(List<TaxGroup> taxlist) {
        
        HashMap<Integer, TaxGroup> taxesByAccounting = new HashMap<>();
        for(TaxGroup tax : taxlist) {
            taxesByAccounting.put(tax.accountingTaxGroupId, tax);
        }
        
        
        HashMap<Integer, TaxGroup> taxesByGetShop = new HashMap<>();
        for(TaxGroup tax : taxlist) {
            taxesByGetShop.put(tax.groupNumber, tax);
        }
        
        for(AccountingDetail detail : accountingAccountDetails.values()) {
            if(detail.getShopTaxGroup == -1 && detail.taxgroup > -1) {
                TaxGroup tax = taxesByAccounting.get(detail.taxgroup);
                if(tax != null) {
                    detail.getShopTaxGroup = tax.groupNumber;
                    saveAccountingDetail(detail);
                }
            }
            if(detail.getShopTaxGroup >= 0 && detail.taxgroup == -1) {
                TaxGroup tax = taxesByGetShop.get(detail.getShopTaxGroup);
                if(tax != null) {
                    detail.taxgroup = tax.accountingTaxGroupId;
                    saveAccountingDetail(detail);
                }
                
            }
        }
    }

    private boolean containsTaxGroup(OverrideTaxGroup overrideTaxGroup, Product product) {
        
        if (overrideTaxGroup.groupNumber == product.taxgroup) {
            return true;
        }
        
        for (TaxGroup add : product.additionalTaxGroupObjects) {
            if (add.groupNumber == overrideTaxGroup.groupNumber) {
                return true;
            }
        }
        
        return false;
    }

    public Product getProductById(String productId) {
        return products.get(productId);
    }

    public Product setProductById(String productId, Product product) {
        nullifySortedProducts();
        return products.put(productId, product);
    }

    public Product removeProductById(String productId) {
        nullifySortedProducts();
        return products.remove(productId);
    }

    public boolean isProductExist(String productId) {
        return products.containsKey(productId);
    }

    public Collection<Product> getProducts() {
        return products.values();
    }

    public Set<Product> getSortedProducts() {
        Set<Product> set = sortedProducts;

        if (set == null) {
            set = new TreeSet<>(getProducts());
            setSortedProducts(set);
        }

        return set;
    }

    public void setSortedProducts(Set<Product> sortedProducts) {
        this.sortedProducts = sortedProducts;
    }

    public void nullifySortedProducts() {
        sortedProducts = null;
    }
}
