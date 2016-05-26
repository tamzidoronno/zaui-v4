package com.thundashop.core.storemanager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.ordermanager.data.Payment;
import com.thundashop.core.storemanager.data.KeyData;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.storemanager.data.StoreConfiguration;
import java.util.List;

/**
 * Aaah, the StoreManager.<br>
 * The core of your store. It handles the more common settings for your store.<br>
 * The storemanager is the god of your webshop, without it, it is useless!
 */
@GetShopApi
public interface IStoreManager {
    
    /**
     * Initializing this api. This will identify the webshop and will act as the root for everything in this api.
     * @param initSessionId The session id to identify to this user.
     * @return
     * @throws ErrorException 
     */
    public Store initializeStore(String webAddress, String initSessionId) throws ErrorException;
    
    /**
     * Initializing this api. This will identify the webshop and will act as the root for everything in this api.
     * @param initSessionId The session id to identify to this user.
     * @return
     * @throws ErrorException 
     */
    public Store initializeStoreByStoreId(String storeId, String initSessionId) throws ErrorException;
    
    /**
     * Save whatever data you want to the store.
     * @param key The key to save to
     * @param value The value to save
     * @param secure Secure? Need to be administrator to read it?
     */
    public void saveKey(String key, String value, boolean secure);
    public void removeKey(String key);
    public String getKey(String key);
    public String getKeySecure(String key, String password);
    
    
    /**
     * A user can set a different language for its session.
     * @param id
     * @throws ErrorException 
     */
    public void setSessionLanguage(String id) throws ErrorException;
    
    /**
     * Check if a web shop address has already been taken.
     * @param address The address to check for.
     * @throws ErrorException 
     */
    public boolean isAddressTaken(String address) throws ErrorException;
    
    /**
     * Get the store added to this session.
     * @return
     * @throws ErrorException 
     */
    public Store getMyStore() throws ErrorException;
    
    /**
     * Fetch the store id identified to this user.
     * @return The store id
     * @throws ErrorException 
     */
    public String getStoreId() throws ErrorException;
    
    /**
     * Update the current store with new configuration data.
     * @param config The configuration data to update.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public Store saveStore(StoreConfiguration config) throws ErrorException;
    
    /**
     * Set a new domain name to this store / webshop
     * @param domainName The domain name to identify this shop with.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public Store setPrimaryDomainName(String domainName) throws ErrorException;
    
    /**
     * Remove an already added domain name.
     * @param domainName The domain name to remove.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public Store removeDomainName(String domainName) throws ErrorException;
    
    /**
     * Create a new store / webshop with a given name.
     * @param hostname The hostname to the webshop.
     * @param email The email to identify the first user with,
     * @param password The password to logon the first user added to this webshop.
     * @param notify Notify the web shop owner by email about this new store.
     * @return 
     * @throws ErrorException 
     */
    public Store createStore(String hostname, String email, String password, boolean notify) throws ErrorException;
    
    public Store autoCreateStore(String hostname) throws ErrorException;
    
    /**
     * This will set the readintroduction variable in the Store object to true.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public Store setIntroductionRead() throws ErrorException;
    
    /**
     * Enable support to send sms for this webshop.
     * This option is not free since there is a cost for each sms sent.
     * @param toggle true or false depending on if this webshop should have access to sms.
     * @param password A password given by getshop to toggle this option.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public Store enableSMSAccess(boolean toggle, String password) throws ErrorException;
    
    /**
     * Enable extended support for this webshop.
     * Extended mode is a more advanced version of the ui where there is no limitation to what can be created / made.
     * @param toggle True or false depending if this webshop should have access to the extended mode.
     * @param password A password given by getshop to toggle this option.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public Store enableExtendedMode(boolean toggle, String password) throws ErrorException;
    
    @Administrator
    public void delete() throws ErrorException;
 
    
    /**
     * Setting this store to be a template or not.
     * 
     * @param storeId
     * @param isTemplate 
     */
    @Administrator
    public void setIsTemplate(String storeId, boolean isTemplate);
    
    /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException 
     */
    public int generateStoreId() throws ErrorException;
    
    public void setImageIdToFavicon(String id); 

    public boolean isProductMode() throws ErrorException;    
    
    @Administrator
    public void syncData(String environment, String username, String password) throws ErrorException;
    
    @Administrator
    public void receiveSyncData(byte[] json) throws ErrorException;
    
    public List<String> getAllEnvironments();
    
    public List<String> getMultiLevelNames() throws ErrorException;
    
    @Administrator
    public void toggleIgnoreBookingErrors(String password);
    
    @Administrator
    public void setStoreIdentifier(String identifier);
}