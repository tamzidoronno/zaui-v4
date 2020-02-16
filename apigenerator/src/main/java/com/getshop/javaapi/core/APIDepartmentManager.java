package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIDepartmentManager {

      public Communicator transport;

      public APIDepartmentManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void createDepartment(Object departmentName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("departmentName",new Gson().toJson(departmentName));
          gs_json_object_data.method = "createDepartment";
          gs_json_object_data.interfaceName = "core.department.IDepartmentManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteDepartment(Object departmentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("departmentId",new Gson().toJson(departmentId));
          gs_json_object_data.method = "deleteDepartment";
          gs_json_object_data.interfaceName = "core.department.IDepartmentManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllDepartments()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllDepartments";
          gs_json_object_data.interfaceName = "core.department.IDepartmentManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getDepartment(Object departmentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("departmentId",new Gson().toJson(departmentId));
          gs_json_object_data.method = "getDepartment";
          gs_json_object_data.interfaceName = "core.department.IDepartmentManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void saveDepartment(Object department)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("department",new Gson().toJson(department));
          gs_json_object_data.method = "saveDepartment";
          gs_json_object_data.interfaceName = "core.department.IDepartmentManager";
          String result = transport.send(gs_json_object_data);
     }

}
