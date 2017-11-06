/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pagemanager;

import com.thundashop.core.pagemanager.data.GetShopModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class GetShopModules {
    private HashMap<String, GetShopModule> modules = new HashMap();

    public GetShopModules() {
        addModule("pms", "PMS", true, 2, "0f70b6b0-97a6-4cd1-9b22-cc30332054b5", "fa-building");
        addModule("cms", "CMS", false, 1, "", "fa-align-center");
        addModule("salespoint", "Salespoint", true, 3, "83d6098e-43b1-4b52-a0d1-665525c67e80", "fa-money"); // SalespointModuleTheme
        addModule("ecommerce", "Ecommerce", true, 4, "f599c0a6-6f76-49af-ba15-7d496dd4387b", "fa-shopping-cart"); // EcommerceModuleTheme
        addModule("crm", "CRM", true, 5, "14d46706-d524-4d3c-abde-7fb1a56ca2b9", "fa-users"); 
    }

    private void addModule(String nameAndId, String displayName, boolean externalPageTemplate, int sequence, String themeApplicationId, String fontAwesome) {
        GetShopModule module = new GetShopModule();
        module.name = displayName;
        module.id = nameAndId;
        module.storeDataRemoteForThisModule = externalPageTemplate;
        module.sequence = sequence;
        module.themeApplicationId = themeApplicationId;
        module.fontAwesome = fontAwesome;
        
        modules.put(module.id, module);
        
        
    }

    public List<GetShopModule> getModules() {
        ArrayList retListe = new ArrayList(modules.values());
        
        Collections.sort(retListe, (GetShopModule m1, GetShopModule m2) -> {
            return m1.sequence.compareTo(m2.sequence);
        });
        
        return retListe;
    }

    public boolean shouldStoreRemote(String shopModule) {
        GetShopModule module = modules.get(shopModule);
        
        if (module == null) {
            return false;
        }
        
        return module.storeDataRemoteForThisModule;
    }

    public String getThemApplicationId(String currentGetShopModule) {
        GetShopModule module = modules.get(currentGetShopModule);
        
        if (module == null) {
            return "";
        }
        
        return module.themeApplicationId;
    }
    
}
