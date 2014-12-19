package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIProductManager {

      public Transporter transport;

      public APIProductManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * You can use this function to change the stock quantity for a given product
     *
     * @param productId The id for the product to change.
     * @param count Number of entries to substract from the product stock quantity, an be negative number to decrease the stock quantity.
     * @return
     * @throws ErrorException
     */
     public void changeStockQuantity(java.lang.String productId, int count)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("count",new Gson().toJson(count));
          data.method = "changeStockQuantity";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * Create a new product instance.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.productmanager.data.Product createProduct()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "createProduct";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.productmanager.data.Product>() {}.getType();
          com.thundashop.core.productmanager.data.Product object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Create a new product list.
     *
     * @param listName
     */
     public com.thundashop.core.productmanager.data.ProductList createProductList(java.lang.String listName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("listName",new Gson().toJson(listName));
          data.method = "createProductList";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.productmanager.data.ProductList>() {}.getType();
          com.thundashop.core.productmanager.data.ProductList object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Create a new product list.
     *
     * @param listName
     */
     public void deleteProductList(java.lang.String listId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("listId",new Gson().toJson(listId));
          data.method = "deleteProductList";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * Fetch all attributes connected to all products.
     * @return
     * @throws ErrorException
     */
     public java.util.List getAllAttributes()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getAllAttributes";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.productmanager.data.AttributeValue>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all products the store has available.
     * @return
     * @throws ErrorException
     */
     public java.util.List getAllProducts()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getAllProducts";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.productmanager.data.Product>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns a list of all products with only name, price and id.
     * @return
     * @throws ErrorException
     */
     public java.util.List getAllProductsLight()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getAllProductsLight";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.productmanager.data.Product>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Whenever you use the getproducts function call, you will be able to fetch a summary of the attributes.
     * Comes in handy to filter result when fetching data from for example a product list.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.productmanager.data.AttributeSummary getAttributeSummary()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getAttributeSummary";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.productmanager.data.AttributeSummary>() {}.getType();
          com.thundashop.core.productmanager.data.AttributeSummary object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch a list of all the latest products.
     * @param count Number of products to fetch.
     * @return
     * @throws ErrorException
     */
     public java.util.List getLatestProducts(int count)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("count",new Gson().toJson(count));
          data.method = "getLatestProducts";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.productmanager.data.Product>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get page by name
     */
     public java.lang.String getPageIdByName(java.lang.String productName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productName",new Gson().toJson(productName));
          data.method = "getPageIdByName";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get price for a product with variations
     */
     public java.lang.Double getPrice(java.lang.String productId, java.util.List variations)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("variations",new Gson().toJson(variations));
          data.method = "getPrice";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.Double>() {}.getType();
          java.lang.Double object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch one single product by id
     * If product does not exists, null is returned.
     *
     * @param id
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.productmanager.data.Product getProduct(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getProduct";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.productmanager.data.Product>() {}.getType();
          com.thundashop.core.productmanager.data.Product object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns a product connected to a specific page.
     */
     public com.thundashop.core.productmanager.data.Product getProductByPage(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getProductByPage";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.productmanager.data.Product>() {}.getType();
          com.thundashop.core.productmanager.data.Product object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Find the product uuid set for an application.
     * @param uuid
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.productmanager.data.Product getProductFromApplicationId(java.lang.String app_uuid)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("app_uuid",new Gson().toJson(app_uuid));
          data.method = "getProductFromApplicationId";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.productmanager.data.Product>() {}.getType();
          com.thundashop.core.productmanager.data.Product object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Create a new product list.
     *
     * @param listName
     */
     public com.thundashop.core.productmanager.data.ProductList getProductList(java.lang.String listId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("listId",new Gson().toJson(listId));
          data.method = "getProductList";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.productmanager.data.ProductList>() {}.getType();
          com.thundashop.core.productmanager.data.ProductList object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Create a new product list.
     *
     * @param listName
     */
     public java.util.List getProductLists()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getProductLists";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.productmanager.data.ProductList>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch products
     *
     * @param productCriteria
     * @return
     * @throws ErrorException
     */
     public java.util.List getProducts(com.thundashop.core.productmanager.data.ProductCriteria productCriteria)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productCriteria",new Gson().toJson(productCriteria));
          data.method = "getProducts";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.productmanager.data.Product>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns a random set of products
     * If the store does not have enough products it will return as close as possible to
     * the fetchsize specified.
     *
     * @param fetchSize Amount of products that you wish to fetch.
     * @param ignoreProductId Will skip this id, can be the productId or the productPageId.
     * @return
     */
     public java.util.ArrayList getRandomProducts(java.lang.Integer fetchSize, java.lang.String ignoreProductId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("fetchSize",new Gson().toJson(fetchSize));
          data.args.put("ignoreProductId",new Gson().toJson(ignoreProductId));
          data.method = "getRandomProducts";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.ArrayList<com.thundashop.core.productmanager.data.Product>>() {}.getType();
          java.util.ArrayList object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get a list of all the taxes set for this store.
     * @return
     * @throws ErrorException
     */
     public java.util.List getTaxes()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getTaxes";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.productmanager.data.TaxGroup>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Remove an existing product.
     *
     * @param productId The id of the product to remove.
     * @return
     * @throws ErrorException
     */
     public void removeProduct(java.lang.String productId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.method = "removeProduct";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * Save a product.
     *
     * @param product The product to save, if the id for the product is not set.
     * @param parentPageId Attach this product to a given page, leave this empty to avoid attaching it.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.productmanager.data.Product saveProduct(com.thundashop.core.productmanager.data.Product product)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("product",new Gson().toJson(product));
          data.method = "saveProduct";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.productmanager.data.Product>() {}.getType();
          com.thundashop.core.productmanager.data.Product object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Create a new product list.
     *
     * @param listName
     */
     public void saveProductList(com.thundashop.core.productmanager.data.ProductList productList)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productList",new Gson().toJson(productList));
          data.method = "saveProductList";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * Returns a list of products for a given searchword,
     * if blank all products will be returned.
     *
     * @param searchWord
     * @param pageSize
     * @param page
     * @return
     */
     public java.util.List search(java.lang.String searchWord, java.lang.Integer pageSize, java.lang.Integer page)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("searchWord",new Gson().toJson(searchWord));
          data.args.put("pageSize",new Gson().toJson(pageSize));
          data.args.put("page",new Gson().toJson(page));
          data.method = "search";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.productmanager.data.Product>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Method for setting a known product image as main image.
     *
     * @param productId
     * @param imageId
     * @throws ErrorException
     */
     public void setMainImage(java.lang.String productId, java.lang.String imageId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("imageId",new Gson().toJson(imageId));
          data.method = "setMainImage";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * Set the tax groups for the the products, (0-5).
     * @param group
     * @throws ErrorException
     */
     public void setTaxes(java.util.List group)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("group",new Gson().toJson(group));
          data.method = "setTaxes";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

     /**
     * Translate all antries found in a given list of entry ids.
     * @param entryIds A list of entries id to translate.
     * @return A list with human readable strings which translate the error mapped to the id.
     * @throws ErrorException
     */
     public java.util.HashMap translateEntries(java.util.List entryIds)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("entryIds",new Gson().toJson(entryIds));
          data.method = "translateEntries";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.HashMap<java.lang.String,java.lang.String>>() {}.getType();
          java.util.HashMap object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Update the attribute pool. This will replace the old one, so all entries has to be included here.
     * @param groups
     * @throws ErrorException
     */
     public void updateAttributePool(java.util.List groups)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("groups",new Gson().toJson(groups));
          data.method = "updateAttributePool";
          data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(data);
     }

}
