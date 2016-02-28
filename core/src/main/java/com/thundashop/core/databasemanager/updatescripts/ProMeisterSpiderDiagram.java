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
public class ProMeisterSpiderDiagram extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("28/02-2016");
    }
    
    @Override
    public String getId() {
        return "6f6fb641-e70c-4a8f-b201-9c0d4bf77dbb";
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
        new ProMeisterSpiderDiagram().runSingle();
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

        Application ProMeisterSpiderDiagram = createSettings("ProMeisterSpiderDiagram",
        "1de1b272-2de3-482f-90cd-6bb8ceb77aca",
        allowed2,
        " ",
        Application.Type.Marketing, true);
        ProMeisterSpiderDiagram.isPublic = true;
        ProMeisterSpiderDiagram.isFrontend = true;
        ProMeisterSpiderDiagram.moduleId = "Marketing";
        ProMeisterSpiderDiagram.defaultActivate = false;
        ProMeisterSpiderDiagram.storeId = "all";
        ProMeisterSpiderDiagram.allowedStoreIds.add("6524eb45-fa17-4e8c-95a5-7387d602a69b");
        database.save(ProMeisterSpiderDiagram, credentials);
    }
}
