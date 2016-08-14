/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.start;

import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.storemanager.data.Store;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class GenerateTranslation extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("09/05-2016");
    }
    
    @Override
    public String getId() {
        return "b5e48320-d00d-42d5-96ac-ca45cc92b1f9";
    }

    @Override
    public boolean doNotRun() {
        return true;
    }
    
    public static void main(String[] args) {
        new GenerateTranslation().runSingle();
    }
    
    @Override
    public void run() {
        Store store = new Store();
        store.id = "6524eb45-fa17-4e8c-95a5-7387d602a69b";
        
        StoreApplicationPool pool = getManager(StoreApplicationPool.class, store, null);
        List<Application> apps = pool.getApplications();
        
        List<Application> appsToGetTranslationFor = apps.stream()
                .filter(app -> app.isFrontend)
                .collect(Collectors.toList());
        
        try {
            ParseTranslation gt = new ParseTranslation(new ArrayList());
            gt.startParsing();
            gt.printSummary();
        } catch (IOException ex) {
            Logger.getLogger(GenerateTranslation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
