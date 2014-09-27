/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.settingsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.appmanager.AppManager;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class SettingsManager extends ManagerBase implements ISettingsManager {

	@Autowired
	private AppManager appManager;

	public SettingsManager() {
	}
	
    @Override
    public List<ApplicationSettings> getDashBoardApplications() throws ErrorException {
        return appManager.getAllApplications()
			.applications
			.stream()
			.filter(app ->  app.hasDashBoard)
			.collect(Collectors.toCollection(ArrayList::new));
    }
    
}
