/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class ChangeNumbersForPromeisterAcademy extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("29/08-2016");
    }
    
    @Override
    public String getId() {
        return "afb20efc-c7d1-4053-867a-05fb396eb025";
    }
    
    public static void main(String[] args) {
        new ChangeNumbersForPromeisterAcademy().runSingle();
    }
    
    @Override
    public void run() {
        Store store = new Store();
        store.id = "17f52f76-2775-4165-87b4-279a860ee92c";
        
        UserManager userManager = getManager(UserManager.class, store, "");
        User loggedOn = userManager.logOn("kai@getshop.com", "g4kkg4kk");
        userManager.getSession().currentUser = loggedOn;
        
        List<User> users = userManager.getAllUsers();
        for (User user : users) {
            user.prefix = "47";
            userManager.saveUser(user);
        }
    }
}
