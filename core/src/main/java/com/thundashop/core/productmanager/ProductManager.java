package com.thundashop.core.productmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.central.GetShopCentral;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FilterOptions;
import com.thundashop.core.common.FilteredData;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pos.PosManager;
import com.thundashop.core.productmanager.data.AccountingDetail;
import com.thundashop.core.productmanager.data.OverrideTaxGroup;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductAccountingInformation;
import com.thundashop.core.productmanager.data.ProductCategory;
import com.thundashop.core.productmanager.data.ProductCriteria;
import com.thundashop.core.productmanager.data.ProductDynamicPrice;
import com.thundashop.core.productmanager.data.ProductLight;
import com.thundashop.core.productmanager.data.ProductList;
import com.thundashop.core.productmanager.data.SearchResult;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.storemanager.StoreManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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
@GetShopSession
public class ProductManager extends AProductManager implements IProductManager {
    @Autowired
    private PageManager pageManager;
    
    @Autowired
    private Database database;
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private StoreManager storeManager;
    
    @Autowired
    private PosManager posManager;
    
    @Autowired
    private GetShopCentral getShopCentral;
    

    @Override
    public Product saveProduct(Product product) throws ErrorException {
        if (product.id == null || product.id.equals("")) {
            throw new ErrorException(87);
        }
        product.original_price = product.price;

        product.doFinalize();
        
        saveObject(product);
        
        if (product.deleted == null) {
            setProductById(product.id, product);
        }

        return finalize(product);
    }

    @Override
    public void changeStockQuantity(String productId, int count) throws ErrorException {
        Product product = getProductById(productId);
        product.stockQuantity = product.stockQuantity + count;
        saveProduct(product);
    }
    
    public void changeStockQuantityForWareHouse(String productId, int count, String wareHouseId) throws ErrorException {
        Product product = getProductById(productId);
        Integer oldCount = product.wareHouseStockQuantities.get(wareHouseId);
        
        if (oldCount == null) {
            oldCount = 0;
        }
        
        oldCount = oldCount + count;
        
        product.wareHouseStockQuantities.put(wareHouseId, oldCount);
        saveProduct(product);
    }

    @Override
    public List<Product> getProducts(ProductCriteria searchCriteria) throws ErrorException {
        return super.getProducts(searchCriteria);
    }

    @Override
    public void removeProduct(String productId) throws ErrorException {
        Product product = getProductById(productId);

        if (product == null) {
            throw new ErrorException(27);
        }

        removeProductById(product.id);
        deleteObject(product);

        listManager.removeProductFromListsIfExists(productId);
    }

    @Override
    public Product createProduct() throws ErrorException {
        Product product = new Product();
        product.storeId = storeId;
        product.id = UUID.randomUUID().toString();
        
        product.pageId = pageManager.createPageFromTemplatePage("ecommerce_product_template_1").id;
        saveProduct(product);
        
        return finalize(product);
    }
    
    @Override
    public Product createProductWithAccount(Integer accountNumber) throws ErrorException {
        
        AccountingDetail account = getAccountingDetail(accountNumber);
        if(account == null && posManager.hasLockedPeriods() && !getShopCentral.hasBeenConnectedToCentral()) {
            return null;
        }
        
        if(account != null && account.getShopTaxGroup < 0) {
            return null;
        }
        
        Product product = new Product();
        product.storeId = storeId;
        product.id = UUID.randomUUID().toString();
        
        product.pageId = pageManager.createPageFromTemplatePage("ecommerce_product_template_1").id;
        
        
        if(account != null) {
            ProductAccountingInformation info = new ProductAccountingInformation();
            info.accountingNumber = account.accountNumber + "";
            info.taxGroupNumber = account.getShopTaxGroup;

            product.taxgroup = account.getShopTaxGroup;
            product.taxGroupObject = getTaxGroup(account.getShopTaxGroup);
            product.accountingConfig.add(info);
        }
        
        saveProduct(product);
        
        return finalize(product);
    }
    

    @Override
    public ArrayList<Product> getRandomProducts(Integer fetchSize, String ignoreProductId) throws ErrorException {
        return randomProducts(ignoreProductId, fetchSize);
    }

