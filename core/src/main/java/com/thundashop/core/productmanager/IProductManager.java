package com.thundashop.core.productmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FilterOptions;
import com.thundashop.core.common.FilteredData;
import com.thundashop.core.common.ForceAsync;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.productmanager.data.AccountingDetail;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductAccountingInformation;
import com.thundashop.core.productmanager.data.ProductCategory;
import com.thundashop.core.productmanager.data.ProductCriteria;
import com.thundashop.core.productmanager.data.ProductLight;
import com.thundashop.core.productmanager.data.ProductList;
import com.thundashop.core.productmanager.data.SearchResult;
import com.thundashop.core.productmanager.data.TaxGroup;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Managing products in a webshop is important.<br>
 * This manager is handling all you product related stuff.<br>
 */
@GetShopApi
public interface IProductManager {
    /**
     * You can use this function to change the stock quantity for a given product
     * 
     * @param productId The id for the product to change.
     * @param count Number of entries to substract from the product stock quantity, an be negative number to decrease the stock quantity.
     * @return
     * @throws ErrorException 
     */
    @Editor
    public void changeStockQuantity(String productId, int count) throws ErrorException;
    
    /**
     * Find the product uuid set for an application.
     * @param uuid
     * @return
     * @throws ErrorException 
     */
    public Product getProductFromApplicationId(String app_uuid) throws ErrorException;
    
    @Administrator
    public void doubleCheckAndCorrectAccounts();

    @Administrator
    public void setAccomodationAccount(String accountId);
    
    @Administrator
    public void updateAllBookingTypesWithAccountingAccount();
    
    /**
     * Save a product.
     * 
     * @param product The product to save, if the id for the product is not set.
     * @param parentPageId Attach this product to a given page, leave this empty to avoid attaching it.
     * @return
     * @throws ErrorException 
     */
    @Editor
    public Product saveProduct(Product product) throws ErrorException;

    /**
     * Fetch products 
     * 
     * @param productCriteria
     * @return
     * @throws ErrorException 
     */
    public List<Product> getProducts(ProductCriteria productCriteria) throws ErrorException;
    
    /**
     * Fetch a list of all the latest products.
     * @param count Number of products to fetch.
     * @return
     * @throws ErrorException 
     */
    public List<Product> getLatestProducts(int count) throws ErrorException;
    
    /**
     * Remove an existing product.
     * 
     * @param productId The id of the product to remove.
     * @return
     * @throws ErrorException 
     */
    @Editor
    public void removeProduct(String productId) throws ErrorException;
    
    /**
     * Create a new product instance.
     * @return
     * @throws ErrorException 
     */
    @Editor
    public Product createProduct() throws ErrorException;
    
    @Editor
    public Product createProductWithAccount(Integer accountNumber) throws ErrorException;
        
    /**
     * Returns a random set of products
     * If the store does not have enough products it will return as close as possible to 
     * the fetchsize specified.
     * 
     * @param fetchSize Amount of products that you wish to fetch.
     * @param ignoreProductId Will skip this id, can be the productId or the productPageId.
     * @return 
     */
    public ArrayList<Product> getRandomProducts(Integer fetchSize, String ignoreProductId) throws ErrorException ;
    
    
    /**
     * Translate all antries found in a given list of entry ids.
     * @param entryIds A list of entries id to translate.
     * @return A list with human readable strings which translate the error mapped to the id.
     * @throws ErrorException 
     */
    public HashMap<String, String> translateEntries(List<String> entryIds) throws ErrorException;

    /**
     * Fetch one single product by id
     * If product does not exists, null is returned.
     * 
     * @param id
     * @return
     * @throws ErrorException 
     */
    public Product getProduct(String id) throws ErrorException;
    
    public List<ProductLight> getProductLight(List<String> ids) throws ErrorException;
    
    @Administrator
    public Product getDeletedProduct(String id) throws ErrorException;
    
    /**
     * Method for setting a known product image as main image.
     * 
     * @param productId
     * @param imageId
     * @throws ErrorException 
     */
    public void setMainImage(String productId, String imageId) throws ErrorException;
    
