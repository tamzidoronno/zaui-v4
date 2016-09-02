/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class AsanaProjects extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("02/09-2016");
    }
    
    @Override
    public String getId() {
        return "10b12b67-fbd0-4dcc-aaa3-8fcdc9cb0723";
    }

    @SuppressWarnings("empty-statement")
    private Application createSettings(String appName, String id, List<String> allowedAreas, String description, String type, boolean isSingleton) {
        Application applicationSettings = new Application();
        applicationSettings.appName = appName;
        applicationSettings.allowedAreas = allowedAreas;
        applicationSettings.id = id;
        applicationSettings.description = description;
        applicationSettings.price = 0.0;
        applicationSettings.type = type;
        applicationSettings.isPublic = true;
        applicationSettings.ownerStoreId = "cdae85c1-35b9-45e6-a6b9-fd95c18bb291";
        applicationSettings.userId = "3241047c-4c78-4465-a0ae-588111c570ff";
        applicationSettings.isSingleton = isSingleton;
        return applicationSettings;
    }
    
    public static void main(String[] args) {
        new AsanaProjects().runSingle();
    }
    
    @Override
    public void run() {
        List<String> allowed2 = new ArrayList();
        allowed2.add("cell");
        allowed2.add("small");
        allowed2.add("medium");
        allowed2.add("large");
        allowed2.add("xlarge");

        Credentials credentials = new Credentials();
        credentials.manangerName = "ApplicationPool";
        credentials.password = "ADFASDF";
        credentials.storeid = "all";

        Application AsanaProjects = createSettings("AsanaProjects",
        "b78a42d9-2713-4c89-bb9e-48c3dd363a40",
        allowed2,
        " ",
        Application.Type.Marketing, true);
        AsanaProjects.isPublic = false;
        AsanaProjects.isFrontend = true;
        AsanaProjects.moduleId = "other";
        AsanaProjects.defaultActivate = false;
        AsanaProjects.storeId = "all";
        AsanaProjects.allowedStoreIds = new ArrayList();
        AsanaProjects.allowedStoreIds.add("2b24c43c-3da9-48f2-92e0-4538cfff3eb0");
        
        database.save(AsanaProjects, credentials);
    }
}
