package com.getshop.javaapi.core;

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
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public void addClosedPeriode(Object closed)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("closed",new Gson().toJson(closed));
          gs_json_object_data.method = "addClosedPeriode";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public void addOrderTransaction(Object orderId, Object amount, Object comment, Object paymentDate, Object amountInLocalCurrency, Object agio, Object accountDetailId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("amount",new Gson().toJson(amount));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.args.put("paymentDate",new Gson().toJson(paymentDate));
          gs_json_object_data.args.put("amountInLocalCurrency",new Gson().toJson(amountInLocalCurrency));
          gs_json_object_data.args.put("agio",new Gson().toJson(agio));
          gs_json_object_data.args.put("accountDetailId",new Gson().toJson(accountDetailId));
          gs_json_object_data.method = "addOrderTransaction";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Fetch all orders for a user.
     *
     * @param userId
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
     * Fetch all orders for a user.
     *
     * @param userId
     * @return
     * @throws ErrorException
     */
     public void addSpecialPaymentTransactions(Object orderId, Object amount, Object amountInLocalCurrency, Object transactionType, Object comment, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("amount",new Gson().toJson(amount));
          gs_json_object_data.args.put("amountInLocalCurrency",new Gson().toJson(amountInLocalCurrency));
          gs_json_object_data.args.put("transactionType",new Gson().toJson(transactionType));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "addSpecialPaymentTransactions";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public void applyCorrectionForOrder(Object orderId, Object passord)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("passord",new Gson().toJson(passord));
          gs_json_object_data.method = "applyCorrectionForOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public void cancelPaymentProcess(Object tokenId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tokenId",new Gson().toJson(tokenId));
          gs_json_object_data.method = "cancelPaymentProcess";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
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
     * Change order status of a specified order.
     * The id could be the orderId or the transaction id.
     */
     public void changeOrderStatusWithPassword(Object id, Object status, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("status",new Gson().toJson(status));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "changeOrderStatusWithPassword";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public void changeOrderType(Object orderId, Object paymentTypeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("paymentTypeId",new Gson().toJson(paymentTypeId));
          gs_json_object_data.method = "changeOrderType";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
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
     * If everything is ok, the price is the same as the order and the currency, then update the status.
     * @param password A predefined password needed to update the status.
     * @param orderId The id of the order to update
     * @param currency The currency the transaction returned
     * @param price The price.
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
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public void chargeOrder(Object order, Object tokenId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("order",new Gson().toJson(order));
          gs_json_object_data.args.put("tokenId",new Gson().toJson(tokenId));
          gs_json_object_data.method = "chargeOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public void checkForOrdersFailedCollecting()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "checkForOrdersFailedCollecting";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Got a reference number for the order, fetch it from here.
     * @param referenceId
     * @throws ErrorException
     */
     public void checkForOrdersToAutoPay(Object daysToTryAfterOrderHasStarted)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("daysToTryAfterOrderHasStarted",new Gson().toJson(daysToTryAfterOrderHasStarted));
          gs_json_object_data.method = "checkForOrdersToAutoPay";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Got a reference number for the order, fetch it from here.
     * @param referenceId
     * @throws ErrorException
     */
     public void checkForOrdersToCapture(Object internalPassword)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("internalPassword",new Gson().toJson(internalPassword));
          gs_json_object_data.method = "checkForOrdersToCapture";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public void checkGroupInvoicing(Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "checkGroupInvoicing";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public void cleanOrder(Object orderId, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "cleanOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public void cleanupMessedUpOrderTransactionForForignCurrencyCreditNotes(Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "cleanupMessedUpOrderTransactionForForignCurrencyCreditNotes";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public void clearMessages()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "clearMessages";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public void closeBankAccount(Object endDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("endDate",new Gson().toJson(endDate));
          gs_json_object_data.method = "closeBankAccount";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
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
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public void closeTransactionPeriode(Object closeDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("closeDate",new Gson().toJson(closeDate));
          gs_json_object_data.method = "closeTransactionPeriode";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public void createNewDoubleTransferFile(Object paymentId, Object from, Object to)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.args.put("from",new Gson().toJson(from));
          gs_json_object_data.args.put("to",new Gson().toJson(to));
          gs_json_object_data.method = "createNewDoubleTransferFile";
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
     * If a customer is providing a customer reference id, it should be possible to create order by it.
     * @param cart The cart object generated by cartmanager.
     * @return
     * @throws ErrorException
     */
     public JsonElement createOrderByCustomerReference(Object referenceKey)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("referenceKey",new Gson().toJson(referenceKey));
          gs_json_object_data.method = "createOrderByCustomerReference";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public JsonElement createOrderForUser(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "createOrderForUser";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public JsonElement createRegisterCardOrder(Object paymentType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentType",new Gson().toJson(paymentType));
          gs_json_object_data.method = "createRegisterCardOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all orders for a user.
     *
     * @param userId
     * @return
     * @throws ErrorException
     */
     public JsonElement creditOrder(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "creditOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public void deleteAllVirtualOrders()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "deleteAllVirtualOrders";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public void deleteDoublePostingFile(Object fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "deleteDoublePostingFile";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public void deleteFreePost(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteFreePost";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
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
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public JsonElement filterOrdersIsCredittedAndPaidFor(Object orderIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderIds",new Gson().toJson(orderIds));
          gs_json_object_data.method = "filterOrdersIsCredittedAndPaidFor";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public void forceChangeOverrideAccountingDate(Object password, Object orderId, Object overrideDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("overrideDate",new Gson().toJson(overrideDate));
          gs_json_object_data.method = "forceChangeOverrideAccountingDate";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public void forceDeleteOrder(Object orderId, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "forceDeleteOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public void forceSetNewPaymentDate(Object orderId, Object date, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "forceSetNewPaymentDate";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public JsonElement getAccountFreePost(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getAccountFreePost";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
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
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getActualDayIncome(Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getActualDayIncome";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public JsonElement getAllDoublePostTransferFiles(Object paymentId, Object from, Object to)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.args.put("from",new Gson().toJson(from));
          gs_json_object_data.args.put("to",new Gson().toJson(to));
          gs_json_object_data.method = "getAllDoublePostTransferFiles";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public JsonElement getAllOrdersForRoom(Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "getAllOrdersForRoom";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all orders for a user.
     *
     * @param userId
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllOrdersForUser(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getAllOrdersForUser";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all orders on product.
     * @param userId
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllOrdersOnProduct(Object productId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.method = "getAllOrdersOnProduct";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public JsonElement getAllTransactionsForInvoices(Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getAllTransactionsForInvoices";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getAllUnpaid(Object paymentMethod)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentMethod",new Gson().toJson(paymentMethod));
          gs_json_object_data.method = "getAllUnpaid";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getAllUnpaidInvoices()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllUnpaidInvoices";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public JsonElement getAutoCreatedOrdersForConference(Object conferenceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("conferenceId",new Gson().toJson(conferenceId));
          gs_json_object_data.method = "getAutoCreatedOrdersForConference";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public JsonElement getBalance(Object date, Object paymentId, Object incTaxes)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.args.put("incTaxes",new Gson().toJson(incTaxes));
          gs_json_object_data.method = "getBalance";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getBankOrderTransactions()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getBankOrderTransactions";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public JsonElement getCurrentPaymentOrderId()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCurrentPaymentOrderId";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getDayEntriesForOrder(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "getDayEntriesForOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getDayIncomes(Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getDayIncomes";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getDayIncomesWithMetaData(Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getDayIncomesWithMetaData";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public JsonElement getDiffReport(Object start, Object end, Object incTaxes)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("incTaxes",new Gson().toJson(incTaxes));
          gs_json_object_data.method = "getDiffReport";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public JsonElement getDoublePostAccountingTransfer(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getDoublePostAccountingTransfer";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public JsonElement getDoublePostingDayIncomes(Object paymentId, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getDoublePostingDayIncomes";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
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
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getEhfXml(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "getEhfXml";
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
     * Calculate the total amount to pay for the order.
     *
     * @param order
     * @return
     */
     public JsonElement getItemDates(Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getItemDates";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getMostSoldProducts(Object numberOfProducts)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("numberOfProducts",new Gson().toJson(numberOfProducts));
          gs_json_object_data.method = "getMostSoldProducts";
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
     public JsonElement getMyPrefferedPaymentMethod()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getMyPrefferedPaymentMethod";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public JsonElement getNameOnOrder(Object orderId, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "getNameOnOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a single order based on its id.
     * @param orderId
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
     * Got a reference number for the order, fetch it from here.
     * @param referenceId
     * @throws ErrorException
     */
     public JsonElement getOrderByReference(Object referenceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("referenceId",new Gson().toJson(referenceId));
          gs_json_object_data.method = "getOrderByReference";
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
     * @param id
     * @return
     * @throws ErrorException
     */
     public JsonElement getOrderByincrementOrderIdAndPassword(Object id, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "getOrderByincrementOrderIdAndPassword";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a single order based on its id.
     * @param orderId
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
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
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
     * Fetch a single order based on its id.
     * @param orderId
     * @return
     * @throws ErrorException
     */
     public JsonElement getOrderSecure(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "getOrderSecure";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a single order based on its id.
     * @param orderId
     * @return
     * @throws ErrorException
     */
     public JsonElement getOrderWithIdAndPassword(Object orderId, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "getOrderWithIdAndPassword";
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
     * Get a list of already created orders.
     * @param orderIds A list of all orders you want to fetch, all orders are retrieved if this list is empty.
     * @param page What page are you fetching (default 0)
     * @param pageSize Number of entries for each page (default 10)
     * @return
     * @throws ErrorException
     */
     public JsonElement getOrdersByFilter(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getOrdersByFilter";
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
     public JsonElement getOrdersFiltered(Object filterOptions)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filterOptions",new Gson().toJson(filterOptions));
          gs_json_object_data.method = "getOrdersFiltered";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a single order based on its id.
     * @param orderId
     * @return
     * @throws ErrorException
     */
     public JsonElement getOrdersFromPeriode(Object start, Object end, Object statistics)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("statistics",new Gson().toJson(statistics));
          gs_json_object_data.method = "getOrdersFromPeriode";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getOrdersNotTransferredToAccountingSystem()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getOrdersNotTransferredToAccountingSystem";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getOrdersPaid(Object paymentId, Object userId, Object from, Object to)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("from",new Gson().toJson(from));
          gs_json_object_data.args.put("to",new Gson().toJson(to));
          gs_json_object_data.method = "getOrdersPaid";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getOrdersToCapture()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getOrdersToCapture";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public JsonElement getOrdersUnsettledAmount(Object accountNumber, Object date, Object paymentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("accountNumber",new Gson().toJson(accountNumber));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.method = "getOrdersUnsettledAmount";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public JsonElement getOverdueInvoices(Object filterData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filterData",new Gson().toJson(filterData));
          gs_json_object_data.method = "getOverdueInvoices";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns how many pages there is for this store with the given pagesize
     * @return
     */
     public JsonElement getPageCount(Object pageSize, Object searchWord)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageSize",new Gson().toJson(pageSize));
          gs_json_object_data.args.put("searchWord",new Gson().toJson(searchWord));
          gs_json_object_data.method = "getPageCount";
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
     public JsonElement getPaymentMethodsThatHasOrders()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getPaymentMethodsThatHasOrders";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public JsonElement getPaymentRecords(Object paymentId, Object from, Object to)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.args.put("from",new Gson().toJson(from));
          gs_json_object_data.args.put("to",new Gson().toJson(to));
          gs_json_object_data.method = "getPaymentRecords";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public JsonElement getPmiResult(Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getPmiResult";
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
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getSalesNumber(Object year)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("year",new Gson().toJson(year));
          gs_json_object_data.method = "getSalesNumber";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getSalesStatistics(Object startDate, Object endDate, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("endDate",new Gson().toJson(endDate));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "getSalesStatistics";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getStorePreferredPayementMethod()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getStorePreferredPayementMethod";
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
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
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
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public JsonElement getTerminalMessages()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getTerminalMessages";
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
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
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
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
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
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public JsonElement getTotalOutstandingInvoices(Object filterData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filterData",new Gson().toJson(filterData));
          gs_json_object_data.method = "getTotalOutstandingInvoices";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public JsonElement getTotalOutstandingInvoicesOverdue(Object filterData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filterData",new Gson().toJson(filterData));
          gs_json_object_data.method = "getTotalOutstandingInvoicesOverdue";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getTotalSalesAmount(Object year, Object month, Object week, Object day, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("year",new Gson().toJson(year));
          gs_json_object_data.args.put("month",new Gson().toJson(month));
          gs_json_object_data.args.put("week",new Gson().toJson(week));
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "getTotalSalesAmount";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement getUnpaidOrderIdsThatAreIllegalWhenClosingPeriode()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getUnpaidOrderIdsThatAreIllegalWhenClosingPeriode";
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
     public JsonElement getUserPrefferedPaymentMethod(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getUserPrefferedPaymentMethod";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Got a reference number for the order, fetch it from here.
     * @param referenceId
     * @throws ErrorException
     */
     public JsonElement hasNoOrders()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "hasNoOrders";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement isBankAccountClosed(Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "isBankAccountClosed";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement isConfiguredForEhf()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "isConfiguredForEhf";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
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
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public JsonElement isPaymentInProgress()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "isPaymentInProgress";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
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
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
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
     * Change order status of a specified order.
     * The id could be the orderId or the transaction id.
     */
     public void markAsInvoicePayment(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "markAsInvoicePayment";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Update or modify an existing order.
     * @param order The order to modify
     * @return
     * @throws ErrorException
     */
     public void markAsPaid(Object orderId, Object date, Object amount)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.args.put("amount",new Gson().toJson(amount));
          gs_json_object_data.method = "markAsPaid";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Update or modify an existing order.
     * @param order The order to modify
     * @return
     * @throws ErrorException
     */
     public void markAsPaidWithPassword(Object orderId, Object date, Object amount, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.args.put("amount",new Gson().toJson(amount));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "markAsPaidWithPassword";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public void markOrderAsBillabe(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "markOrderAsBillabe";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public JsonElement mergeAndCreateNewOrder(Object userId, Object orderIds, Object paymentMethod, Object note)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("orderIds",new Gson().toJson(orderIds));
          gs_json_object_data.args.put("paymentMethod",new Gson().toJson(paymentMethod));
          gs_json_object_data.args.put("note",new Gson().toJson(note));
          gs_json_object_data.method = "mergeAndCreateNewOrder";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
     */
     public JsonElement orderIsCredittedAndPaidFor(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "orderIsCredittedAndPaidFor";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a single order based on its id.
     * @param orderId
     * @return
     * @throws ErrorException
     */
     public JsonElement payWithCard(Object orderId, Object cardId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("cardId",new Gson().toJson(cardId));
          gs_json_object_data.method = "payWithCard";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
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
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
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
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
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
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public void readdTaxGroupToNullItems(Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "readdTaxGroupToNullItems";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     *
     * @param userId
     */
     public void registerLoss(Object orderId, Object loss, Object comment, Object paymentDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("loss",new Gson().toJson(loss));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.args.put("paymentDate",new Gson().toJson(paymentDate));
          gs_json_object_data.method = "registerLoss";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public void registerSentEhf(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "registerSentEhf";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create an order out of a given cart.
     * @param cart The cart object generated by cartmanager.
     * @return
     * @throws ErrorException
     */
     public void resetLanguageAndCurrencyOnOrders()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "resetLanguageAndCurrencyOnOrders";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public void resetLastMonthClose(Object password, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "resetLastMonthClose";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public JsonElement saveFreePost(Object freePost)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("freePost",new Gson().toJson(freePost));
          gs_json_object_data.method = "saveFreePost";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
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
     * Returns how many pages there is for this store with the given pagesize
     * @return
     */
     public JsonElement searchForOrders(Object searchWord, Object page, Object pageSize)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("searchWord",new Gson().toJson(searchWord));
          gs_json_object_data.args.put("page",new Gson().toJson(page));
          gs_json_object_data.args.put("pageSize",new Gson().toJson(pageSize));
          gs_json_object_data.method = "searchForOrders";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public void sendReciept(Object orderId, Object email)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.method = "sendReciept";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public void sendRecieptWithText(Object orderId, Object email, Object subject, Object text)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.args.put("subject",new Gson().toJson(subject));
          gs_json_object_data.args.put("text",new Gson().toJson(text));
          gs_json_object_data.method = "sendRecieptWithText";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
     */
     public void setExternalRefOnCartItem(Object cartItem, Object externalId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cartItem",new Gson().toJson(cartItem));
          gs_json_object_data.args.put("externalId",new Gson().toJson(externalId));
          gs_json_object_data.method = "setExternalRefOnCartItem";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
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
     * If everything is ok, the price is the same as the order and the currency, then update the status.
     * @param password A predefined password needed to update the status.
     * @param orderId The id of the order to update
     * @param currency The currency the transaction returned
     * @param price The price.
     * @throws ErrorException
     */
     public void setOrderStatus(Object password, Object orderId, Object currency, Object price, Object status)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("currency",new Gson().toJson(currency));
          gs_json_object_data.args.put("price",new Gson().toJson(price));
          gs_json_object_data.args.put("status",new Gson().toJson(status));
          gs_json_object_data.method = "setOrderStatus";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
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
     * Will filter out all the orderids that has a correcsponding
     * paid creditnote.
     *
     * @param orderIds
     * @return
     */
     public void transferToNewFReport()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "transferToNewFReport";
          gs_json_object_data.interfaceName = "core.ordermanager.IOrderManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This function is not in use anywhere so it can be deleted at any time.
     *
     * just a function that fixes a problem that we had by resetting orders
     * that has not been fully completed.
     *
     * @param password
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
     * Fetch all orders for a user.
     *
     * @param userId
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
     * Returns the total amount of sales for a given year. If you year is left blank you
     * will get the total amount for all years.
     *
     * @param year
     * @return
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
