/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.applications;

import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IStoreApplicationPool {
	/**
	 * Returns a list of all applications this store
	 * has activated.
	 * 
	 * @return 
	 */
	public List<Application> getApplications();
	
	/**
	 * Returns a list of all applications that are available
	 * for this store. This also includes applications that has
	 * not yet been activated by the administrator.
	 * 
	 * @return 
	 */
	public List<Application> getAvailableApplications();
	
	/**
	 * Activate an application.
	 * 
	 * @param applicationId 
	 */
	@Administrator
	public void activateApplication(String applicationId);
	
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
	 * Use this function to change or set the 
	 * theme application you wish to use.
	 * @param applicationId 
	 */
	public void setThemeApplication(String applicationId);
	
	/**
	 * Return an activated application by the given Id.
	 * 
	 * @param id
	 * @return 
	 */
	public Application getApplication(String id);
}
