/*
 * This is the main pool for applications. All available applications are available from here.
 */
package com.thundashop.core.applications;

import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.appmanager.data.ApplicationModule;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class GetShopApplicationPool extends ManagerBase implements IGetShopApplicationPool {
    private List<GetShopApplicationsChanged> changeListeners = new ArrayList();
    
    @Autowired
    private ApplicationModulePool modulePool;
    
    @Autowired
    private Database database;

    private Map<String, Application> applications;

    @PostConstruct
    public void loadData() {
        Stream<DataCommon> dataStream = database.getAll("ApplicationPool", "all");
        applications = dataStream.map(o -> (Application) o).collect(Collectors.toMap(o -> o.id, o -> o));
    }

    @Override
    public List<Application> getApplications() {
        return new ArrayList(finalizeApplications().values());
    }

    @Override
    public Application get(String applicationId) {
        return finalizeApplications().get(applicationId);
    }

    @Override
    public void saveApplication(Application application) {
        applications.put(application.id, application);
        databaseSave(application);
        finalizeApplications();
        triggerChangedEvent();
    }
    
    private void databaseSave(DataCommon application) {
        Credentials cred = new Credentials();
        cred.manangerName = "ApplicationPool";
        cred.password = "asdfadsf";
        cred.storeid = "all";
        database.save(application, cred);
    }
    
    private void databaseDelete(DataCommon application) {
        Credentials cred = new Credentials();
        cred.manangerName = "ApplicationPool";
        cred.password = "asdfadsf";
        cred.storeid = "all";
        database.delete(application, cred);
    }
    
    private void triggerChangedEvent() {
        for (GetShopApplicationsChanged list : changeListeners) {
            list.refresh();
        }
    }
    
    private Map<String, Application> finalizeApplications() {
        List<Application> apps = new ArrayList();
        applications.values().stream()
                .forEach(app -> apps.add(finalizeApp(app)));
        return apps.stream().collect(Collectors.toMap(o -> o.id, o -> o));
    }

    private Application finalizeApp(Application app) {
        app.applicationModule = modulePool.getModule(app.moduleId);
        return app;
    }
    
    public List<ApplicationModule> getModules() {
        return modulePool.getModules();
    }

    public void addListener(GetShopApplicationsChanged listener) {
        changeListeners.add(listener);
    }

    @Override
    public void deleteApplication(String applicationId) {
        Application app = applications.remove(applicationId);
        if (app != null) {
            databaseDelete(app);
            finalizeApplications();
            triggerChangedEvent();    
        }
        
    }
}
