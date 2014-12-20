/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.applications;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.appmanager.data.ApplicationModule;
import com.thundashop.core.appmanager.data.SavedApplicationSettings;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

    private Set<ApplicationModule> activatedModules = new HashSet();

    private Map<String, SavedApplicationSettings> settings = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        allApplications = getShopApplicationPool.getApplications()
                .stream()
                .filter(app -> app.allowedStoreIds.contains(storeId) || app.isPublic)
                .collect(Collectors.toList());

        activatedApplications = allApplications.stream()
                .filter(app -> app.defaultActivate)
                .collect(Collectors.toSet());

        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof SavedApplicationSettings) {
                SavedApplicationSettings set = (SavedApplicationSettings)dataCommon;
                settings.put(set.applicationId, set);
            }
        }
        addActivatedApplications();
        addActivatedModules();
    }

    @Override
    public List<Application> getApplications() {
        List<Application> finalizedList = new ArrayList();
        getApplicationsInternally().forEach(app -> finalizedList.add(finalizeApplication(app)));
        return finalizedList;
    }

    private List<Application> getApplicationsInternally() {
        List<Application> availableApplications = getAvailableApplicationsInternally();
        Set<Application> activatedApps = availableApplications.stream()
                .filter(o -> activatedApplications.contains(o))
                .filter(o -> !o.type.equals(Application.Type.Theme))
                .collect(Collectors.toSet());

        activatedApps.addAll(getAllDefaultActivatedApps());
        return new ArrayList(activatedApps);
    }
    
    private List<Application> getAvailableApplicationsInternally() {
        List<Application> publicApplications = getAllPublicApplications();
        List<Application> nonePublicButIsAllowed = getApplicationsThatAreExplicitAllowed();
        publicApplications.addAll(nonePublicButIsAllowed);
        return publicApplications;
    }
    
    @Override
    public List<Application> getAvailableApplications() {
        List<Application> finalizedList = new ArrayList();
        getAvailableApplicationsInternally().forEach(app -> finalizedList.add(finalizeApplication(app)));
        return finalizedList;
    }

    @Override
    public void activateApplication(String applicationId) {
        Application application = getAvailableApplicationsInternally().stream().filter(app -> app.id.equals(applicationId)).findFirst().get();
        if (application != null) {
            activatedApplications.add(application);
            setManagerSetting(application.id, "activated");
        }
    }

    private List<Application> getApplicationsThatAreExplicitAllowed() {
        return allApplications.stream()
                .filter(o -> !o.isPublic)
                .filter(o -> o.allowedStoreIds.contains(storeId))
                .collect(Collectors.toList());
    }

    private List<Application> getAllPublicApplications() {
        return allApplications.stream()
                .filter(o -> o.isPublic)
                .collect(Collectors.toList());
    }

    @Override
    public List<Application> getAvailableThemeApplications() {
        List<Application> retList = getAvailableApplicationsInternally().stream()
                .filter(app -> app.type.equals(Application.Type.Theme))
                .collect(Collectors.toList());

        List<Application> finalizedList = new ArrayList();
        retList.forEach(app -> finalizedList.add(finalizeApplication(app)));
        return finalizedList;
    }

    @Override
    public Application getThemeApplication() {
        String id = getManagerSetting("selectedThemeApplication");

        if (id == null) {
            return finalizeApplication(getDefaultThemeApplication());
        }

        Application app = getAvailableThemeApplications()
                .stream()
                .filter(a -> a.id.equals(id))
                .findFirst()
                .orElse(null);

        if (app == null) {
            return finalizeApplication(getDefaultThemeApplication());
        }

        return finalizeApplication(app);
    }

    @Override
    public void setThemeApplication(String applicationId) {
        setManagerSetting("selectedThemeApplication", applicationId);
    }

    @Override
    public Application getApplication(String id) {
        Application retApp = getApplicationsInternally().stream()
                .filter(app -> app.id.equals(id))
                .findFirst()
                .orElse(null);

        return finalizeApplication(retApp);
    }

    private Application getDefaultThemeApplication() {
        if (getAvailableThemeApplications().size() == 0) {
            throw new NullPointerException("There are no theme activated for this shop");
        }

        return getAvailableThemeApplications().get(0);
    }

    @Override
    public List<Application> getAvailableApplicationsThatIsNotActivated() {
        List<Application> apps = getAvailableApplicationsInternally()
                .stream()
                .filter(a -> !activatedApplications.contains(a))
                .collect(Collectors.toList());
        
        List<Application> finalizedList = new ArrayList();
        apps.forEach(app -> finalizedList.add(finalizeApplication(app)));
        return finalizedList;
    }

    @Override
    public List<ApplicationModule> getAllAvailableModules() {
        return getShopApplicationPool.getModules();
    }

    @Override
    public List<ApplicationModule> getActivatedModules() {
        return new ArrayList(activatedModules);
    }

    private void addActivatedModules() {
        getShopApplicationPool.getModules().stream()
                .filter(module -> getManagerSetting("module_actived_" + module.id) != null)
                .filter(module -> getManagerSetting("module_actived_" + module.id).equals("activated"))
                .forEach(module -> activatedModules.add(module));
    }

    private void addActivatedApplications() {
        allApplications.stream()
                .filter(app -> getManagerSetting(app.id) != null)
                .filter(app -> getManagerSetting(app.id).equals("activated"))
                .forEach(app -> activatedApplications.add(app));
    }

    @Override
    public void activateModule(String moduleId) {
        ApplicationModule foundModule = getAllAvailableModules().stream()
                .filter(m -> m.id.equals(moduleId))
                .findFirst()
                .orElse(null);

        if (foundModule != null) {
            setManagerSetting("module_actived_" + foundModule.id, "activated");
            activatedModules.add(foundModule);
        }
    }

    @Override
    public void deactivateApplication(String applicationId) {
        Application application = getAvailableApplicationsInternally().stream().filter(app -> app.id.equals(applicationId)).findFirst().get();
        if (application != null) {
            activatedApplications.add(application);
            setManagerSetting(application.id, "deactivated");
        }
    }

    private Collection<? extends Application> getAllDefaultActivatedApps() {
        Set<Application> applications = new HashSet();
        for (ApplicationModule module : getActivatedModules()) {
            allApplications.stream()
                    .filter(app -> app.moduleId != null)
                    .filter(app -> app.moduleId.equals(module.id))
                    .filter(app -> app.activeAppOnModuleActivation)
                    .forEach(app -> applications.add(finalizeApplication(app)));
        }

        return applications;
    }

    @Override
    public void setSetting(String applicationId, Setting inSetting) {
        Application application = getApplication(applicationId);

        if (application == null) {
            return;
        }

        SavedApplicationSettings setting = settings.get(applicationId);
        if (setting == null) {
            setting = new SavedApplicationSettings();
        }
        setting.applicationId = applicationId;
        setting.settings.put(inSetting.name, inSetting);
        settings.put(applicationId, setting);
        saveObject(setting);
    }

    private Application finalizeApplication(Application app) {
        
        if (app == null) {
            return null;
        }
        
        Application retApp = app.jsonClone();

        SavedApplicationSettings setting = settings.get(app.id);
        if (setting != null) {
            for (String settingKey : setting.settings.keySet()) {
                retApp.settings.put(settingKey, setting.settings.get(settingKey));
            }
        }

        return retApp;
    }

    @Override
    public List<Application> getShippingApplications() {
        List<Application> shipmentApplications = getApplicationsInternally().stream()
                .filter(app -> app.type.equals(Application.Type.Shipment))
                .collect(Collectors.toList());
        
        List<Application> finalizedList = new ArrayList();
        shipmentApplications.forEach(app -> finalizedList.add(finalizeApplication(app)));
        return finalizedList;
    }

}
