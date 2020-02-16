package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIGetShopAccountingManager {

      public Communicator transport;

      public APIGetShopAccountingManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public JsonElement canOrderBeTransferredDirect(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "canOrderBeTransferredDirect";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createBankTransferFile()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "createBankTransferFile";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createNextOrderFile(Object endDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("endDate",new Gson().toJson(endDate));
          gs_json_object_data.method = "createNextOrderFile";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void deleteFile(Object fileId, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "deleteFile";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getConfigOptions(Object systemType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemType",new Gson().toJson(systemType));
          gs_json_object_data.method = "getConfigOptions";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getConfigs(Object systemType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemType",new Gson().toJson(systemType));
          gs_json_object_data.method = "getConfigs";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getCurrentSystemInvoices()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCurrentSystemInvoices";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getCurrentSystemOther()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCurrentSystemOther";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getListOfSystems()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getListOfSystems";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLogEntries()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getLogEntries";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getOrderFile(Object fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "getOrderFile";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getOrderFiles()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getOrderFiles";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getOrdersToIncludeForNextTransfer(Object endDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("endDate",new Gson().toJson(endDate));
          gs_json_object_data.method = "getOrdersToIncludeForNextTransfer";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPreviouseEndDate()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getPreviouseEndDate";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTransferData(Object start, Object end, Object doublePostingFileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("doublePostingFileId",new Gson().toJson(doublePostingFileId));
          gs_json_object_data.method = "getTransferData";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement isCurrentSelectedAccountingSystemPrimitive()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "isCurrentSelectedAccountingSystemPrimitive";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement isCurrentSelectedSupportingDirectTransfer()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "isCurrentSelectedSupportingDirectTransfer";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void setConfig(Object systemType, Object key, Object value)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemType",new Gson().toJson(systemType));
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.args.put("value",new Gson().toJson(value));
          gs_json_object_data.method = "setConfig";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setSystemTypeInvoice(Object systemType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemType",new Gson().toJson(systemType));
          gs_json_object_data.method = "setSystemTypeInvoice";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setSystemTypeOther(Object systemType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemType",new Gson().toJson(systemType));
          gs_json_object_data.method = "setSystemTypeOther";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void transferAllDaysThatCanBeTransferred()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "transferAllDaysThatCanBeTransferred";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void transferData(Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "transferData";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement transferDirect(Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "transferDirect";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void transferDoublePostFile(Object doublePostFileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("doublePostFileId",new Gson().toJson(doublePostFileId));
          gs_json_object_data.method = "transferDoublePostFile";
          gs_json_object_data.interfaceName = "core.getshopaccounting.IGetShopAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

}
