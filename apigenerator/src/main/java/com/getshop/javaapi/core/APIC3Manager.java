package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIC3Manager {

      public Communicator transport;

      public APIC3Manager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void addForskningsUserPeriode(Object periode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("periode",new Gson().toJson(periode));
          gs_json_object_data.method = "addForskningsUserPeriode";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addHour(Object hour)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("hour",new Gson().toJson(hour));
          gs_json_object_data.method = "addHour";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addTimeRate(Object name, Object rate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.args.put("rate",new Gson().toJson(rate));
          gs_json_object_data.method = "addTimeRate";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addUserProjectPeriode(Object projectPeriode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("projectPeriode",new Gson().toJson(projectPeriode));
          gs_json_object_data.method = "addUserProjectPeriode";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement allowedFixedHourCosts(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "allowedFixedHourCosts";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement allowedNfrHour(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "allowedNfrHour";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement allowedNfrHourCurrentUser()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "allowedNfrHourCurrentUser";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement allowedNfrOtherCost(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "allowedNfrOtherCost";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement allowedNfrOtherCostCurrentUser()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "allowedNfrOtherCostCurrentUser";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void assignProjectToCompany(Object companyId, Object projectId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.args.put("projectId",new Gson().toJson(projectId));
          gs_json_object_data.method = "assignProjectToCompany";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement calculateSum(Object periodeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("periodeId",new Gson().toJson(periodeId));
          gs_json_object_data.method = "calculateSum";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement canAdd(Object hour)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("hour",new Gson().toJson(hour));
          gs_json_object_data.method = "canAdd";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void deleteC3Periode(Object periodeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("periodeId",new Gson().toJson(periodeId));
          gs_json_object_data.method = "deleteC3Periode";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteForskningsUserPeriode(Object periodeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("periodeId",new Gson().toJson(periodeId));
          gs_json_object_data.method = "deleteForskningsUserPeriode";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteProject(Object projectId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("projectId",new Gson().toJson(projectId));
          gs_json_object_data.method = "deleteProject";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteProjectCost(Object projectCostId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("projectCostId",new Gson().toJson(projectCostId));
          gs_json_object_data.method = "deleteProjectCost";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteTimeRate(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteTimeRate";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteWorkPackage(Object workPackageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("workPackageId",new Gson().toJson(workPackageId));
          gs_json_object_data.method = "deleteWorkPackage";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAcceListForUser(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getAcceListForUser";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAccessList()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAccessList";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAccessListByProjectId(Object projectId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("projectId",new Gson().toJson(projectId));
          gs_json_object_data.method = "getAccessListByProjectId";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getActivePeriode()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getActivePeriode";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllProjectsConnectedToCompany(Object compnayId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("compnayId",new Gson().toJson(compnayId));
          gs_json_object_data.method = "getAllProjectsConnectedToCompany";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllProjectsConnectedToWorkPackage(Object wpId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("wpId",new Gson().toJson(wpId));
          gs_json_object_data.method = "getAllProjectsConnectedToWorkPackage";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getBase64ESAExcelReport(Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getBase64ESAExcelReport";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getBase64SFIExcelReport(Object companyId, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getBase64SFIExcelReport";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getBase64SFIExcelReportTotal(Object companyId, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getBase64SFIExcelReportTotal";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getCurrentForskningsPeriode()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCurrentForskningsPeriode";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getForskningsPeriodesForUser(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getForskningsPeriodesForUser";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getGroupInformation(Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "getGroupInformation";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getHourById(Object hourId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("hourId",new Gson().toJson(hourId));
          gs_json_object_data.method = "getHourById";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getOtherCost(Object otherCostId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("otherCostId",new Gson().toJson(otherCostId));
          gs_json_object_data.method = "getOtherCost";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPeriodes()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getPeriodes";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPeriodesForProject(Object projectId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("projectId",new Gson().toJson(projectId));
          gs_json_object_data.method = "getPeriodesForProject";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getProject(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getProject";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getProjectCostsForAllUsersInCompany(Object projectId, Object from, Object to)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("projectId",new Gson().toJson(projectId));
          gs_json_object_data.args.put("from",new Gson().toJson(from));
          gs_json_object_data.args.put("to",new Gson().toJson(to));
          gs_json_object_data.method = "getProjectCostsForAllUsersInCompany";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getProjectCostsForCurrentUser(Object projectId, Object from, Object to)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("projectId",new Gson().toJson(projectId));
          gs_json_object_data.args.put("from",new Gson().toJson(from));
          gs_json_object_data.args.put("to",new Gson().toJson(to));
          gs_json_object_data.method = "getProjectCostsForCurrentUser";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getProjects()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getProjects";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getReportForUserProject(Object userId, Object projectId, Object start, Object end, Object forWorkPackageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("projectId",new Gson().toJson(projectId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("forWorkPackageId",new Gson().toJson(forWorkPackageId));
          gs_json_object_data.method = "getReportForUserProject";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getRoundSum(Object year)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("year",new Gson().toJson(year));
          gs_json_object_data.method = "getRoundSum";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTimeRate(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getTimeRate";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTimeRates()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getTimeRates";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getUserProjectPeriodeById(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getUserProjectPeriodeById";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getWorkPackage(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getWorkPackage";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getWorkPackages()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getWorkPackages";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void removeCompanyAccess(Object projectId, Object companyId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("projectId",new Gson().toJson(projectId));
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.method = "removeCompanyAccess";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void removeContract(Object companyId, Object projectId, Object workPackageId, Object contractId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.args.put("projectId",new Gson().toJson(projectId));
          gs_json_object_data.args.put("workPackageId",new Gson().toJson(workPackageId));
          gs_json_object_data.args.put("contractId",new Gson().toJson(contractId));
          gs_json_object_data.method = "removeContract";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveGroupInfo(Object groupId, Object type, Object value)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.args.put("value",new Gson().toJson(value));
          gs_json_object_data.method = "saveGroupInfo";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement saveOtherCosts(Object otherCost)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("otherCost",new Gson().toJson(otherCost));
          gs_json_object_data.method = "saveOtherCosts";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void savePeriode(Object periode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("periode",new Gson().toJson(periode));
          gs_json_object_data.method = "savePeriode";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement saveProject(Object project)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("project",new Gson().toJson(project));
          gs_json_object_data.method = "saveProject";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void saveRate(Object rate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("rate",new Gson().toJson(rate));
          gs_json_object_data.method = "saveRate";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement saveWorkPackage(Object workPackage)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("workPackage",new Gson().toJson(workPackage));
          gs_json_object_data.method = "saveWorkPackage";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement search(Object searchText)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("searchText",new Gson().toJson(searchText));
          gs_json_object_data.method = "search";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void setActivePeriode(Object periodeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("periodeId",new Gson().toJson(periodeId));
          gs_json_object_data.method = "setActivePeriode";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setC3RoundSum(Object year, Object sum)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("year",new Gson().toJson(year));
          gs_json_object_data.args.put("sum",new Gson().toJson(sum));
          gs_json_object_data.method = "setC3RoundSum";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setNfrAccess(Object access)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("access",new Gson().toJson(access));
          gs_json_object_data.method = "setNfrAccess";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setProjectAccess(Object companyId, Object projectId, Object workPackageId, Object value)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.args.put("projectId",new Gson().toJson(projectId));
          gs_json_object_data.args.put("workPackageId",new Gson().toJson(workPackageId));
          gs_json_object_data.args.put("value",new Gson().toJson(value));
          gs_json_object_data.method = "setProjectAccess";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setProjectCust(Object companyId, Object projectId, Object workPackageId, Object start, Object end, Object price, Object contractId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.args.put("projectId",new Gson().toJson(projectId));
          gs_json_object_data.args.put("workPackageId",new Gson().toJson(workPackageId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("price",new Gson().toJson(price));
          gs_json_object_data.args.put("contractId",new Gson().toJson(contractId));
          gs_json_object_data.method = "setProjectCust";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setRateToUser(Object userId, Object rateId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("rateId",new Gson().toJson(rateId));
          gs_json_object_data.method = "setRateToUser";
          gs_json_object_data.interfaceName = "core.c3.IC3Manager";
          String result = transport.send(gs_json_object_data);
     }

}
