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
public class RenaTreningsTheme extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("20/04-2016");
    }
    
    @Override
    public String getId() {
        return "893942f0-7514-4de0-8ec1-38b708fd1a9e";
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
        new RenaTreningsTheme().runSingle();
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

        Application RenaTreningsTheme = createSettings("RenaTreningsTheme",
        "969e6b86-3cdb-4653-a5b2-8f1aa3cbddac",
        allowed2,
        " ",
        Application.Type.Theme, true);
        RenaTreningsTheme.isPublic = false;
        RenaTreningsTheme.isFrontend = true;
        RenaTreningsTheme.moduleId = "other";
        RenaTreningsTheme.defaultActivate = false;
        RenaTreningsTheme.storeId = "all";
        RenaTreningsTheme.allowedStoreIds.add("cd94ea1c-01a1-49aa-8a24-836a87a67d3b");
        database.save(RenaTreningsTheme, credentials);
    }
}
