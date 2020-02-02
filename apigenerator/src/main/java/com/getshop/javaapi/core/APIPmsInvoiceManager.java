package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPmsInvoiceManager {

      public Communicator transport;

      public APIPmsInvoiceManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Pms invoice system.
     */
     public JsonElement autoCreateOrderForBookingAndRoom(String gs_multiLevelName, Object roomBookingId, Object paymentMethod)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomBookingId",new Gson().toJson(roomBookingId));
          gs_json_object_data.args.put("paymentMethod",new Gson().toJson(paymentMethod));
          gs_json_object_data.method = "autoCreateOrderForBookingAndRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * The dates are included the given dates.
     *
     * @param roomBookingId
     * @param paymentMethod
     * @param start
     * @param end
     * @return
     */
     public JsonElement autoCreateOrderForBookingAndRoomBetweenDates(String gs_multiLevelName, Object roomBookingId, Object paymentMethod, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomBookingId",new Gson().toJson(roomBookingId));
          gs_json_object_data.args.put("paymentMethod",new Gson().toJson(paymentMethod));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "autoCreateOrderForBookingAndRoomBetweenDates";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement autoSendPaymentLink(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "autoSendPaymentLink";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement calculatePriceMatrix(String gs_multiLevelName, Object booking, Object room)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("booking",new Gson().toJson(booking));
          gs_json_object_data.args.put("room",new Gson().toJson(room));
          gs_json_object_data.method = "calculatePriceMatrix";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public void clearOrder(String gs_multiLevelName, Object bookingId, Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "clearOrder";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms invoice system.
     */
     public JsonElement convertCartToOrders(String gs_multiLevelName, Object id, Object address, Object paymentId, Object orderCreationType, Object overrideDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("address",new Gson().toJson(address));
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.args.put("orderCreationType",new Gson().toJson(orderCreationType));
          gs_json_object_data.args.put("overrideDate",new Gson().toJson(overrideDate));
          gs_json_object_data.method = "convertCartToOrders";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement createOrder(String gs_multiLevelName, Object bookingId, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "createOrder";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement createOrderOnUnsettledAmount(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "createOrderOnUnsettledAmount";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public void createPeriodeInvoice(String gs_multiLevelName, Object start, Object end, Object amount, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("amount",new Gson().toJson(amount));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "createPeriodeInvoice";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms invoice system.
     */
     public JsonElement createRegisterCardOrder(String gs_multiLevelName, Object item)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("item",new Gson().toJson(item));
          gs_json_object_data.method = "createRegisterCardOrder";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public void creditOrder(String gs_multiLevelName, Object bookingId, Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "creditOrder";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms invoice system.
     */
     public void deleteStatisticsFilter(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteStatisticsFilter";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms invoice system.
     */
     public void deleteYieldPlan(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteYieldPlan";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms invoice system.
     */
     public JsonElement fetchDibsOrdersToAutoPay(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "fetchDibsOrdersToAutoPay";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement generateStatistics(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "generateStatistics";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getAccountingStatistics(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getAccountingStatistics";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getAdvancePriceYieldPlan(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getAdvancePriceYieldPlan";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getAllAdvancePriceYields(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAllAdvancePriceYields";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getAllStatisticsFilters(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAllStatisticsFilters";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getAllUnpaidItemsForRoom(String gs_multiLevelName, Object pmsRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsRoomId",new Gson().toJson(pmsRoomId));
          gs_json_object_data.method = "getAllUnpaidItemsForRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getAllUnpaidOrdersForRoom(String gs_multiLevelName, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "getAllUnpaidOrdersForRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getDiscountsForUser(String gs_multiLevelName, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getDiscountsForUser";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getOrdersForRoomToPay(String gs_multiLevelName, Object pmsRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsRoomId",new Gson().toJson(pmsRoomId));
          gs_json_object_data.method = "getOrdersForRoomToPay";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getPaymentLinkConfig(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getPaymentLinkConfig";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getPaymentLinkSendingDate(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getPaymentLinkSendingDate";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getPreferredPaymentMethod(String gs_multiLevelName, Object bookingId, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getPreferredPaymentMethod";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getReasonForNotSendingPaymentLink(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getReasonForNotSendingPaymentLink";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getRedirectForBooking(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getRedirectForBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getSubscriptionOverview(String gs_multiLevelName, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getSubscriptionOverview";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getTotalOnOrdersForRoom(String gs_multiLevelName, Object pmsRoomId, Object inctaxes)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsRoomId",new Gson().toJson(pmsRoomId));
          gs_json_object_data.args.put("inctaxes",new Gson().toJson(inctaxes));
          gs_json_object_data.method = "getTotalOnOrdersForRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getTotalOrdersOnBooking(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getTotalOrdersOnBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getTotalPaidOnRoomOrBooking(String gs_multiLevelName, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "getTotalPaidOnRoomOrBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getUnpaidAmountOnBooking(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getUnpaidAmountOnBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement getUserDiscountByCouponCode(String gs_multiLevelName, Object couponCode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("couponCode",new Gson().toJson(couponCode));
          gs_json_object_data.method = "getUserDiscountByCouponCode";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement isRoomPaidFor(String gs_multiLevelName, Object pmsRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsRoomId",new Gson().toJson(pmsRoomId));
          gs_json_object_data.method = "isRoomPaidFor";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public void markOrderAsPaid(String gs_multiLevelName, Object bookingId, Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "markOrderAsPaid";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms invoice system.
     */
     public void recalculateAllBookings(String gs_multiLevelName, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "recalculateAllBookings";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms invoice system.
     */
     public JsonElement removeDuplicateOrderLines(String gs_multiLevelName, Object order)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("order",new Gson().toJson(order));
          gs_json_object_data.method = "removeDuplicateOrderLines";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement removeOrderLinesOnOrdersForBooking(String gs_multiLevelName, Object id, Object roomIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("roomIds",new Gson().toJson(roomIds));
          gs_json_object_data.method = "removeOrderLinesOnOrdersForBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public void saveAdvancePriceYield(String gs_multiLevelName, Object yieldPlan)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("yieldPlan",new Gson().toJson(yieldPlan));
          gs_json_object_data.method = "saveAdvancePriceYield";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms invoice system.
     */
     public void saveDiscounts(String gs_multiLevelName, Object discounts)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("discounts",new Gson().toJson(discounts));
          gs_json_object_data.method = "saveDiscounts";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms invoice system.
     */
     public void savePaymentLinkConfig(String gs_multiLevelName, Object config)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("config",new Gson().toJson(config));
          gs_json_object_data.method = "savePaymentLinkConfig";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms invoice system.
     */
     public void saveStatisticsFilter(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "saveStatisticsFilter";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms invoice system.
     */
     public JsonElement sendRecieptOrInvoice(String gs_multiLevelName, Object orderId, Object email, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "sendRecieptOrInvoice";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement sendRecieptOrInvoiceWithMessage(String gs_multiLevelName, Object orderId, Object email, Object bookingId, Object message, Object subject)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.args.put("subject",new Gson().toJson(subject));
          gs_json_object_data.method = "sendRecieptOrInvoiceWithMessage";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public JsonElement supportsDailyPmsInvoiceing(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "supportsDailyPmsInvoiceing";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms invoice system.
     */
     public void toggleNewPaymentProcess(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "toggleNewPaymentProcess";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms invoice system.
     */
     public JsonElement validateAllInvoiceToDates(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "validateAllInvoiceToDates";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
