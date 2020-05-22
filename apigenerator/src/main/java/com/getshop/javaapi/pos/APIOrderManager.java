package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIOrderManager {

      public Communicator transport;

      public APIOrderManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Get a list of already created orders.
     * @param orderIds A list of all orders you want to fetch, all orders are retrieved if this list is empty.
     * @param page What page are you fetching (default 0)
     * @param pageSize Number of entries for each page (default 10)
     * @return
     * @throws ErrorException
     */
     public void addProductToOrder(Object orderId, Object productId, Object count)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("count",new Gson().toJson(count));
          gs_json_object_data.method = "addProductToOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     *
     * @param order
     * @return
     * @throws ErrorException
     */
     public void cancelIntegratedPaymentProcess(Object token)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.method = "cancelIntegratedPaymentProcess";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     *
     * @param order
     * @return
     * @throws ErrorException
     */
     public void changeAutoClosePeriodesOnZRepport(Object autoClose)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("autoClose",new Gson().toJson(autoClose));
          gs_json_object_data.method = "changeAutoClosePeriodesOnZRepport";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Change order status of a specified order.
     * The id could be the orderId or the transaction id.
     */
     public void changeOrderStatus(Object id, Object status)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("status",new Gson().toJson(status));
          gs_json_object_data.method = "changeOrderStatus";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     *
     * @param order
     * @return
     * @throws ErrorException
     */
     public void changeProductOnCartItem(Object orderId, Object cartItemId, Object productId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("cartItemId",new Gson().toJson(cartItemId));
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.method = "changeProductOnCartItem";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * @param id
     * @return
     * @throws ErrorException
     */
     public JsonElement changeUserOnOrder(Object orderId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "changeUserOnOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public void closeOrder(Object orderId, Object reason)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("reason",new Gson().toJson(reason));
          gs_json_object_data.method = "closeOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create an order out of a given cart.
     * @param cart The cart object generated by cartmanager.
     * @return
     * @throws ErrorException
     */
     public JsonElement createOrder(Object address)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("address",new Gson().toJson(address));
          gs_json_object_data.method = "createOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public void deleteOrder(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "deleteOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get a list of already created orders.
     * @param orderIds A list of all orders you want to fetch, all orders are retrieved if this list is empty.
     * @param page What page are you fetching (default 0)
     * @param pageSize Number of entries for each page (default 10)
     * @return
     * @throws ErrorException
     */
     public void doRoundUpOnCurrentOrder(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "doRoundUpOnCurrentOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     *
     * @param order
     * @return
     * @throws ErrorException
     */
     public JsonElement getAccountingDetails()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAccountingDetails";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create an order out of a given cart.
     * @param cart The cart object generated by cartmanager.
     * @return
     * @throws ErrorException
     */
     public JsonElement getEarliestPostingDate()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getEarliestPostingDate";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * @param id
     * @return
     * @throws ErrorException
     */
     public JsonElement getIncrementalOrderIdByOrderId(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "getIncrementalOrderIdByOrderId";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getLastTerminalMessage(Object paymentMethodId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentMethodId",new Gson().toJson(paymentMethodId));
          gs_json_object_data.method = "getLastTerminalMessage";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getOrder(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "getOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * @param id
     * @return
     * @throws ErrorException
     */
     public JsonElement getOrderByincrementOrderId(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getOrderByincrementOrderId";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getOrderLight(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "getOrderLight";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getOrderManagerSettings()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getOrderManagerSettings";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getOrders(Object orderIds, Object page, Object pageSize)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderIds",new Gson().toJson(orderIds));
          gs_json_object_data.args.put("page",new Gson().toJson(page));
          gs_json_object_data.args.put("pageSize",new Gson().toJson(pageSize));
          gs_json_object_data.method = "getOrders";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Calculate the total amount to pay for the order.
     *
     * @param order
     * @return
     */
     public JsonElement getRestToPay(Object order)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("order",new Gson().toJson(order));
          gs_json_object_data.method = "getRestToPay";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getTaxes(Object order)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("order",new Gson().toJson(order));
          gs_json_object_data.method = "getTaxes";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getTerminalInformation(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "getTerminalInformation";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Calculate the total amount to pay for the order.
     *
     * @param order
     * @return
     */
     public JsonElement getTotalAmount(Object order)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("order",new Gson().toJson(order));
          gs_json_object_data.method = "getTotalAmount";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Calculate the total amount to pay for the order.
     *
     * @param order
     * @return
     */
     public JsonElement getTotalAmountExTaxes(Object order)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("order",new Gson().toJson(order));
          gs_json_object_data.method = "getTotalAmountExTaxes";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getTotalForOrderById(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "getTotalForOrderById";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getTotalForOrderInLocalCurrencyById(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "getTotalForOrderInLocalCurrencyById";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement isLocked(Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "isLocked";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement isStockManagementActive()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "isStockManagementActive";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public void logTransactionEntry(Object orderId, Object entry)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("entry",new Gson().toJson(entry));
          gs_json_object_data.method = "logTransactionEntry";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Update or modify an existing order.
     * @param order The order to modify
     * @return
     * @throws ErrorException
     */
     public void markAsPaid(Object order)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("order",new Gson().toJson(order));
          gs_json_object_data.method = "markAsPaid";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create an order out of a given cart.
     * @param cart The cart object generated by cartmanager.
     * @return
     * @throws ErrorException
     */
     public void paymentResponse(Object tokenId, Object response)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tokenId",new Gson().toJson(tokenId));
          gs_json_object_data.args.put("response",new Gson().toJson(response));
          gs_json_object_data.method = "paymentResponse";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     *
     * @param order
     * @return
     * @throws ErrorException
     */
     public void paymentText(Object tokenId, Object text)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tokenId",new Gson().toJson(tokenId));
          gs_json_object_data.args.put("text",new Gson().toJson(text));
          gs_json_object_data.method = "paymentText";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     *
     * @param order
     * @return
     * @throws ErrorException
     */
     public void printInvoice(Object orderId, Object printerId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("printerId",new Gson().toJson(printerId));
          gs_json_object_data.method = "printInvoice";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     *
     * @param order
     * @return
     * @throws ErrorException
     */
     public void receiptText(Object token, Object terminalReceiptText)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.args.put("terminalReceiptText",new Gson().toJson(terminalReceiptText));
          gs_json_object_data.method = "receiptText";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     *
     * @param order
     * @return
     * @throws ErrorException
     */
     public void saveAccountingDetails(Object details)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("details",new Gson().toJson(details));
          gs_json_object_data.method = "saveAccountingDetails";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Update or modify an existing order.
     * @param order The order to modify
     * @return
     * @throws ErrorException
     */
     public void saveOrder(Object order)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("order",new Gson().toJson(order));
          gs_json_object_data.method = "saveOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     *
     * @param order
     * @return
     * @throws ErrorException
     */
     public void setNewStartIncrementalOrderId(Object incrementalOrderId, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("incrementalOrderId",new Gson().toJson(incrementalOrderId));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "setNewStartIncrementalOrderId";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     *
     * @param order
     * @return
     * @throws ErrorException
     */
     public void startCheckForOrdersToCapture(Object internalPassword)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("internalPassword",new Gson().toJson(internalPassword));
          gs_json_object_data.method = "startCheckForOrdersToCapture";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     *
     * @param order
     * @return
     * @throws ErrorException
     */
     public void updateCartItemOnOrder(Object orderId, Object cartItem)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("cartItem",new Gson().toJson(cartItem));
          gs_json_object_data.method = "updateCartItemOnOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get a list of already created orders.
     * @param orderIds A list of all orders you want to fetch, all orders are retrieved if this list is empty.
     * @param page What page are you fetching (default 0)
     * @param pageSize Number of entries for each page (default 10)
     * @return
     * @throws ErrorException
     */
     public void updateCountForOrderLine(Object cartItemId, Object orderId, Object count)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cartItemId",new Gson().toJson(cartItemId));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("count",new Gson().toJson(count));
          gs_json_object_data.method = "updateCountForOrderLine";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list over taxes
     * for the specified order.
     *
     * @param order
     * @return
     * @throws ErrorException
     */
     public void updatePriceForOrderLine(Object cartItemId, Object orderId, Object price)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cartItemId",new Gson().toJson(cartItemId));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("price",new Gson().toJson(price));
          gs_json_object_data.method = "updatePriceForOrderLine";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

}
