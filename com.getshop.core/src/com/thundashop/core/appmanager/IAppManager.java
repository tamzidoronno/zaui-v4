/**
 * The application manager keeps the settings for you applications, and keeps
 * track of the applications added / modified by you. The application manager
 * is important for the synchronization tool.
 */
package com.thundashop.core.appmanager;

import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.ApplicationSubscription;
import com.thundashop.core.appmanager.data.AvailableApplications;
import com.thundashop.core.appmanager.data.ApplicationSynchronization;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import java.util.List;
import java.util.Map;

/**
 *
 * @author boggi
 */
@GetShopApi
public interface IAppManager {
    
    /**
     * Fetch all the applications connected to you.
     * @return 
     */
    public AvailableApplications getAllApplications() throws ErrorException;
    
    /**
     * Create a new application.
     * @param appName The name of the application
     * @return 
     */
    @Administrator
    public ApplicationSettings createApplication(String appName) throws ErrorException;
    
    /**
     * Save applications
     * @param settings 
     */
    @Administrator
    public void saveApplication(ApplicationSettings settings) throws ErrorException;

    /**
     * Delete an application owned by you.
     * @param identificationId
     * @throws ErrorException 
     */
    @Administrator
    public void deleteApplication(String id) throws ErrorException;
    
    /**
     * Get all the applications added to this store.
     * @param includeAppSettings Do you need the application settings object or not?
     * @throws ErrorException 
     */
    @Administrator
    public Map<String, ApplicationSubscription> getAllApplicationSubscriptions(boolean includeAppSettings) throws ErrorException;
    
    /**
     * Fetch the settings for a given id.
     * @param id
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public ApplicationSettings getApplication(String id) throws ErrorException;
    
    /**
     * Notify the synchronization server to synchronize this application for the logged on user.
     * @param id
     * @throws ErrorException 
     */
    @Administrator
    public void setSyncApplication(String id) throws ErrorException;
    
    /**
     * Fetch all application that has been marked for synchronization.
     * When this method is called all objects related to this will unqueued.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<ApplicationSynchronization> getSyncApplications() throws ErrorException;
    
    /**
     * Check if the synchronization client is connected or not.
     * @return boolean
     * @throws ErrorException 
     */
    @Administrator
    public boolean isSyncToolConnected() throws ErrorException;
    
    /**
     * Get a list of all applicationsettings that is in 
     * use for this webopage.
     * 
     * @return 
     */
    public List<ApplicationSettings> getApplicationSettingsUsedByWebPage() throws ErrorException;
	
	/**
	 * Returns an application setting for the given id.
	 * 
	 * @param appSettingsId
	 * @return
	 * @throws ErrorException 
	 */
	@Editor
	public ApplicationSettings getApplicationSettings(String appSettingsId) throws ErrorException;
    
}
