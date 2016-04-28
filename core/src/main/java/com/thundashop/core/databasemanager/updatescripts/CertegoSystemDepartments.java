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
public class CertegoSystemDepartments extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("28/04-2016");
    }
    
    @Override
    public String getId() {
        return "11df3559-8fd7-4d7d-8aa7-9926ea0ee895";
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
        new CertegoSystemDepartments().runSingle();
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

        Application CertegoSystemDepartments = createSettings("CertegoSystemDepartments",
        "48a459b8-90b2-4dea-9bae-f548d006f526",
        allowed2,
        " ",
        Application.Type.Marketing, true);
        CertegoSystemDepartments.isPublic = true;
        CertegoSystemDepartments.isFrontend = true;
        CertegoSystemDepartments.moduleId = "other";
        CertegoSystemDepartments.defaultActivate = false;
        CertegoSystemDepartments.storeId = "all";
        CertegoSystemDepartments.allowedStoreIds = new ArrayList();
        CertegoSystemDepartments.allowedStoreIds.add("1e647711-6624-40fd-807e-7673250accc4");
        
        database.save(CertegoSystemDepartments, credentials);
    }
}