    @Override
    public HashMap<String, String> translateEntries(List<String> entryIds) throws ErrorException {
        HashMap result = new HashMap();
        for (String id : entryIds) {
            for (Product prod : getProducts()) {
                if (prod.pageId != null && prod.pageId.equals(id)) {
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
    public Product getProduct(String productId) throws ErrorException {
        return super.getProduct(productId);
    }

    @Override
    public List<Product> getLatestProducts(int count) throws ErrorException {
        return super.latestProducts(count);
    }


    @Override
    public Double getPrice(String productId, Map<String, String> variations) throws ErrorException {
        Product product = getProduct(productId);
        return product.getPrice(variations);
    }
    
    public Double getPriceWithoutDiscount(String productId, List<String> variations) throws ErrorException {
        Product product = getProduct(productId);
        return product.price;
    }

    @Override
    public Product getProductFromApplicationId(String app_uuid) throws ErrorException {
        List<String> uuidlist = new ArrayList();
        uuidlist.add(app_uuid);
        List<String> pageIds = pageManager.getPagesForApplication(app_uuid);
        if (pageIds.isEmpty()) {
            throw new ErrorException(1011);
        }
        ProductCriteria criteria = new ProductCriteria();
        criteria.pageIds.addAll(pageIds);
        List<Product> foundresult = getProducts(criteria);
        if (foundresult.isEmpty()) {
            throw new ErrorException(1011);
        }
        return foundresult.get(0);
    }

    @Override
    public List<Product> getAllProducts() throws ErrorException {
        long time = System.currentTimeMillis();
        
        ArrayList<Product> list = new ArrayList(getProducts());
        ArrayList<Product> finalized = new ArrayList();
        for (Product prod : list) {
            finalized.add(finalize(prod));
        }
        list = finalized;

        Comparator<Product> comparator = new Comparator<Product>() {
            public int compare(Product c1, Product c2) {
                if (c1 == null || c2 == null || c1.rowCreatedDate == null || c2.rowCreatedDate == null) {
                    return 0;
                }

                return c1.rowCreatedDate.compareTo(c2.rowCreatedDate);
            }
        };

        Collections.sort(list, comparator);
        return list;
    }

    @Override
    public List<Product> getAllProductsSortedByName() throws ErrorException {
        long time = System.currentTimeMillis();
        
        ArrayList<Product> list = new ArrayList(getProducts());
        ArrayList<Product> finalized = new ArrayList();
        for (Product prod : list) {
            finalized.add(finalize(prod));
        }
        list = finalized;

        Comparator<Product> comparator = new Comparator<Product>() {
            public int compare(Product c1, Product c2) {
                if (c1 == null || c2 == null) {
                    return 0;
                }
                if (c1.name == null) {
                    c1.name = "";
                }
                if (c2.name == null) {
                    c1.name = "";
                }

                return c1.name.compareTo(c2.name);
            }
        };

        Collections.sort(list, comparator);
        return list;
    }


    @Override
    public String getPageIdByName(String name) {
        for (Product product : getProducts()) {
            String productName = makeSeoUrl(product.uniqueName, "");
            if (productName.equals(name)) {
                return product.pageId;
            }
        }

        return "";
    }

    public boolean exists(String id) {
        return isProductExist(id);
    }

    @Override
    public void setTaxes(List<TaxGroup> groups) throws ErrorException {
        //Remove the old ones first.
        List<TaxGroup> oldGroups = new ArrayList(taxGroups.values());
        
        for (TaxGroup grp : taxGroups.values()) {
            deleteObject(grp);
        }

        taxGroups = new HashMap();
        for (TaxGroup grp : groups) {
            taxGroups.put(grp.groupNumber, grp);
            grp.storeId = storeId;
            if (accountingCodeChangedFromNull(grp, oldGroups)) {
                orderManager.updateOrdersWithNewAccountingTaxCode(grp);
            }
            setOriginalValues(grp, oldGroups);
            saveObject(grp);
        }
    }

    @Override
    public List<TaxGroup> getTaxes() throws ErrorException {
        return new ArrayList(taxGroups.values());
    }

    public TaxGroup getTaxGroup(int i) {
        return taxGroups.get(i);
    }
    
    public TaxGroup getTaxGroupById(String id) {
        return taxGroups.values()
                .stream()
                .filter(g -> g.id.equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Product getProductByPage(String id) throws ErrorException {
        Product product = findProductByPage(id);
        return finalize(product);
    }

    @Override
    public List<Product> getAllProductsLight() throws ErrorException {
        List<Product> retval = new ArrayList();

        for (Product prod : getProducts()) {
            Product result = new Product();
            result.id = prod.id;
            result.name = prod.name;
            result.price = prod.price;
            result.deactivated = prod.deactivated;
            retval.add(result);
        }


        return retval;
    }

    @Override
    public ProductList createProductList(String listName) {
        ProductList list = new ProductList();
        list.listName = listName;
        saveObject(list);
        productList.put(list.id, list);
        return list;
    }

    @Override
    public void deleteProductList(String listId) {
        ProductList list = productList.remove(listId);
        if (list != null) {
            deleteObject(list);
        }
    }

    @Override
    public List<ProductList> getProductLists() {
        return new ArrayList(productList.values());
    }

    @Override
    public ProductList getProductList(String listId) {
        return productList.get(listId);
    }

    @Override
    public void saveProductList(ProductList productList) {
        if (productList == null) {
            return;
        }
        
        if (productList.id == null || productList.id.equals("")) {
            saveObject(productList);
        }
        
        this.productList.put(productList.id, productList);
        saveObject(productList);
    }

    @Override
    public SearchResult search(String searchWord, Integer pageSize, Integer page) {
        return super.search(searchWord, pageSize, page); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProductDynamicPrice(String productId, int count) {
        Product product = getProduct(productId);
        if (product != null) {
            product.prices.clear();
            for (int i=0; i<count; i++) {
                product.prices.add(new ProductDynamicPrice(i));
            }
        } 
   }

    @Override
    public void saveCategory(ProductCategory categories) {
        saveObject(categories);
        this.categories.put(categories.id, categories);
    }

    @Override
    public void deleteCategory(String categoryId) {
        ProductCategory cat = getCategory(categoryId);
        this.categories.remove(categoryId);
        deleteObject(cat);
    }

    @Override
    public ProductCategory getCategory(String categoryId) {
        return this.categories.get(categoryId);
    }

    @Override
    public List<ProductCategory> getAllCategories() {
        List<ProductCategory> result = new ArrayList(this.categories.values());
        
        Collections.sort(result, new Comparator<ProductCategory>(){
            public int compare(ProductCategory o1, ProductCategory o2){
                return o1.name.compareTo(o2.name);
            }
       });
        
        return result;
    }

    @Override
    public Product copyProduct(String fromProductId, String newName) {
        Product product = getProductById(fromProductId);
        
        Page currentPage = pageManager.getPage(product.pageId);
        
        Product newProduct = product.clone();
        newProduct.id = UUID.randomUUID().toString();
        newProduct.pageId = pageManager.createPageFromTemplatePage(currentPage.masterPageId).id;
        newProduct.name = newName;
        saveProduct(newProduct);
        
        List<ProductList> addToLists = productList.values().stream()
                .filter(list -> list.productIds.contains(product.id))
                .collect(Collectors.toList());
                
        
        addToLists.forEach(o -> addProductToList(o.id, newProduct.id));
        return newProduct;
    }

    private void addProductToList(String productListId, String productId) {
        ProductList list = productList.get(productListId);
        list.productIds.add(productId);
        saveProductList(list);
        
    }

    public Product getProductUnfinalized(String productId) {
        return getProductById(productId);
    }

    public String getFirstProductList(String productId) {
        for (ProductList list : productList.values()) {
            if (list.productIds != null && list.productIds.contains(productId)) {
                return list.id;
            }
        }
        
        return "";
    }

    @Override
    public Product getDeletedProduct(String id) throws ErrorException {
        HashMap<String,String> searchCriteria = new HashMap();
        searchCriteria.put("_id", id);
        List<DataCommon> res = database.findWithDeleted("col_" + storeId, null, null, "ProductManager", searchCriteria, true);
        if(res.isEmpty()) {
            return null;
        }
        return (Product) res.get(0);
    }

    @Override
    public List<Product> getAllProductsIncDeleted() throws ErrorException {
        List<DataCommon> res = database.findWithDeleted("col_" + storeId, null, null, "ProductManager", null, true);
        
        List<Product> result = new ArrayList();
        for(DataCommon com : res) {
            if(com instanceof Product) {
                result.add((Product) com);
            }
        }
        return result;
    }

    @Override
    public FilteredData getAllProductsForRestaurant(FilterOptions filterOptions) {
        List<Product> products = search(filterOptions.searchWord, 10000000, 1).products;
        return pageIt(products, filterOptions);
    }
    
    @Override
    public FilteredData findProducts(FilterOptions filterOptions) {
        List<Product> products = search(filterOptions.searchWord, 10000000, 1).products;
        return pageIt(products, filterOptions);
    }

    @Override
    public List<ProductLight> getProductLight(List<String> ids) throws ErrorException {
        List<ProductLight> arrayList = new ArrayList();
        
        ids.stream()
            .forEach(id -> arrayList.add(new ProductLight(getProduct(id))));
        
        
        return arrayList;
    }

    @Override
    public AccountingDetail getAccountingDetail(int accountNumber) {
        return super.getAccountingDetail(accountNumber); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<AccountingDetail> getAccountingAccounts() {
        return super.getAccountingAccounts();
    }

    @Override
    public void saveAccountingDetail(AccountingDetail detail) {
        AccountingDetail exsisting = getAccountingDetail(detail.accountNumber);
        if(exsisting != null && exsisting.getShopTaxGroup != detail.getShopTaxGroup) {
            TaxGroup tax = getTaxGroup(detail.getShopTaxGroup);
            detail.taxgroup = tax.accountingTaxGroupId;
        }
        super.saveAccountingDetail(detail); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addAdditionalTaxGroup(String productId, String taxGroupId) {
        Product product = getProduct(productId);
        
        if (product == null) {
            product = getDeletedProduct(productId);
        }
        
        if (product != null) {
            if (product.taxGroupObject != null && product.taxGroupObject.id.equals(taxGroupId)) {
                return;
            }
            
            for (TaxGroup existingGroup : product.additionalTaxGroupObjects) {
                if (existingGroup.id.equals(taxGroupId)) {
                    return;
                }
            }
            
            TaxGroup taxGroup = taxGroups.values()
                    .stream()
                    .filter(tax -> tax.id.equals(taxGroupId))
                    .findFirst()
                    .orElse(null);
            
            if (taxGroup != null) {
                product.additionalTaxGroupObjects.add(taxGroup);
                saveProduct(product);
            }
        }
    }

    @Override
    public void removeTaxGroup(String productId, String taxGroupId) {
        Product product = getProduct(productId);
        
        if (product != null) {
            product.additionalTaxGroupObjects.removeIf(o -> o.id.equals(taxGroupId));
            saveObject(product);
        }
    }

    @Override
    public Product changeTaxCode(Product product, String taxGroupId) {
        product.changeToAdditionalTaxCode(taxGroupId);
        return product;
    }

    private boolean accountingCodeChangedFromNull(TaxGroup grp, List<TaxGroup> oldGroups) {
        if (grp.accountingTaxAccount == null || grp.accountingTaxAccount.isEmpty()) {
            return false;
        }
        
        TaxGroup oldGroup = oldGroups.stream()
                .filter(g -> g.groupNumber == grp.groupNumber)
                .findFirst()
                .orElse(null);
        
        boolean oldGroupHasNoAccountingTaxAccounnt = oldGroup.accountingTaxAccount == null || oldGroup.accountingTaxAccount.isEmpty();
        boolean newTaxGroupHasAccounting = grp.accountingTaxAccount != null && !grp.accountingTaxAccount.isEmpty();
        
        return oldGroupHasNoAccountingTaxAccounnt && newTaxGroupHasAccounting;
    }

    public void createGiftCardProduct() {
        if (getProduct("giftcard") != null) 
            return;
        
        Product product = new Product();
        product.id = "giftcard";
        product.name = "Gift Card";
        product.taxgroup = 0;
        product.setAccountingAccount("2901");
        saveObject(product);
        
        setProductById(product.id, product);
    }

    @Override
    public void saveAccountingInformation(String productId, List<ProductAccountingInformation> infos) {
        Product product = getAllProductsIncDeleted().stream()
                .filter(o -> o.id.equals(productId))
                .findAny()
                .orElse(null);
        
        if (product == null) {
            throw new ErrorException(28);
        }
        
        product.accountingConfig = infos;
        saveProduct(product);
    }

    public String getAgioAccountNumber(boolean loss) {
        if (loss) {
            return "8160";
        }
        
        return "8060";
    }

    @Override
    public void deleteAccountingAccount(int accountNumber) {
        AccountingDetail detail = accountingAccountDetails.get(accountNumber);
        
        if (detail != null) {
            boolean isInUse = getProducts()
                    .stream()
                    .filter(o -> {
                        return o.accountingConfig.stream()
                                .filter(i -> i.accountingNumber.equals(""+accountNumber))
                                .count() > 0;
                    })
                    .count() > 0;
            
            if (isInUse) {
                logPrint("Cant delete the account as it is in use for a one or more products");
                return;
            }
            
            accountingAccountDetails.remove(accountNumber);
            deleteObject(detail);
        }
    }

    @Override
    public void changeAccountingNumber(int oldAccountNumber, int accountNumber) {
        AccountingDetail res = getAccountingDetail(oldAccountNumber);
        res.accountNumber = accountNumber;
        saveObject(res);
        
        accountingAccountDetails.remove(oldAccountNumber);
        accountingAccountDetails.put(accountNumber, res);
    }

    @Override
    public AccountingDetail getAccountingDetailById(String accountingDetailId) {
        return getAccountingAccounts().stream()
                .filter(o -> o.id != null && o.id.equals(accountingDetailId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ProductList> getProductListsForPga() {
        return getProductLists();
    }

    @Override
    public void doubleCheckAndCorrectAccounts() {
        List<TaxGroup> taxes = getTaxes();
        super.doubleCheckAndCorrectAccounts(taxes);
    }
    
    @Override
    public void setAccomodationAccount(String accountId) {
        AccountingDetail accomodationAccount = null;
        List<AccountingDetail> accounts = getAccountingAccounts();
        for(AccountingDetail detail : accounts) {
            if(detail.isAccomodation) {
                detail.isAccomodation = false;
                saveAccountingDetail(detail);
            }
            
            if(detail.id.equals(accountId)) {
                if(detail.getShopTaxGroup > -1) {
                    detail.isAccomodation = true;
                    saveAccountingDetail(detail);
                    accomodationAccount = detail;
                }
            }
        }
        updateAllBookingTypesWithAccountingAccount();
    }
    
    @Override
    public void updateAllBookingTypesWithAccountingAccount() {
        AccountingDetail accomodationAccount = null;
        List<AccountingDetail> accounts = getAccountingAccounts();
        for(AccountingDetail detail : accounts) {
            if(detail.isAccomodation) {
                accomodationAccount = detail;
            }
        }
        if(accomodationAccount == null) {
            return;
        }
        
        List<BookingEngine> bengines = storeManager.getBookingEngines();
        for(BookingEngine engine : bengines) {
            List<BookingItemType> types = engine.getBookingItemTypes();
            for(BookingItemType type : types) {
                Product prod = getProduct(type.productId);
                for(ProductAccountingInformation check : prod.accountingConfig) {
                    if(check.accountingNumber == null || check.accountingNumber.isEmpty() || !check.accountingNumber.equals(accomodationAccount.accountNumber+"")) {
                        check.accountingNumber = accomodationAccount.accountNumber + "";
                        saveProduct(prod);
                    }
                }
                if(prod.accountingConfig.isEmpty()) {
                    ProductAccountingInformation check = new ProductAccountingInformation();
                    check.accountingNumber = accomodationAccount.accountNumber + "";
                    check.taxGroupNumber = accomodationAccount.getShopTaxGroup;
                    prod.accountingConfig.add(check);
                    saveProduct(prod);
                }
            }
        }
    }

    public TaxGroup getTaxGroup(String productId, int taxgroup, Date o) {
        TaxGroup taxGroup = getTaxGroup(taxgroup);
        
        if (o == null) {
            return taxGroup;
        }
        
        for (OverrideTaxGroup overTaxGroup : taxGroup.overrideTaxGroups) {
            long startL = overTaxGroup.start.getTime();
            long endL = overTaxGroup.end.getTime() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
            long toCheck = o.getTime();
            if (startL <= toCheck && endL > toCheck) {
                return getTaxGroup(overTaxGroup.groupNumber);
            }
        }
        
        return taxGroup;
    }

    @Override
    public void addOverrideTaxGroup(Integer groupNumber, Date start, Date end, Integer overrideGroupNumber) {
        TaxGroup taxGroup = getTaxGroup(groupNumber);
        
        if (taxGroup != null) {
            taxGroup.addOverrideGroup(start, end, overrideGroupNumber);
            saveObject(taxGroup);
        }
    }

    @Override
    public void removeOverrideTaxGroup(int taxGroupNumber, String id) {
        TaxGroup group = getTaxGroup(taxGroupNumber);
        if (group != null) {
            group.overrideTaxGroups.removeIf(o -> o.id.equals(id));
            saveObject(group);
        }
    }

    @Override
    public TaxGroup getTaxGroupAbstract(int taxGroupNumber) {
        return getTaxGroup(taxGroupNumber);
    }

    private void setOriginalValues(TaxGroup grp, List<TaxGroup> oldGroups) {
        for (TaxGroup taxGroup : oldGroups) {
            if (taxGroup.groupNumber == grp.groupNumber) {
                grp.overrideTaxGroups = taxGroup.overrideTaxGroups;
            }
        }
    }
}