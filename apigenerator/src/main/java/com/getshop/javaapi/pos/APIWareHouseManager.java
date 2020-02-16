package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIWareHouseManager {

      public Communicator transport;

      public APIWareHouseManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void addWareHouseLocation(Object location)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("location",new Gson().toJson(location));
          gs_json_object_data.method = "addWareHouseLocation";
          gs_json_object_data.interfaceName = "core.warehousemanager.IWareHouseManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void adjustStockOrderedQuantity(Object productId, Object quantity, Object warehouseId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("quantity",new Gson().toJson(quantity));
          gs_json_object_data.args.put("warehouseId",new Gson().toJson(warehouseId));
          gs_json_object_data.method = "adjustStockOrderedQuantity";
          gs_json_object_data.interfaceName = "core.warehousemanager.IWareHouseManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void adjustStockQuantity(Object productId, Object quantity, Object warehouseId, Object comment)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("quantity",new Gson().toJson(quantity));
          gs_json_object_data.args.put("warehouseId",new Gson().toJson(warehouseId));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.method = "adjustStockQuantity";
          gs_json_object_data.interfaceName = "core.warehousemanager.IWareHouseManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void createWareHouse(Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "createWareHouse";
          gs_json_object_data.interfaceName = "core.warehousemanager.IWareHouseManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteWareHouse(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteWareHouse";
          gs_json_object_data.interfaceName = "core.warehousemanager.IWareHouseManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteWareHouseLocation(Object wareHouseLocationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("wareHouseLocationId",new Gson().toJson(wareHouseLocationId));
          gs_json_object_data.method = "deleteWareHouseLocation";
          gs_json_object_data.interfaceName = "core.warehousemanager.IWareHouseManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the report for a specific warehouse, but if you leave the warehouseid blank
     * it will return an overview for all warehouses.
     *
     * @param wareHouseId
     * @param year
     * @param month
     * @return
     */
     public JsonElement getMonthStockReport(Object wareHouseId, Object year, Object month)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("wareHouseId",new Gson().toJson(wareHouseId));
          gs_json_object_data.args.put("year",new Gson().toJson(year));
          gs_json_object_data.args.put("month",new Gson().toJson(month));
          gs_json_object_data.method = "getMonthStockReport";
          gs_json_object_data.interfaceName = "core.warehousemanager.IWareHouseManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getStockQuantityForWareHouseBetweenDate(Object warehouseId, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("warehouseId",new Gson().toJson(warehouseId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getStockQuantityForWareHouseBetweenDate";
          gs_json_object_data.interfaceName = "core.warehousemanager.IWareHouseManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getStockQuantityRowsForProduct(Object productId, Object limit)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("limit",new Gson().toJson(limit));
          gs_json_object_data.method = "getStockQuantityRowsForProduct";
          gs_json_object_data.interfaceName = "core.warehousemanager.IWareHouseManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getWareHouse(Object wareHouseId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("wareHouseId",new Gson().toJson(wareHouseId));
          gs_json_object_data.method = "getWareHouse";
          gs_json_object_data.interfaceName = "core.warehousemanager.IWareHouseManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getWareHouseLocation(Object wareHouseLocationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("wareHouseLocationId",new Gson().toJson(wareHouseLocationId));
          gs_json_object_data.method = "getWareHouseLocation";
          gs_json_object_data.interfaceName = "core.warehousemanager.IWareHouseManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getWareHouses()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getWareHouses";
          gs_json_object_data.interfaceName = "core.warehousemanager.IWareHouseManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void setAsDefaultWareHosue(Object wareHouseId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("wareHouseId",new Gson().toJson(wareHouseId));
          gs_json_object_data.method = "setAsDefaultWareHosue";
          gs_json_object_data.interfaceName = "core.warehousemanager.IWareHouseManager";
          String result = transport.send(gs_json_object_data);
     }

}
