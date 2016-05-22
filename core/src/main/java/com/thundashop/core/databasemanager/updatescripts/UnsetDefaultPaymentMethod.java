/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class UnsetDefaultPaymentMethod extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("22/05-2016");
    }
    
    @Override
    public String getId() {
        return "b870e66c-9abd-4e5e-91ae-6be841e2995b";
    }
    
    public static void main(String[] args) {
        new UnsetDefaultPaymentMethod().runSingle();
    }
    
    @Override
    public void run() {
        
        Map<Store, List<DataCommon>> data = getAllData(UserManager.class);
        
        for (Store store : data.keySet()) {
            List<DataCommon> datas = data.get(store);
            for (DataCommon userData : datas) {
                if (userData instanceof User) {
                    User user = (User)userData;

                    if (user.preferredPaymentType != null && user.preferredPaymentType.equals("ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\\InvoicePayment")) {
                        user.preferredPaymentType = "";
                        database.save("UserManager", "col_"+store.id, user);
                    }
                }
            }
        }
    }
}
