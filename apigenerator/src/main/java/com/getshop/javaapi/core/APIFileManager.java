package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIFileManager {

      public Communicator transport;

      public APIFileManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ptonder
     */
     public JsonElement addFileEntry(Object listId, Object entry)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.args.put("entry",new Gson().toJson(entry));
          gs_json_object_data.method = "addFileEntry";
          gs_json_object_data.interfaceName = "core.filemanager.IFileManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ptonder
     */
     public void deleteFileEntry(Object fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "deleteFileEntry";
          gs_json_object_data.interfaceName = "core.filemanager.IFileManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ptonder
     */
     public JsonElement getFile(Object fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "getFile";
          gs_json_object_data.interfaceName = "core.filemanager.IFileManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ptonder
     */
     public JsonElement getFiles(Object listId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.method = "getFiles";
          gs_json_object_data.interfaceName = "core.filemanager.IFileManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ptonder
     */
     public void renameFileEntry(Object fileId, Object newName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.args.put("newName",new Gson().toJson(newName));
          gs_json_object_data.method = "renameFileEntry";
          gs_json_object_data.interfaceName = "core.filemanager.IFileManager";
          String result = transport.send(gs_json_object_data);
     }

}
