package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIProductManager {

      public Communicator transport;

      public APIProductManager(Communicator transport){
           this.transport = transport;
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
     public void addAdditionalTaxGroup(Object productId, Object taxGroupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("taxGroupId",new Gson().toJson(taxGroupId));
          gs_json_object_data.method = "addAdditionalTaxGroup";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * You can use this function to change the stock quantity for a given product
     *
     * @param productId The id for the product to change.
     * @param count Number of entries to substract from the product stock quantity, an be negative number to decrease the stock quantity.
     * @return
     * @throws ErrorException
     */
     public void changeStockQuantity(Object productId, Object count)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("count",new Gson().toJson(count));
          gs_json_object_data.method = "changeStockQuantity";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
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
     public JsonElement changeTaxCode(Object product, Object taxGroupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("product",new Gson().toJson(product));
          gs_json_object_data.args.put("taxGroupId",new Gson().toJson(taxGroupId));
          gs_json_object_data.method = "changeTaxCode";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
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
     public JsonElement copyProduct(Object fromProductId, Object newName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("fromProductId",new Gson().toJson(fromProductId));
          gs_json_object_data.args.put("newName",new Gson().toJson(newName));
          gs_json_object_data.method = "copyProduct";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new product instance.
     * @return
     * @throws ErrorException
     */
     public JsonElement createProduct()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "createProduct";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new product list.
     *
     * @param listName
     */
     public JsonElement createProductList(Object listName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("listName",new Gson().toJson(listName));
          gs_json_object_data.method = "createProductList";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new product instance.
     * @return
     * @throws ErrorException
     */
     public JsonElement createProductWithAccount(Object accountNumber)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("accountNumber",new Gson().toJson(accountNumber));
          gs_json_object_data.method = "createProductWithAccount";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
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
     public void deleteCategory(Object categoryId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("categoryId",new Gson().toJson(categoryId));
          gs_json_object_data.method = "deleteCategory";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create a new product list.
     *
     * @param listName
     */
     public void deleteProductList(Object listId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.method = "deleteProductList";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
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
     public JsonElement findProducts(Object filterOptions)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filterOptions",new Gson().toJson(filterOptions));
          gs_json_object_data.method = "findProducts";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
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
     public JsonElement getAccountingDetail(Object accountNumber)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("accountNumber",new Gson().toJson(accountNumber));
          gs_json_object_data.method = "getAccountingDetail";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
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
     public JsonElement getAccountingDetailById(Object accountingDetailId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("accountingDetailId",new Gson().toJson(accountingDetailId));
          gs_json_object_data.method = "getAccountingDetailById";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
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
     public JsonElement getAllCategories()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllCategories";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all products the store has available.
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllProducts()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllProducts";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
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
     public JsonElement getAllProductsForRestaurant(Object filterOptions)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filterOptions",new Gson().toJson(filterOptions));
          gs_json_object_data.method = "getAllProductsForRestaurant";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all products the store has available.
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllProductsIncDeleted()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllProductsIncDeleted";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all products with only name, price and id.
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllProductsLight()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllProductsLight";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all products the store has available.
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllProductsSortedByName()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllProductsSortedByName";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
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
     public JsonElement getCategory(Object categoryId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("categoryId",new Gson().toJson(categoryId));
          gs_json_object_data.method = "getCategory";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getDeletedProduct(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getDeletedProduct";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a list of all the latest products.
     * @param count Number of products to fetch.
     * @return
     * @throws ErrorException
     */
     public JsonElement getLatestProducts(Object count)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("count",new Gson().toJson(count));
          gs_json_object_data.method = "getLatestProducts";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get price for a product with variations
     */
     public JsonElement getPrice(Object productId, Object variations)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("variations",new Gson().toJson(variations));
          gs_json_object_data.method = "getPrice";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getProduct(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getProduct";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
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
     public JsonElement getProductBySku(Object sku)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("sku",new Gson().toJson(sku));
          gs_json_object_data.method = "getProductBySku";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getProductLight(Object ids)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ids",new Gson().toJson(ids));
          gs_json_object_data.method = "getProductLight";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new product list.
     *
     * @param listName
     */
     public JsonElement getProductList(Object listId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.method = "getProductList";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new product list.
     *
     * @param listName
     */
     public JsonElement getProductLists()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getProductLists";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
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
     public JsonElement getProductListsForPga()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getProductListsForPga";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch products
     *
     * @param productCriteria
     * @return
     * @throws ErrorException
     */
     public JsonElement getProducts(Object productCriteria)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productCriteria",new Gson().toJson(productCriteria));
          gs_json_object_data.method = "getProducts";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get a list of all the taxes set for this store.
     * @return
     * @throws ErrorException
     */
     public JsonElement getTaxes()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getTaxes";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Remove an existing product.
     *
     * @param productId The id of the product to remove.
     * @return
     * @throws ErrorException
     */
     public void removeProduct(Object productId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.method = "removeProduct";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
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
     public void removeTaxGroup(Object productId, Object taxGroupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("taxGroupId",new Gson().toJson(taxGroupId));
          gs_json_object_data.method = "removeTaxGroup";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
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
     public void saveAccountingDetail(Object detail)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("detail",new Gson().toJson(detail));
          gs_json_object_data.method = "saveAccountingDetail";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
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
     public void saveCategory(Object categories)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("categories",new Gson().toJson(categories));
          gs_json_object_data.method = "saveCategory";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Save a product.
     *
     * @param product The product to save, if the id for the product is not set.
     * @param parentPageId Attach this product to a given page, leave this empty to avoid attaching it.
     * @return
     * @throws ErrorException
     */
     public JsonElement saveProduct(Object product)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("product",new Gson().toJson(product));
          gs_json_object_data.method = "saveProduct";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new product list.
     *
     * @param listName
     */
     public void saveProductList(Object productList)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productList",new Gson().toJson(productList));
          gs_json_object_data.method = "saveProductList";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
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
     public void saveTaxGroup(Object taxGroup)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("taxGroup",new Gson().toJson(taxGroup));
          gs_json_object_data.method = "saveTaxGroup";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
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
     public JsonElement search(Object searchWord, Object pageSize, Object page)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("searchWord",new Gson().toJson(searchWord));
          gs_json_object_data.args.put("pageSize",new Gson().toJson(pageSize));
          gs_json_object_data.args.put("page",new Gson().toJson(page));
          gs_json_object_data.method = "search";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Set the tax groups for the the products, (0-5).
     * @param group
     * @throws ErrorException
     */
     public void setTaxes(Object group)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("group",new Gson().toJson(group));
          gs_json_object_data.method = "setTaxes";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
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
     public void updateTaxGroup(Object taxgroup, Object desc, Object rate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("taxgroup",new Gson().toJson(taxgroup));
          gs_json_object_data.args.put("desc",new Gson().toJson(desc));
          gs_json_object_data.args.put("rate",new Gson().toJson(rate));
          gs_json_object_data.method = "updateTaxGroup";
          gs_json_object_data.interfaceName = "core.productmanager.IProductManager";
          String result = transport.send(gs_json_object_data);
     }

}
