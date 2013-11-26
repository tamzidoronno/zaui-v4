package com.thundashop.api.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
import java.util.ArrayList;
import java.util.List;
import com.thundashop.core.listmanager.data.Entry;
import java.util.HashMap;
import com.thundashop.core.listmanager.data.EntryList;

public class APIListManager {

      public Transporter transport;

      public APIListManager(Transporter transport){
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
     * @return Entry
     */

     public Entry addEntry(String listId, Entry entry, String parentPageId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("listId",new Gson().toJson(listId));
          data.args.put("entry",new Gson().toJson(entry));
          data.args.put("parentPageId",new Gson().toJson(parentPageId));
          data.method = "addEntry";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Entry>() {}.getType();
          Entry object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Remove all list entries for a specified list
     * 
     * @param listId
     * @throws ErrorException 
     */

     public void clearList(String listId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("listId",new Gson().toJson(listId));
          data.method = "clearList";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
     }

     /**
     * If you would like to combine more lists to a current list, you can do it by using this call.<br>
     * 
     * @param toListId The current list to be appended on.
     * @param newListId The list which you would like to combine.
     * @throws ErrorException 
     */

     public void combineList(String toListId, String newListId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("toListId",new Gson().toJson(toListId));
          data.args.put("newListId",new Gson().toJson(newListId));
          data.method = "combineList";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
     }

     /**
     * Create a list id, this will create a a new list for you.
     * @return String
     */

     public String createListId()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "createListId";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<String>() {}.getType();
          String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Delete an already existing entry from a list.
     * @param id The of the entry to delete.
     * @param id The id of the list to remove from.
     */

     public void deleteEntry(String id, String listId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.args.put("listId",new Gson().toJson(listId));
          data.method = "deleteEntry";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
     }

     /**
     * Returns the entrylist of a given id.
     * 
     * type = MENU 
     * type = PRODUCT
     * 
     * @return List<EntryList>
     */

     public List<EntryList> getAllListsByType(String type)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("type",new Gson().toJson(type));
          data.method = "getAllListsByType";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<EntryList>>() {}.getType();
          List<EntryList> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch a list of all lists combined with a given list.
     * @param listId
     * @throws ErrorException 
     */

     public List<String> getCombinedLists(String listId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("listId",new Gson().toJson(listId));
          data.method = "getCombinedLists";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<String>>() {}.getType();
          List<String> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch a list from the system.
     * @param listId The id for the list to fetch
     * @return List<Entry>
     */

     public List<Entry> getList(String listId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("listId",new Gson().toJson(listId));
          data.method = "getList";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<Entry>>() {}.getType();
          List<Entry> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch a single menu entry.
     * @param id The id for the entry to fetch.
     * @return Entry
     */

     public Entry getListEntry(String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getListEntry";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Entry>() {}.getType();
          Entry object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch a list of ids that current shop has.<br>
     * This will return a list with the ids for all lists created by this webshop.<br>
     * 
     * @return List<String>
     */

     public List<String> getLists()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getLists";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<String>>() {}.getType();
          List<String> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Remove all list entries for a specified list
     * 
     * @param listId
     * @throws ErrorException 
     */

     public String getPageIdByName(String name)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("name",new Gson().toJson(name));
          data.method = "getPageIdByName";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<String>() {}.getType();
          String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
    * Order a entry on the list.
    * @param id The id for the entry to move / reorder.
    * @param after Put it after a given entry (this will be the id for the given entry). To move the entry to the top leave this empty.
    * @param parent If you want to move the entry into a given entry, then specify the id to the entry here. Leave empty to move to top.
     * @return Entry
    */

     public Entry orderEntry(String id, String after, String parentId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.args.put("after",new Gson().toJson(after));
          data.args.put("parentId",new Gson().toJson(parentId));
          data.method = "orderEntry";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Entry>() {}.getType();
          Entry object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * This function flushes all entries in the list and set this as new entries instead.
     * @param listId The id of the list to be updated
     * @param entries All entries to be included in the list.
     * @throws ErrorException 
     */

     public void setEntries(String listId, ArrayList<Entry> entries)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("listId",new Gson().toJson(listId));
          data.args.put("entries",new Gson().toJson(entries));
          data.method = "setEntries";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
     }

     /**
     * Translate all antries found in a given list of entry ids.
     * @param entryIds A list of entries id to translate.
     * @return HashMap<String,String>
     * @throws ErrorException 
     */

     public HashMap<String,String> translateEntries(List<String> entryIds)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("entryIds",new Gson().toJson(entryIds));
          data.method = "translateEntries";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<HashMap<String,String>>() {}.getType();
          HashMap<String,String> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Does the exact opposite as combineList(...), removes a list from a combined list.
     * @param fromListId The id of the list to be removed from.
     * @param toRemoveId The id of the list to remove.
     * @throws ErrorException 
     */

     public void unCombineList(String fromListId, String toRemoveId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("fromListId",new Gson().toJson(fromListId));
          data.args.put("toRemoveId",new Gson().toJson(toRemoveId));
          data.method = "unCombineList";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
     }

     /**
     * Update an already existing entry
     * @param entry The entry to update.
     * @return void
     */

     public void updateEntry(Entry entry)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("entry",new Gson().toJson(entry));
          data.method = "updateEntry";
          data.interfaceName = "core.listmanager.IListManager";
          String result = transport.send(data);
     }

}
