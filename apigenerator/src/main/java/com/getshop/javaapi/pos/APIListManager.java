package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIListManager {

      public Communicator transport;

      public APIListManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Add a new entry to a given list.<br>
     * For most components like leftmenu, topmenu, footer, category displayer the list id is the same as the application id.<br>
     * When creating an entry, a page will automatically be created and attached to this entry if not exists.<br>
     *
     * @param listId The id for the list to add the entry to, if list does not exists, it will be created automatically.
     * @param entry The entry to append to the list.
     * @param parentPageId See the pagemanager for more information about the page id, when the page to this entry is created set this id as the parent.
     * @return The entry which has been updated.
     */
     public JsonElement addEntry(Object listId, Object entry, Object parentPageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.args.put("entry",new Gson().toJson(entry));
          gs_json_object_data.args.put("parentPageId",new Gson().toJson(parentPageId));
          gs_json_object_data.method = "addEntry";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Add a new entry to a given list.<br>
     * For most components like leftmenu, topmenu, footer, category displayer the list id is the same as the application id.<br>
     * When creating an entry, a page will automatically be created and attached to this entry if not exists.<br>
     *
     * @param listId The id for the list to add the entry to, if list does not exists, it will be created automatically.
     * @param entry The entry to append to the list.
     * @param parentPageId See the pagemanager for more information about the page id, when the page to this entry is created set this id as the parent.
     * @return The entry which has been updated.
     */
     public JsonElement addUnsecureEntry(Object listId, Object entry)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.args.put("entry",new Gson().toJson(entry));
          gs_json_object_data.method = "addUnsecureEntry";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create new list for a given id
     *
     * @param listName
     * @throws ErrorException
     */
     public void askConfirmationOnEntry(Object entryId, Object text)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("entryId",new Gson().toJson(entryId));
          gs_json_object_data.args.put("text",new Gson().toJson(text));
          gs_json_object_data.method = "askConfirmationOnEntry";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Remove all list entries for a specified list
     *
     * @param listId
     * @throws ErrorException
     */
     public void clearList(Object listId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.method = "clearList";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If you would like to combine more lists to a current list, you can do it by using this call.<br>
     *
     * @param toListId The current list to be appended on.
     * @param newListId The list which you would like to combine.
     * @throws ErrorException
     */
     public void combineList(Object toListId, Object newListId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("toListId",new Gson().toJson(toListId));
          gs_json_object_data.args.put("newListId",new Gson().toJson(newListId));
          gs_json_object_data.method = "combineList";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add a new entry to a given list.<br>
     * For most components like leftmenu, topmenu, footer, category displayer the list id is the same as the application id.<br>
     * When creating an entry, a page will automatically be created and attached to this entry if not exists.<br>
     *
     * @param listId The id for the list to add the entry to, if list does not exists, it will be created automatically.
     * @param entry The entry to append to the list.
     * @param parentPageId See the pagemanager for more information about the page id, when the page to this entry is created set this id as the parent.
     * @return The entry which has been updated.
     */
     public void confirmEntry(Object entryId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("entryId",new Gson().toJson(entryId));
          gs_json_object_data.method = "confirmEntry";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create a list id, this will create a a new list for you.
     * @return The id for a list to use
     */
     public JsonElement createListId()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "createListId";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create new list for a given id
     *
     * @param listName
     * @throws ErrorException
     */
     public void createMenuList(Object menuApplicationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("menuApplicationId",new Gson().toJson(menuApplicationId));
          gs_json_object_data.method = "createMenuList";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Delete an already existing entry from a list.
     * @param id The of the entry to delete.
     * @param id The id of the list to remove from.
     */
     public void deleteEntry(Object id, Object listId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.method = "deleteEntry";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This function flushes all entries in the list and set this as new entries instead.
     * @param listId The id of the list to be updated
     * @param entries All entries to be included in the list.
     * @throws ErrorException
     */
     public void deleteMenu(Object appId, Object listId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("appId",new Gson().toJson(appId));
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.method = "deleteMenu";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns the entrylist of a given id.
     *
     * type = MENU
     * type = PRODUCT
     *
     * @return
     */
     public JsonElement getAllListsByType(Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "getAllListsByType";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a list of all lists combined with a given list.
     * @param listId
     * @throws ErrorException
     */
     public JsonElement getCombinedLists(Object listId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.method = "getCombinedLists";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a single menu entry.
     * @param id The id for the entry to fetch.
     * @return
     */
     public JsonElement getEntryByPageId(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getEntryByPageId";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create new list for a given id
     *
     * @param listName
     * @throws ErrorException
     */
     public JsonElement getJSTreeNode(Object nodeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("nodeId",new Gson().toJson(nodeId));
          gs_json_object_data.method = "getJSTreeNode";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create new list for a given id
     *
     * @param listName
     * @throws ErrorException
     */
     public JsonElement getJsTree(Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "getJsTree";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a list from the system.
     * @param listId The id for the list to fetch
     * @return An object with all the entries included.
     */
     public JsonElement getList(Object listId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.method = "getList";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a single menu entry.
     * @param id The id for the entry to fetch.
     * @return
     */
     public JsonElement getListEntry(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getListEntry";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a list of ids that current shop has.<br>
     * This will return a list with the ids for all lists created by this webshop.<br>
     *
     * @return List of lists ids
     */
     public JsonElement getLists()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getLists";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create new list for a given id
     *
     * @param listName
     * @throws ErrorException
     */
     public JsonElement getMenues(Object applicationInstanceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationInstanceId",new Gson().toJson(applicationInstanceId));
          gs_json_object_data.method = "getMenues";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Remove all list entries for a specified list
     *
     * @param listId
     * @throws ErrorException
     */
     public JsonElement getPageIdByName(Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "getPageIdByName";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Order a entry on the list.
     * @param id The id for the entry to move / reorder.
     * @param after Put it after a given entry (this will be the id for the given entry). To move the entry to the top leave this empty.
     * @param parent If you want to move the entry into a given entry, then specify the id to the entry here. Leave empty to move to top.
     * @return A message that the given entry has been updated.
     */
     public JsonElement orderEntry(Object id, Object after, Object parentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("after",new Gson().toJson(after));
          gs_json_object_data.args.put("parentId",new Gson().toJson(parentId));
          gs_json_object_data.method = "orderEntry";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create new list for a given id
     *
     * @param listName
     * @throws ErrorException
     */
     public void saveJsTree(Object name, Object list)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.args.put("list",new Gson().toJson(list));
          gs_json_object_data.method = "saveJsTree";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This function flushes all entries in the list and set this as new entries instead.
     * @param listId The id of the list to be updated
     * @param entries All entries to be included in the list.
     * @throws ErrorException
     */
     public void saveMenu(Object appId, Object listId, Object entries, Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("appId",new Gson().toJson(appId));
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.args.put("entries",new Gson().toJson(entries));
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "saveMenu";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This function flushes all entries in the list and set this as new entries instead.
     * @param listId The id of the list to be updated
     * @param entries All entries to be included in the list.
     * @throws ErrorException
     */
     public void setEntries(Object listId, Object entries)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("listId",new Gson().toJson(listId));
          gs_json_object_data.args.put("entries",new Gson().toJson(entries));
          gs_json_object_data.method = "setEntries";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Translate all antries found in a given list of entry ids.
     * @param entryIds A list of entries id to translate.
     * @return A list with human readable strings which translate the error mapped to the id.
     * @throws ErrorException
     */
     public JsonElement translateEntries(Object entryIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("entryIds",new Gson().toJson(entryIds));
          gs_json_object_data.method = "translateEntries";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Does the exact opposite as combineList(...), removes a list from a combined list.
     * @param fromListId The id of the list to be removed from.
     * @param toRemoveId The id of the list to remove.
     * @throws ErrorException
     */
     public void unCombineList(Object fromListId, Object toRemoveId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("fromListId",new Gson().toJson(fromListId));
          gs_json_object_data.args.put("toRemoveId",new Gson().toJson(toRemoveId));
          gs_json_object_data.method = "unCombineList";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Update an already existing entry
     * @param entry The entry to update.
     * @return A message that the message has been updated.
     */
     public void updateEntry(Object entry)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("entry",new Gson().toJson(entry));
          gs_json_object_data.method = "updateEntry";
          gs_json_object_data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(gs_json_object_data);
     }

}
