/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.applications;

import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.GetShopAdministrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IGetShopApplicationPool {
	/**
	 * Returns a list of all available applications.
	 * 
	 * @return 
	 */
	public List<Application> getApplications();
	
	/**
	 * Get an application by an given id.
	 * 
	 * @param applicationId
	 * @return 
	 */
	public Application get(String applicationId);
	
	/**
	 * Save an application
	 * @param application 
	 */
	@GetShopAdministrator
	public void saveApplication(Application application);
}