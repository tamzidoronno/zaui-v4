package com.thundashop.core.productmanager;

import com.thundashop.core.productmanager.data.AttributeSummary;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductCriteria;
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
     * add image to specified product
     * 
     * @param productId
     * @param productImageId
     * @param description
     * @throws ErrorException 
     */
    public void addImage(String productId, String productImageId, String description) throws ErrorException;
    
    /**
     * Add an attribute to a product.
     * @param productId The id of the product to attach it to.
     * @param attributeGroup The name of the attribute group, if it does not exists, it is being added to the attribute pool.
     * @param attribute The name of the attribute, leave this empty to create a new attribute group.
     * @throws ErrorException 
     */
    public void addAttributeGroupToProduct(String productId, String attributeGroup, String attribute) throws ErrorException;
    
    /**
     * Oh, so you added an attribute to a product which where not ment to be?
     * @param productId The id of the product.
     * @param attributeGroupId The id for the product group attached.
     */
    public void removeAttributeGroupFromProduct(String productId, String attributeGroupId) throws ErrorException;

    /**
     * Typically, i just enter in incorrect group name, and i just figured it out... :(<br>
     * Well, this one helps you.
     * @param oldName The old group name.
     * @param newName The new group name.
     * @throws ErrorException 
     */
    public void renameAttributeGroupName(String oldName, String newName) throws ErrorException;
    
    
    /**
     * Oh, I incorrectly wrote my attribute, i need to rename it for a specific group.
     * @param groupName The name of the group where the attribute is located.
     * @param oldAttributeName The old attribute text
     * @param newAttributeName The new attribute text.
     * @throws ErrorException 
     */
    public void renameAttribute(String groupName, String oldAttributeName, String newAttributeName) throws ErrorException;
    
    /**
     * This attribute is not even in use, I want to delete it.
     * @param groupName The name of the group to delete.
     * @throws ErrorException 
     */
    public void deleteGroup(String groupName) throws ErrorException;
    
    /**
     * I have an attribute attached to a group, but it does not seems to be in use, and i want to delete it.
     * @param groupName The name of the group where the attribute is located.
     * @param attribute The name of the attribute to remove.
     * @throws ErrorException 
     */
    public void deleteAttribute(String groupName, String attribute) throws ErrorException;
    
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
}