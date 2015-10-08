/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.applications;

import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.appmanager.data.ApplicationModule;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.Setting;
import com.thundashop.core.storemanager.data.SettingsRow;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IStoreApplicationPool {

    /**
     * Returns a list of all applications this store has activated.
     *
     * @return
     */
    public List<Application> getApplications();

    public boolean isActivated(String appId);
    
    /**
     * Returns a list of all applications that are available for this store.
     * This also includes applications that has not yet been activated by the
     * administrator.
     *
     * @return
     */
    public List<Application> getAvailableApplications();

    /**
     * This is a filtered list of the getAvailableApplications function.
     *
     * @return
     */
    public List<Application> getAvailableApplicationsThatIsNotActivated();

    /**
     * Activate an application.
     *
     * @param applicationId
     */
    @Administrator
    public void activateApplication(String applicationId);

    /**
     * Activate an application.
     *
     * @param applicationId
     */
    @Administrator
    public HashMap<String, List<SettingsRow>> getPaymentSettingsApplication();

    /**
     * Returns a list of all available theme applications.
     *
     * @return
     */
    public List<Application> getAvailableThemeApplications();

    /**
     * Return the themeapplication that is currently set.
     *
     * @return
     */
    public Application getThemeApplication();

    /**
     * Use this function to change or set the theme application you wish to use.
     *
     * @param applicationId
     */
    @Administrator
    public void setThemeApplication(String applicationId);

    /**
     * Return an activated application by the given Id.
     *
     * @param id
     * @return
     */
    public Application getApplication(String id);

    /**
     * Return a list of all applucation modules available
     * 
     * @return 
     */
    public List<ApplicationModule> getAllAvailableModules();
    
    /**
     * Returns a list of all available applications.
     * 
     * @return 
     */
    public List<ApplicationModule> getActivatedModules();
    
    /**
     * Actiave a module by a given module id.
     * 
     * @param module 
     */
    @Administrator
    public void activateModule(String module);
    
    @Administrator
    public void deactivateApplication(String applicationId);
    
    @Administrator
    public void setSetting(String applicationId, Setting settings);
    
    /**
     * Returns shipment applications.
     * 
     * @return 
     */
    public List<Application> getShippingApplications();
    
    public List<Application> getActivatedPaymentApplications();
}
