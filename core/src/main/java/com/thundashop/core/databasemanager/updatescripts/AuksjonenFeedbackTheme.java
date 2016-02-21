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
public class AuksjonenFeedbackTheme extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("21/02-2016");
    }
    
    @Override
    public String getId() {
        return "aac55366-9090-443c-85d7-d33540a7757c";
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
        new AuksjonenFeedbackTheme().runSingle();
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

        Application AuksjonenFeedbackTheme = createSettings("AuksjonenFeedbackTheme",
        "fbf97749-c49d-41be-9314-c71de4893f9f",
        allowed2,
        " ",
        Application.Type.Theme, true);
        AuksjonenFeedbackTheme.isPublic = true;
        AuksjonenFeedbackTheme.isFrontend = true;
        AuksjonenFeedbackTheme.moduleId = "other";
        AuksjonenFeedbackTheme.defaultActivate = false;
        AuksjonenFeedbackTheme.storeId = "all";
        database.save(AuksjonenFeedbackTheme, credentials);
    }
}