    /**
     * Fetch all products the store has available.
     * @return
     * @throws ErrorException 
     */
    public List<Product> getAllProducts() throws ErrorException;
    
    /**
     * Fetch all products the store has available.
     * @return
     * @throws ErrorException 
     */ 
    public List<Product> getAllProductsSortedByName() throws ErrorException;
    
    @Administrator
    public List<Product> getAllProductsIncDeleted() throws ErrorException;
    
    /**
     * Get price for a product with variations
     */
    public Double getPrice(String productId, Map<String,String> variations) throws ErrorException;
    
    /**
     * Get page by name
     */
    public String getPageIdByName(String productName);
    
    /**
     * Set the tax groups for the the products, (0-5).
     * @param group
     * @throws ErrorException 
     */
    public void setTaxes(List<TaxGroup> group) throws ErrorException;
    
    /**
     * Get a list of all the taxes set for this store.
     * @return
     * @throws ErrorException 
     */
    public List<TaxGroup> getTaxes() throws ErrorException;
    
    /**
     * Returns a product connected to a specific page.
     */
    public Product getProductByPage(String id) throws ErrorException;
    
    /**
     * Returns a list of all products with only name, price and id.
     * @return
     * @throws ErrorException 
     */
    public List<Product> getAllProductsLight() throws ErrorException;
    
    /**
     * Create a new product list.
     * 
     * @param listName 
     */
    @Administrator
    public ProductList createProductList(String listName);
    
    /**
     * Create a new product list.
     * 
     * @param listName
     */
    @Administrator
    public void deleteProductList(String listId);
    
    /**
     * Create a new product list.
     * 
     * @param listName
     */
    @Administrator
    public List<ProductList> getProductLists();
    
    /**
     * Create a new product list.
     * 
     * @param listName
     */
    public ProductList getProductList(String listId);
    
    /**
     * Create a new product list.
     * 
     * @param listName
     */
    @Administrator
    public void saveProductList(ProductList productList);
    
    /**
     * Returns a list of products for a given searchword, 
     * if blank all products will be returned.
     * 
     * @param searchWord
     * @param pageSize
     * @param page
     * @return 
     */
    public SearchResult search(String searchWord, Integer pageSize, Integer page);
    
    @Administrator
    public void setProductDynamicPrice(String productId, int count);
    
    @Administrator
    public void addOverrideTaxGroup(Integer groupNumber, Date start, Date end, Integer overrideGroupNumber);
    
    @Administrator
    public void saveCategory(ProductCategory categories);
    @Administrator
    public void deleteCategory(String categoryId);
    public List<ProductCategory> getAllCategories();
    public ProductCategory getCategory(String categoryId);
    
    @Editor
    public Product copyProduct(String fromProductId, String newName);
    
    @Editor
    public FilteredData getAllProductsForRestaurant(FilterOptions filterOptions);
    
    @Editor
    public FilteredData findProducts(FilterOptions filterOptions);
    
    @Editor
    public AccountingDetail getAccountingDetail(int accountNumber);
    
    @Editor
    public AccountingDetail getAccountingDetailById(String accountingDetailId);
    
    @Editor
    public void saveAccountingDetail(AccountingDetail detail);
    
    @Editor
    public void addAdditionalTaxGroup(String productId, String taxGroupId);
    
    @Editor
    public void removeTaxGroup(String productId, String taxGroupId);
    
    @Editor
    public Product changeTaxCode(Product product, String taxGroupId);
    
    @Editor
    public void saveAccountingInformation(String productId, List<ProductAccountingInformation> infos);
    
    @Editor
    public List<AccountingDetail> getAccountingAccounts();
    
    @Administrator
    public void deleteAccountingAccount(int accountNumber);
    
    @Administrator
    public void changeAccountingNumber(int oldAccountNumber, int accountNumber);
   
    @Administrator
    public void removeOverrideTaxGroup(int taxGroupNumber, String id);
   
    public List<ProductList> getProductListsForPga();
    
}