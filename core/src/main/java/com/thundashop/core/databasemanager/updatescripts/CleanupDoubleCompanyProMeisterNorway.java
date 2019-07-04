/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.pdf.InvoiceGeneratorCartItem;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class CleanupDoubleCompanyProMeisterNorway extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("24/02-2017");
    }
    
    @Override
    public String getId() {
        return "91d79907-1a5c-4a2f-b8cf-72b90574c199";
    }
    
    public static void main(String[] args) {
        new CleanupDoubleCompanyProMeisterNorway().runSingle();
    }
    
    @Override
    public void run() {
//        Store store = new Store();
//        store.id = "17f52f76-2775-4165-87b4-279a860ee92c";
//        
//        UserManager userManager = getManager(UserManager.class, store, null);
//        
//        List<Company> companies = userManager.getAllCompanies();
//        
//        Map<String, List<Company>> result = companies.stream()
//            .collect(
//                Collectors.groupingBy(e -> e.vatNumber, Collectors.toList())
//            );
//        
//        for (String companyId : result.keySet()) {
//            if (result.get(companyId).size() > 1) {
//                Company mainCompany = result.get(companyId).get(0);
//                for (Company comp : result.get(companyId)) {
//                    if (comp.equals(mainCompany))
//                        continue;
//                    
//                    List<User> users = userManager.getUsersThatHasCompany(comp.id);
//                    for (User user: users) {
//                        if (user.company != null) {
//                            user.company.clear();
//                            user.company.add(mainCompany.id);
//                            userManager.saveCustomerDirect(user);
//                        }
//                    }
//                    
//                    userManager.deleteCompany(comp.id);
//                }
//            }
//        }
        // Your magic code goes here :D
    }
}
