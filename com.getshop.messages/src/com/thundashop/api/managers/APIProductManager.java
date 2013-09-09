package com.thundashop.api.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import com.thundashop.core.common.JsonObject2;
import java.util.ArrayList;
import com.thundashop.core.productmanager.data.ProductCriteria;
import com.thundashop.core.productmanager.data.Product;
import java.util.List;
import com.thundashop.core.productmanager.data.AttributeGroup;
import com.thundashop.core.productmanager.data.AttributeSummary;
import java.util.HashMap;

public class APIProductManager {

      public Transporter transport;

      public APIProductManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Add an attribute to a product.
     * @param productId The id of the product to attach it to.
     * @param attributeGroup The name of the attribute group, if it does not exists, it is being added to the attribute pool.
     * @param attribute The name of the attribute, leave this empty to create a new attribute group.
     * @throws ErrorException 
     */

     public void addAttributeGroupToProduct(String productId, String attributeGroup, String attribute)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("attributeGroup",new Gson().toJson(attributeGroup));
          data.args.put("attribute",new Gson().toJson(attribute));
          data.method = "addAttributeGroupToProduct";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * add image to specified product
     * 
     * @param productId
     * @param productImageId
     * @param description
     * @throws ErrorException 
     */

     public void addImage(String productId, String productImageId, String description)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("productImageId",new Gson().toJson(productImageId));
          data.args.put("description",new Gson().toJson(description));
          data.method = "addImage";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * You can use this function to change the stock quantity for a given product
     * 
     * @param productId The id for the product to change.
     * @param count Number of entries to substract from the product stock quantity, an be negative number to decrease the stock quantity.
     * @return void
     * @throws ErrorException 
     */

     public void changeStockQuantity(String productId, int count)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("count",new Gson().toJson(count));
          data.method = "changeStockQuantity";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * Create a new product instance.
     * @return Product
     * @throws ErrorException 
     */

     public Product createProduct()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "createProduct";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Product>() {}.getType();
          Product object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * I have an attribute attached to a group, but it does not seems to be in use, and i want to delete it.
     * @param groupName The name of the group where the attribute is located.
     * @param attribute The name of the attribute to remove.
     * @throws ErrorException 
     */

     public void deleteAttribute(String groupName, String attribute)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("groupName",new Gson().toJson(groupName));
          data.args.put("attribute",new Gson().toJson(attribute));
          data.method = "deleteAttribute";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * This attribute is not even in use, I want to delete it.
     * @param groupName The name of the group to delete.
     * @throws ErrorException 
     */

