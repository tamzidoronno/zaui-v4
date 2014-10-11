/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.applications;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class StoreApplicationPool extends ManagerBase implements IStoreApplicationPool {

	@Autowired
	private GetShopApplicationPool getShopApplicationPool;
	
	private List<Application> allApplications;
	
	private Set<Application> activatedApplications = new HashSet();
	
	
	
	@PostConstruct
	public void load() {
		allApplications = getShopApplicationPool.getApplications();
		activatedApplications = allApplications.stream().filter( app -> app.defaultActivate).collect(Collectors.toSet());
	}

	@Override
	public void dataFromDatabase(DataRetreived data) {
		
	}
	
	@Override
	public List<Application> getApplications() {
		List<Application> availableApplications = getAvailableApplications();
		return availableApplications.stream()
				.filter( o -> activatedApplications.contains(o))
				.collect(Collectors.toList());
	}

	@Override
	public List<Application> getAvailableApplications() {
		List<Application> publicApplications = getAllPublicApplications();
		List<Application> nonePublicButIsAllowed = getApplicationsThatAreExplicitAllowed();
		publicApplications.addAll(nonePublicButIsAllowed);
		return publicApplications;
	}

	@Override
	public void activateApplication(String applicationId) {
		Application application = getAvailableApplications().stream().filter(app -> app.id.equals(applicationId)).findFirst().get();
		if (application != null) {
			activatedApplications.add(application);
		}
	}

	private List<Application> getApplicationsThatAreExplicitAllowed() {
		return allApplications.stream()
				.filter( o -> !o.isPublic)
				.filter( o -> o.allowedStoreIds.contains(storeId))
				.collect(Collectors.toList());
	}

	private List<Application> getAllPublicApplications() {
		return allApplications.stream()
				.filter(o -> o.isPublic)
				.collect(Collectors.toList());
	}

	@Override
	public List<Application> getAvailableThemeApplications() {
		return getAvailableApplications().stream()
				.filter(app -> app.type.equals(Application.Type.Theme))
				.collect(Collectors.toList());
	}

	@Override
	public Application getThemeApplication() {
		String id = getManagerSetting("selectedThemeApplication");
		
		if (id == null) {
			return getDefaultThemeApplication();
		}
		
		Application app = getApplication(id);
		if (app == null) {
			return getDefaultThemeApplication();
		}
		
		return app;
	}

	@Override
	public void setThemeApplication(String applicationId) {
		setManagerSetting("selectedThemeApplication", applicationId);
	}

	@Override
	public Application getApplication(String id) {
		return getApplications().stream()
				.filter(app -> app.id.equals(id))
				.findFirst()
				.get();
	}

	private Application getDefaultThemeApplication() {
		if (getAvailableThemeApplications().size() == 0) {
			throw new NullPointerException("There are no theme activated for this shop");
		}
		
		return getAvailableApplications().get(0);
	}
	
}
