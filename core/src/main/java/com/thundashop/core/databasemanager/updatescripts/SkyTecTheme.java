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
public class SkyTecTheme extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("25/02-2016");
    }
    
    @Override
    public String getId() {
        return "6d9850eb-3ce5-4d6d-b49a-ddde55be4a56";
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
        new SkyTecTheme().runSingle();
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

        Application SkyTecTheme = createSettings("SkyTecTheme",
        "566fb905-a4a7-43bf-ad09-b9b6f15b8ead",
        allowed2,
        " ",
        Application.Type.Theme, true);
        SkyTecTheme.isPublic = true;
        SkyTecTheme.isFrontend = true;
        SkyTecTheme.moduleId = "other";
        SkyTecTheme.defaultActivate = false;
        SkyTecTheme.allowedStoreIds = new ArrayList();
        SkyTecTheme.allowedStoreIds.add("d4d1317a-640f-4fb2-94f1-a41efd3b6b71");
        SkyTecTheme.storeId = "all";
        database.save(SkyTecTheme, credentials);
    }
}
