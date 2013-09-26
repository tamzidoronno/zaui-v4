package com.thundashop.core.listmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.listmanager.data.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * List manager is a manager that is handling your lists in GetShop.<br>
 * It has all the basic functionality, list ordering, etc.<br>
 * Lists are being used in categories, left menu, top menu, footer, product lists, etc.<br>
 * A list entry contains will automatically have page attached which, and can contain subentries.
 */
@GetShopApi
public interface IListManager {
    /**
     * Fetch a list from the system.
     * @param listId The id for the list to fetch
     * @return An object with all the entries included.
     */
    public List<Entry> getList(String listId) throws ErrorException;
    
    
    /**
     * Fetch a list of ids that current shop has.<br>
     * This will return a list with the ids for all lists created by this webshop.<br>
     * 
     * @return List of lists ids
     */
    public List<String> getLists();
    
    /**
     * This function flushes all entries in the list and set this as new entries instead.
     * @param listId The id of the list to be updated
     * @param entries All entries to be included in the list.
     * @throws ErrorException 
     */
    public void setEntries(String listId, ArrayList<Entry> entries) throws ErrorException;
    
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
    @Administrator
    public Entry addEntry(String listId, Entry entry, String parentPageId) throws ErrorException;
    
    /**
     * Delete an already existing entry from a list.
     * @param id The of the entry to delete.
     * @param id The id of the list to remove from.
     */
    @Administrator
    public void deleteEntry(String id, String listId) throws ErrorException;
    
    /**
     * Create a list id, this will create a a new list for you.
     * @return The id for a list to use
     */
    @Administrator
    public String createListId() throws ErrorException;
    
   /**
    * Order a entry on the list.
    * @param id The id for the entry to move / reorder.
    * @param after Put it after a given entry (this will be the id for the given entry). To move the entry to the top leave this empty.
    * @param parent If you want to move the entry into a given entry, then specify the id to the entry here. Leave empty to move to top.
    * @return A message that the given entry has been updated.
    */
    @Administrator
    public Entry orderEntry(String id, String after, String parentId) throws ErrorException;
    
    
    /**
     * Update an already existing entry
     * @param entry The entry to update.
     * @return A message that the message has been updated.
     */
    @Administrator
    public void updateEntry(Entry entry) throws ErrorException;
    
    /**
     * Fetch a single menu entry.
     * @param id The id for the entry to fetch.
     * @return 
     */
    public Entry getListEntry(String id) throws ErrorException;
    
    /**
     * Translate all antries found in a given list of entry ids.
     * @param entryIds A list of entries id to translate.
     * @return A list with human readable strings which translate the error mapped to the id.
     * @throws ErrorException 
     */
    public HashMap<String, String> translateEntries(List<String> entryIds) throws ErrorException;
    
    /**
     * If you would like to combine more lists to a current list, you can do it by using this call.<br>
     * 
     * @param toListId The current list to be appended on.
     * @param newListId The list which you would like to combine.
     * @throws ErrorException 
     */
    public void combineList(String toListId, String newListId) throws ErrorException;
    
    /**
     * Does the exact opposite as combineList(...), removes a list from a combined list.
     * @param fromListId The id of the list to be removed from.
     * @param toRemoveId The id of the list to remove.
     * @throws ErrorException 
     */
    public void unCombineList(String fromListId, String toRemoveId) throws ErrorException;
    
    /**
     * Fetch a list of all lists combined with a given list.
     * @param listId
     * @throws ErrorException 
     */
    public List<String> getCombinedLists(String listId) throws ErrorException;

    
    /**
     * Remove all list entries for a specified list
     * 
     * @param listId
     * @throws ErrorException 
     */
    @Administrator
    public void clearList(String listId) throws ErrorException;
    
    public String getPageIdByName(String name);
}
