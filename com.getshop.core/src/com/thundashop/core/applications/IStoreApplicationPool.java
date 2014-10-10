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
}