     public void deleteGroup(String groupName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("groupName",new Gson().toJson(groupName));
          data.method = "deleteGroup";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * Fetch all attributes connected to all products.
     * @return HashMap<String,AttributeGroup>
     * @throws ErrorException 
     */

     public HashMap<String,AttributeGroup> getAllAttributes()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getAllAttributes";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<HashMap<String,AttributeGroup>>() {}.getType();
          HashMap<String,AttributeGroup> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all products the store has available.
     * @return List<Product>
     * @throws ErrorException 
     */

     public List<Product> getAllProducts()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getAllProducts";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<Product>>() {}.getType();
          List<Product> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Whenever you use the getproducts function call, you will be able to fetch a summary of the attributes.
     * Comes in handy to filter result when fetching data from for example a product list.
     * @return AttributeSummary
     * @throws ErrorException 
     */

     public AttributeSummary getAttributeSummary()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getAttributeSummary";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<AttributeSummary>() {}.getType();
          AttributeSummary object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch a list of all the latest products.
     * @param count Number of products to fetch.
     * @return List<Product>
     * @throws ErrorException 
     */

     public List<Product> getLatestProducts(int count)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("count",new Gson().toJson(count));
          data.method = "getLatestProducts";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<Product>>() {}.getType();
          List<Product> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get price for a product with variations
     */

     public Double getPrice(String productId, List<String> variations)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("variations",new Gson().toJson(variations));
          data.method = "getPrice";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Double>() {}.getType();
          Double object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch one single product by id
     * 
     * @param id
     * @return Product
     * @throws ErrorException 
     */

     public Product getProduct(String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getProduct";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Product>() {}.getType();
          Product object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Find the product uuid set for an application.
     * @param uuid
     * @return Product
     * @throws ErrorException 
     */

     public Product getProductFromApplicationId(String app_uuid)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("app_uuid",new Gson().toJson(app_uuid));
          data.method = "getProductFromApplicationId";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Product>() {}.getType();
          Product object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch products 
     * 
     * @param productCriteria
     * @return List<Product>
     * @throws ErrorException 
     */

     public List<Product> getProducts(ProductCriteria productCriteria)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("productCriteria",new Gson().toJson(productCriteria));
          data.method = "getProducts";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<Product>>() {}.getType();
          List<Product> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns a random set of products
     * If the store does not have enough products it will return as close as possible to 
     * the fetchsize specified.
     * 
     * @param fetchSize Amount of products that you wish to fetch.
     * @param ignoreProductId Will skip this id, can be the productId or the productPageId.
     * @return ArrayList<Product>
     */

     public ArrayList<Product> getRandomProducts(Integer fetchSize, String ignoreProductId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("fetchSize",new Gson().toJson(fetchSize));
          data.args.put("ignoreProductId",new Gson().toJson(ignoreProductId));
          data.method = "getRandomProducts";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<ArrayList<Product>>() {}.getType();
          ArrayList<Product> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Oh, so you added an attribute to a product which where not ment to be?
     * @param productId The id of the product.
     * @param attributeGroupId The id for the product group attached.
     */

     public void removeAttributeGroupFromProduct(String productId, String attributeGroupId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("attributeGroupId",new Gson().toJson(attributeGroupId));
          data.method = "removeAttributeGroupFromProduct";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * Remove an existing product.
     * 
     * @param productId The id of the product to remove.
     * @return void
     * @throws ErrorException 
     */

     public void removeProduct(String productId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.method = "removeProduct";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * Oh, I incorrectly wrote my attribute, i need to rename it for a specific group.
     * @param groupName The name of the group where the attribute is located.
     * @param oldAttributeName The old attribute text
     * @param newAttributeName The new attribute text.
     * @throws ErrorException 
     */

     public void renameAttribute(String groupName, String oldAttributeName, String newAttributeName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("groupName",new Gson().toJson(groupName));
          data.args.put("oldAttributeName",new Gson().toJson(oldAttributeName));
          data.args.put("newAttributeName",new Gson().toJson(newAttributeName));
          data.method = "renameAttribute";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * Typically, i just enter in incorrect group name, and i just figured it out... :(<br>
     * Well, this one helps you.
     * @param oldName The old group name.
     * @param newName The new group name.
     * @throws ErrorException 
     */

     public void renameAttributeGroupName(String oldName, String newName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("oldName",new Gson().toJson(oldName));
          data.args.put("newName",new Gson().toJson(newName));
          data.method = "renameAttributeGroupName";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * Save a product.
     * 
     * @param product The product to save, if the id for the product is not set.
     * @param parentPageId Attach this product to a given page, leave this empty to avoid attaching it.
     * @return Product
     * @throws ErrorException 
     */

     public Product saveProduct(Product product)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("product",new Gson().toJson(product));
          data.method = "saveProduct";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Product>() {}.getType();
          Product object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Method for setting a known product image as main image.
     * 
     * @param productId
     * @param imageId
     * @throws ErrorException 
     */

     public void setMainImage(String productId, String imageId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("imageId",new Gson().toJson(imageId));
          data.method = "setMainImage";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * Translate all antries found in a given list of entry ids.
     * @param entryIds A list of entries id to translate.
     * @return HashMap<String,String>
     * @throws ErrorException 
     */

     public HashMap<String,String> translateEntries(List<String> entryIds)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("entryIds",new Gson().toJson(entryIds));
          data.method = "translateEntries";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<HashMap<String,String>>() {}.getType();
          HashMap<String,String> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

}
