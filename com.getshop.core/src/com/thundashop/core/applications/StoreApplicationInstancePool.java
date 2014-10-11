/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.applications;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.ApplicationInstance;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class StoreApplicationInstancePool extends ManagerBase implements IStoreApplicationInstancePool{
	@Autowired
	private StoreApplicationPool storeApplicationPool;
	
	private Map<String, ApplicationInstance> applicationInstances;

	@Override
	public void dataFromDatabase(DataRetreived data) {
		applicationInstances = data.data.stream()
				.filter(o -> o.getClass() == ApplicationInstance.class)
				.map(o -> (ApplicationInstance)o)
				.collect(Collectors.toMap(o -> o.id, o -> o));
	}

	@Override
	public ApplicationInstance createNewInstance(String applicationId) {
		Application app = storeApplicationPool.getApplication(applicationId);
		
		if (app == null) {
			return null;
		}
		
		ApplicationInstance applicationInstance = new ApplicationInstance();
		applicationInstance.appSettingsId = app.id;
		saveObject(applicationInstance);
		
		applicationInstances.put(applicationInstance.id, applicationInstance);
		return applicationInstance.secureClone();
	}

	@Override
	public ApplicationInstance getApplicationInstance(String applicationInstanceId) {
		ApplicationInstance instance = applicationInstances.get(applicationInstanceId);
		
		if ( instance == null) {
			return null;
		}
		
		return instance.secureClone();
	}
	
}
