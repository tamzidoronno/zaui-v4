package com.thundashop.api.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
import java.util.ArrayList;
import com.thundashop.core.usermanager.data.Address;
import java.util.List;
import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.ordermanager.data.Order;

public class APIOrderManager {

      public Transporter transport;

      public APIOrderManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Change order status of a specified order.
     * The id could be the orderId or the transaction id.
     */

     public void changeOrderStatus(String id, int status)  throws Exception  {
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
     * @return Order
     * @throws ErrorException 
     */

     public Order createOrder(Address address)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("address",new Gson().toJson(address));
          data.method = "createOrder";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Order>() {}.getType();
          Order object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch a single order based on its id.
     * @param orderId
     * @return Order
     * @throws ErrorException 
     */

     public Order getOrder(String orderId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("orderId",new Gson().toJson(orderId));
          data.method = "getOrder";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Order>() {}.getType();
          Order object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * @param id
     * @return Order
     * @throws ErrorException 
     */

     public Order getOrderByincrementOrderId(Integer id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getOrderByincrementOrderId";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Order>() {}.getType();
          Order object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get a list of already created orders.
     * @param orderIds A list of all orders you want to fetch, all orders are retrieved if this list is empty.
     * @param page What page are you fetching (default 0)
     * @param pageSize Number of entries for each page (default 10)
     * @return List<Order>
     * @throws ErrorException 
     */

     public List<Order> getOrders(ArrayList<String> orderIds, Integer page, Integer pageSize)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("orderIds",new Gson().toJson(orderIds));
          data.args.put("page",new Gson().toJson(page));
          data.args.put("pageSize",new Gson().toJson(pageSize));
          data.method = "getOrders";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<Order>>() {}.getType();
          List<Order> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     * 
     * @param order
     * @return List<CartTax>
     * @throws ErrorException 
     */

     public List<CartTax> getTaxes(Order order)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("order",new Gson().toJson(order));
          data.method = "getTaxes";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<CartTax>>() {}.getType();
          List<CartTax> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Calculate the total amount to pay for the order.
     * 
     * @param order
     * @return Double
     */

     public Double getTotalAmount(Order order)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("order",new Gson().toJson(order));
          data.method = "getTotalAmount";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Double>() {}.getType();
          Double object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Update or modify an existing order. 
     * @param order The order to modify
     * @return void
     * @throws ErrorException 
     */

     public void saveOrder(Order order)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("order",new Gson().toJson(order));
          data.method = "saveOrder";
          data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(data);
     }

     /**
     * If everything is ok, the price is the same as the order and the currency, then update the status.
     * @param password A predefined password needed to update the status.
     * @param orderId The id of the order to update
     * @param currency The currency the transaction returned
     * @param price The price.
     * @throws ErrorException 
     */

     public void setOrderStatus(String password, String orderId, String currency, double price, int status)  throws Exception  {
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
