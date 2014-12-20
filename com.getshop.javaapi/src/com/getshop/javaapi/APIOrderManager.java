package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIOrderManager {

      public Transporter transport;

      public APIOrderManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Change order status of a specified order.
     * The id could be the orderId or the transaction id.
     */
     public void changeOrderStatus(java.lang.String id, int status)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.args.put("status",new Gson().toJson(status));
          data.method = "changeOrderStatus";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
     }

     /**
     * Create an order out of a given cart.
     * @param cart The cart object generated by cartmanager.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.ordermanager.data.Order createOrder(com.thundashop.core.usermanager.data.Address address)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("address",new Gson().toJson(address));
          data.method = "createOrder";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.ordermanager.data.Order>() {}.getType();
          com.thundashop.core.ordermanager.data.Order object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * If a customer is providing a customer reference id, it should be possible to create order by it.
     * @param cart The cart object generated by cartmanager.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.ordermanager.data.Order createOrderByCustomerReference(java.lang.String referenceKey)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("referenceKey",new Gson().toJson(referenceKey));
          data.method = "createOrderByCustomerReference";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.ordermanager.data.Order>() {}.getType();
          com.thundashop.core.ordermanager.data.Order object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all orders for a user.
     * @param userId
     * @return
     * @throws ErrorException
     */
     public java.util.List getAllOrdersForUser(java.lang.String userId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.method = "getAllOrdersForUser";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.ordermanager.data.Order>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public java.util.Map getMostSoldProducts(int numberOfProducts)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("numberOfProducts",new Gson().toJson(numberOfProducts));
          data.method = "getMostSoldProducts";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.Map<java.lang.String,java.util.List<com.thundashop.core.ordermanager.data.Statistic>>>() {}.getType();
          java.util.Map object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch a single order based on its id.
     * @param orderId
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.ordermanager.data.Order getOrder(java.lang.String orderId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("orderId",new Gson().toJson(orderId));
          data.method = "getOrder";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.ordermanager.data.Order>() {}.getType();
          com.thundashop.core.ordermanager.data.Order object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Got a reference number for the order, fetch it from here.
     * @param referenceId
     * @throws ErrorException
     */
     public com.thundashop.core.ordermanager.data.Order getOrderByReference(java.lang.String referenceId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("referenceId",new Gson().toJson(referenceId));
          data.method = "getOrderByReference";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.ordermanager.data.Order>() {}.getType();
          com.thundashop.core.ordermanager.data.Order object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * @param id
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.ordermanager.data.Order getOrderByincrementOrderId(java.lang.Integer id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getOrderByincrementOrderId";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.ordermanager.data.Order>() {}.getType();
          com.thundashop.core.ordermanager.data.Order object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get a list of already created orders.
     * @param orderIds A list of all orders you want to fetch, all orders are retrieved if this list is empty.
     * @param page What page are you fetching (default 0)
     * @param pageSize Number of entries for each page (default 10)
     * @return
     * @throws ErrorException
     */
     public java.util.List getOrders(java.util.ArrayList orderIds, java.lang.Integer page, java.lang.Integer pageSize)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("orderIds",new Gson().toJson(orderIds));
          data.args.put("page",new Gson().toJson(page));
          data.args.put("pageSize",new Gson().toJson(pageSize));
          data.method = "getOrders";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.ordermanager.data.Order>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns how many pages there is for this store with the given pagesize
     * @return
     */
     public int getPageCount(int pageSize, java.lang.String searchWord)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageSize",new Gson().toJson(pageSize));
          data.args.put("searchWord",new Gson().toJson(searchWord));
          data.method = "getPageCount";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Integer>() {}.getType();
          Integer object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public java.util.List getSalesNumber(int year)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("year",new Gson().toJson(year));
          data.method = "getSalesNumber";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.ordermanager.data.Statistic>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     *
     * @param order
     * @return
     * @throws ErrorException
     */
     public java.util.List getTaxes(com.thundashop.core.ordermanager.data.Order order)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("order",new Gson().toJson(order));
          data.method = "getTaxes";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.cartmanager.data.CartTax>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Calculate the total amount to pay for the order.
     *
     * @param order
     * @return
     */
     public java.lang.Double getTotalAmount(com.thundashop.core.ordermanager.data.Order order)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("order",new Gson().toJson(order));
          data.method = "getTotalAmount";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.Double>() {}.getType();
          java.lang.Double object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public double getTotalSalesAmount(java.lang.Integer year)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("year",new Gson().toJson(year));
          data.method = "getTotalSalesAmount";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<double>() {}.getType();
          double object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Update or modify an existing order.
     * @param order The order to modify
     * @return
     * @throws ErrorException
     */
     public void saveOrder(com.thundashop.core.ordermanager.data.Order order)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("order",new Gson().toJson(order));
          data.method = "saveOrder";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
     }

     /**
     * Returns how many pages there is for this store with the given pagesize
     * @return
     */
     public java.util.List searchForOrders(java.lang.String searchWord, java.lang.Integer page, java.lang.Integer pageSize)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("searchWord",new Gson().toJson(searchWord));
          data.args.put("page",new Gson().toJson(page));
          data.args.put("pageSize",new Gson().toJson(pageSize));
          data.method = "searchForOrders";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.ordermanager.data.Order>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * If everything is ok, the price is the same as the order and the currency, then update the status.
     * @param password A predefined password needed to update the status.
     * @param orderId The id of the order to update
     * @param currency The currency the transaction returned
     * @param price The price.
     * @throws ErrorException
     */
     public void setOrderStatus(java.lang.String password, java.lang.String orderId, java.lang.String currency, double price, int status)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("password",new Gson().toJson(password));
          data.args.put("orderId",new Gson().toJson(orderId));
          data.args.put("currency",new Gson().toJson(currency));
          data.args.put("price",new Gson().toJson(price));
          data.args.put("status",new Gson().toJson(status));
          data.method = "setOrderStatus";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
     }

}
