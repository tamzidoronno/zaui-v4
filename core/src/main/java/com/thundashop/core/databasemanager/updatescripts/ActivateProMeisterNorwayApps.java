/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.applications.GetShopApplicationPool;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.databasemanager.data.Credentials;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class ActivateProMeisterNorwayApps extends UpdateScriptBase implements UpdateScript {

    @Autowired
    private GetShopApplicationPool getShopApplicationPool;
    
    @Override
    public Date getAddedDate() {
        return getDate("18/07-2016");
    }
    
    @Override
    public String getId() {
        return "b6cbf345-8cb3-41e7-bdb6-4dae7e67c9cc";
    }
    
    public static void main(String[] args) {
        new ActivateProMeisterNorwayApps().runSingle();
    }
    
    @Override
    public void run() {
        getShopApplicationPool.setStoreId("all");
        getShopApplicationPool.setCredentials(createCredentialsForApplicationPool());
        getShopApplicationPool.giveAccessToStore("8a48c4d7-09e5-40e2-bdb3-72f706729d27", "17f52f76-2775-4165-87b4-279a860ee92c");
    }

    private Credentials createCredentialsForApplicationPool() {
        Credentials cred = new Credentials();
        cred.manangerName = "ApplicationPool";
        cred.storeid = "all";
        return cred;
    }
}
