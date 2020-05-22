package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPosManager {

      public Communicator transport;

      public APIPosManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void abortPaymentTransaction(Object paymentId, Object forced)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.args.put("forced",new Gson().toJson(forced));
          gs_json_object_data.method = "abortPaymentTransaction";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void activatePaymentMethod(Object posPaymentMethod)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("posPaymentMethod",new Gson().toJson(posPaymentMethod));
          gs_json_object_data.method = "activatePaymentMethod";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addCartItemsToConference(Object paymentMethodId, Object pmsConferenceId, Object cartItems, Object cashPointId, Object tabId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentMethodId",new Gson().toJson(paymentMethodId));
          gs_json_object_data.args.put("pmsConferenceId",new Gson().toJson(pmsConferenceId));
          gs_json_object_data.args.put("cartItems",new Gson().toJson(cartItems));
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.method = "addCartItemsToConference";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addCartItemsToRoom(Object paymentMethodId, Object roomId, Object cartItems, Object cashPointId, Object tabId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentMethodId",new Gson().toJson(paymentMethodId));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("cartItems",new Gson().toJson(cartItems));
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.method = "addCartItemsToRoom";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addCashWithDrawalToTab(Object tabId, Object amount)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("amount",new Gson().toJson(amount));
          gs_json_object_data.method = "addCashWithDrawalToTab";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addOrderIdToZReport(Object incrementalOrderId, Object zReportId, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("incrementalOrderId",new Gson().toJson(incrementalOrderId));
          gs_json_object_data.args.put("zReportId",new Gson().toJson(zReportId));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "addOrderIdToZReport";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addTextToChef(Object posId, Object textToChef, Object values)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("posId",new Gson().toJson(posId));
          gs_json_object_data.args.put("textToChef",new Gson().toJson(textToChef));
          gs_json_object_data.args.put("values",new Gson().toJson(values));
          gs_json_object_data.method = "addTextToChef";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addToTab(Object tabId, Object cartItem)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("cartItem",new Gson().toJson(cartItem));
          gs_json_object_data.method = "addToTab";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addToTabPga(Object tabId, Object cartItem)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("cartItem",new Gson().toJson(cartItem));
          gs_json_object_data.method = "addToTabPga";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement canCreateZReport(Object pmsBookingMultilevelName, Object cashPointId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pmsBookingMultilevelName",new Gson().toJson(pmsBookingMultilevelName));
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.method = "canCreateZReport";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement canDeleteTab(Object tabId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.method = "canDeleteTab";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void changeListView(Object viewId, Object listId, Object showAsGroupButton)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("viewId",new Gson().toJson(viewId));
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.args.put("showAsGroupButton",new Gson().toJson(showAsGroupButton));
          gs_json_object_data.method = "changeListView";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void changeTaxRate(Object tabId, Object taxGroupNumber)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("taxGroupNumber",new Gson().toJson(taxGroupNumber));
          gs_json_object_data.method = "changeTaxRate";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void completeTransaction(Object tabId, Object orderId, Object cashPointDeviceId, Object kitchenDeviceId, Object paymentMetaData, Object cashPointId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("cashPointDeviceId",new Gson().toJson(cashPointDeviceId));
          gs_json_object_data.args.put("kitchenDeviceId",new Gson().toJson(kitchenDeviceId));
          gs_json_object_data.args.put("paymentMetaData",new Gson().toJson(paymentMetaData));
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.method = "completeTransaction";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void createCashPoint(Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "createCashPoint";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createNewGiftCard(Object cashPointId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.method = "createNewGiftCard";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createNewTab(Object referenceName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("referenceName",new Gson().toJson(referenceName));
          gs_json_object_data.method = "createNewTab";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void createNewTable(Object tableName, Object tableNumber)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tableName",new Gson().toJson(tableName));
          gs_json_object_data.args.put("tableNumber",new Gson().toJson(tableNumber));
          gs_json_object_data.method = "createNewTable";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void createNewView(Object viewName, Object viewType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("viewName",new Gson().toJson(viewName));
          gs_json_object_data.args.put("viewType",new Gson().toJson(viewType));
          gs_json_object_data.method = "createNewView";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createOrder(Object cartItems, Object paymentId, Object tabId, Object cashPointId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cartItems",new Gson().toJson(cartItems));
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.method = "createOrder";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createTabForPga(Object tabId, Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "createTabForPga";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void createZReport(Object cashPointId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.method = "createZReport";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deactivatePaymentMethod(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deactivatePaymentMethod";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteTab(Object tabId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.method = "deleteTab";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteTable(Object tableId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.method = "deleteTable";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteView(Object viewId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("viewId",new Gson().toJson(viewId));
          gs_json_object_data.method = "deleteView";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteZReport(Object zreportId, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("zreportId",new Gson().toJson(zreportId));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "deleteZReport";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void doZReport(Object paymentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.method = "doZReport";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllTabs()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllTabs";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAvailableConferences(Object paymentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.method = "getAvailableConferences";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAvailableRooms(Object paymentMethodId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentMethodId",new Gson().toJson(paymentMethodId));
          gs_json_object_data.method = "getAvailableRooms";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getCashPoint(Object cashPointId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.method = "getCashPoint";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getCashPoints()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCashPoints";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getCurrentTabIdForTableId(Object tableId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.method = "getCurrentTabIdForTableId";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getGiftCardByCode(Object code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "getGiftCardByCode";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getOpenZReport(Object cashPointId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.method = "getOpenZReport";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPayOnRoomTaxMapping(Object paymentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.method = "getPayOnRoomTaxMapping";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPaymentMethod(Object paymentMethodId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentMethodId",new Gson().toJson(paymentMethodId));
          gs_json_object_data.method = "getPaymentMethod";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPaymentMethods()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getPaymentMethods";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPosConference(Object pmsConferenceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pmsConferenceId",new Gson().toJson(pmsConferenceId));
          gs_json_object_data.method = "getPosConference";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPosConferences()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getPosConferences";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPrevZReport(Object cashPointId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.method = "getPrevZReport";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getProductCountForPgaTab(Object tabId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.method = "getProductCountForPgaTab";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getProductList(Object viewId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("viewId",new Gson().toJson(viewId));
          gs_json_object_data.method = "getProductList";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getReportsReadyToTransferToCentral()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getReportsReadyToTransferToCentral";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getSalesReport(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getSalesReport";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTab(Object tabId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.method = "getTab";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTabCount()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getTabCount";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTabForPga(Object tabId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.method = "getTabForPga";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTable(Object viewId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("viewId",new Gson().toJson(viewId));
          gs_json_object_data.method = "getTable";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTables()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getTables";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTaxGroupsForPayOnRoom(Object paymentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.method = "getTaxGroupsForPayOnRoom";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTextToChef()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getTextToChef";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTotal(Object tabId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.method = "getTotal";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTotalForCurrentZReport(Object cashPointId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.method = "getTotalForCurrentZReport";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTotalForItems(Object cartItems)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cartItems",new Gson().toJson(cartItems));
          gs_json_object_data.method = "getTotalForItems";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTotalForZreport(Object zReportId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("zReportId",new Gson().toJson(zReportId));
          gs_json_object_data.method = "getTotalForZreport";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTotalUseOfGiftCards(Object tabId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.method = "getTotalUseOfGiftCards";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getView(Object viewId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("viewId",new Gson().toJson(viewId));
          gs_json_object_data.method = "getView";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getViews()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getViews";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getZReport(Object zReportId, Object cashPointId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("zReportId",new Gson().toJson(zReportId));
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.method = "getZReport";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getZReportsUnfinalized(Object filterOptions)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filterOptions",new Gson().toJson(filterOptions));
          gs_json_object_data.method = "getZReportsUnfinalized";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement hasLockedPeriods()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "hasLockedPeriods";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement hasTables()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "hasTables";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement inProgress(Object paymentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.method = "inProgress";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement isTabFromConference(Object tabId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.method = "isTabFromConference";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void markZReportTransferredToCentral(Object zReportId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("zReportId",new Gson().toJson(zReportId));
          gs_json_object_data.method = "markZReportTransferredToCentral";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void moveContentFromOneTabToAnother(Object from, Object to)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("from",new Gson().toJson(from));
          gs_json_object_data.args.put("to",new Gson().toJson(to));
          gs_json_object_data.method = "moveContentFromOneTabToAnother";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void moveList(Object viewId, Object listId, Object down)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("viewId",new Gson().toJson(viewId));
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.args.put("down",new Gson().toJson(down));
          gs_json_object_data.method = "moveList";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void printKitchen(Object tabId, Object gdsDeviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("gdsDeviceId",new Gson().toJson(gdsDeviceId));
          gs_json_object_data.method = "printKitchen";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void printOverview(Object tabId, Object cashPointDeviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("cashPointDeviceId",new Gson().toJson(cashPointDeviceId));
          gs_json_object_data.method = "printOverview";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void printReceipt(Object orderId, Object cashPointId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.method = "printReceipt";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void printRoomReceipt(Object gdsDeviceId, Object roomName, Object guestName, Object items)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gdsDeviceId",new Gson().toJson(gdsDeviceId));
          gs_json_object_data.args.put("roomName",new Gson().toJson(roomName));
          gs_json_object_data.args.put("guestName",new Gson().toJson(guestName));
          gs_json_object_data.args.put("items",new Gson().toJson(items));
          gs_json_object_data.method = "printRoomReceipt";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void removeFromTab(Object cartItemId, Object tabId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cartItemId",new Gson().toJson(cartItemId));
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.method = "removeFromTab";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void removeGiftCardFromTab(Object tabId, Object cartItemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("cartItemId",new Gson().toJson(cartItemId));
          gs_json_object_data.method = "removeGiftCardFromTab";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void removeItemsFromTab(Object tabId, Object cartItems)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("cartItems",new Gson().toJson(cartItems));
          gs_json_object_data.method = "removeItemsFromTab";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveCashPoint(Object cashPoint)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cashPoint",new Gson().toJson(cashPoint));
          gs_json_object_data.method = "saveCashPoint";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void savePayOnRoomTaxMapping(Object taxMapping)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("taxMapping",new Gson().toJson(taxMapping));
          gs_json_object_data.method = "savePayOnRoomTaxMapping";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveTable(Object table)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("table",new Gson().toJson(table));
          gs_json_object_data.method = "saveTable";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveTextToChef(Object textToChef)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("textToChef",new Gson().toJson(textToChef));
          gs_json_object_data.method = "saveTextToChef";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveView(Object view)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("view",new Gson().toJson(view));
          gs_json_object_data.method = "saveView";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement setDiscountToCartItem(Object tabId, Object cartItemId, Object newValue)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("cartItemId",new Gson().toJson(cartItemId));
          gs_json_object_data.args.put("newValue",new Gson().toJson(newValue));
          gs_json_object_data.method = "setDiscountToCartItem";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement setNewProductPrice(Object tabId, Object cartItemId, Object newValue)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("cartItemId",new Gson().toJson(cartItemId));
          gs_json_object_data.args.put("newValue",new Gson().toJson(newValue));
          gs_json_object_data.method = "setNewProductPrice";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void setTabDiscount(Object tabId, Object discount)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("discount",new Gson().toJson(discount));
          gs_json_object_data.method = "setTabDiscount";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setView(Object cashPointId, Object viewId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.args.put("viewId",new Gson().toJson(viewId));
          gs_json_object_data.method = "setView";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void startIntegratedPaymentProcess(Object orderId, Object paymentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("paymentId",new Gson().toJson(paymentId));
          gs_json_object_data.method = "startIntegratedPaymentProcess";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void toggleExternalAccess(Object cashPointId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cashPointId",new Gson().toJson(cashPointId));
          gs_json_object_data.method = "toggleExternalAccess";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void topupGiftCard(Object tabId, Object code, Object value)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.args.put("value",new Gson().toJson(value));
          gs_json_object_data.method = "topupGiftCard";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void useGiftCard(Object tabId, Object code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tabId",new Gson().toJson(tabId));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "useGiftCard";
          gs_json_object_data.interfaceName = "core.pos.IPosManager";
          String result = transport.send(gs_json_object_data);
     }

}
