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
public class PmsBookingMultipleSelectionBooking extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("26/01-2017");
    }
    
    @Override
    public String getId() {
        return "28c4ac69-ed50-42a7-8683-74ae9f60814c";
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
        new PmsBookingMultipleSelectionBooking().runSingle();
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

        Application PmsBookingMultipleSelectionBooking = createSettings("PmsBookingMultipleSelectionBooking",
        "0f6913fb-b28d-49b1-9c6b-7aa023bd21bb",
        allowed2,
        " ",
        Application.Type.Webshop, true);
        PmsBookingMultipleSelectionBooking.isPublic = true;
        PmsBookingMultipleSelectionBooking.isFrontend = true;
        PmsBookingMultipleSelectionBooking.moduleId = "booking";
        PmsBookingMultipleSelectionBooking.defaultActivate = false;
        PmsBookingMultipleSelectionBooking.storeId = "all";
        database.save(PmsBookingMultipleSelectionBooking, credentials);
    }
}
