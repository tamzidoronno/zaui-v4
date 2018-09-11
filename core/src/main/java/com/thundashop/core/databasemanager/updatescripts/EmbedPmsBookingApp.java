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
public class EmbedPmsBookingApp extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("05/09-2018");
    }
    
    @Override
    public String getId() {
        return "2bc01804-21ac-4b31-bf06-ee015fe4e51c";
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
        new EmbedPmsBookingApp().runSingle();
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

        Application EmbedPmsBookingApp = createSettings("EmbedPmsBookingApp",
        "805bb430-bdcf-4db9-8d3a-f227ee6d1799",
        allowed2,
        " ",
        Application.Type.Marketing, true);
        EmbedPmsBookingApp.isPublic = true;
        EmbedPmsBookingApp.isFrontend = true;
        EmbedPmsBookingApp.moduleId = "pms";
        EmbedPmsBookingApp.defaultActivate = true;
        EmbedPmsBookingApp.storeId = "all";
        database.save(EmbedPmsBookingApp, credentials);
    }
}
