/**
 * The application manager keeps the settings for you applications, and keeps
 * track of the applications added / modified by you. The application manager
 * is important for the synchronization tool.
 */
package com.thundashop.core.appmanager;

import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.AvailableApplications;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

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
     * Fetch the settings for a given id.
     * @param id
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public ApplicationSettings getApplication(String id) throws ErrorException;
    
}
