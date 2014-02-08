package com.thundashop.core.productmanager;

import com.thundashop.core.productmanager.data.AttributeSummary;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.productmanager.data.AttributeValue;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductCriteria;
import com.thundashop.core.productmanager.data.TaxGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
     * Fetch all attributes connected to all products.
     * @return
     * @throws ErrorException 
     */
    public List<AttributeValue> getAllAttributes() throws ErrorException;
    
    /**
     * Find the product uuid set for an application.
     * @param uuid
     * @return
     * @throws ErrorException 
     */
    public Product getProductFromApplicationId(String app_uuid) throws ErrorException;
    
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
     * Update the attribute pool. This will replace the old one, so all entries has to be included here.
     * @param groups
     * @throws ErrorException 
     */
    public void updateAttributePool(List<AttributeValue> groups) throws ErrorException;
    
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
     * Whenever you use the getproducts function call, you will be able to fetch a summary of the attributes.
     * Comes in handy to filter result when fetching data from for example a product list.
     * @return
     * @throws ErrorException 
     */
    public AttributeSummary getAttributeSummary() throws ErrorException;
    
    /**
     * Get price for a product with variations
     */
    public Double getPrice(String productId, List<String> variations) throws ErrorException;
    
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
}