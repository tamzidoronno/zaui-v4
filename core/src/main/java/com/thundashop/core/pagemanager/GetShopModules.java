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
    private static HashMap<String, GetShopModule> modules = new HashMap();

    public GetShopModules() {
        addModule("pms", "PMS", true, 2, "0f70b6b0-97a6-4cd1-9b22-cc30332054b5", "icon-bed"); //GetShopThemePms
        addModule("cms", "CMS", false, 1, "", "icon-document-file-html");
        addModule("salespoint", "Salespoint", true, 3, "83d6098e-43b1-4b52-a0d1-665525c67e80", "icon-cashier"); // SalespointModuleTheme
        addModule("ecommerce", "Ecommerce", true, 4, "f599c0a6-6f76-49af-ba15-7d496dd4387b", "icon-basket"); // EcommerceModuleTheme
        addModule("crm", "CRM", true, 5, "14d46706-d524-4d3c-abde-7fb1a56ca2b9", "icon-users");   
        addModule("apac", "APAC", true, 6, "affd0624-7311-480c-93bc-06b72459b45d", "icon-key"); // GetShopModuleApacTheme
        addModule("settings", "Settings", true, 7, "75f4dc38-a356-4d04-866c-c8bc4e11d7ab", "icon-gs-gears"); // GetShopModuleSettingsTheme
        addModule("account", "Accounting", true, 8, "a34d4cdd-e339-4c19-b1ae-500b510a4209", "icon-calculator"); // AccountingModuleTheme
        addModule("srs", "Resturant", true, 8, "a34d4cdd-e339-4c19-b1ae-500b510a4209", "icon-dinner"); // AccountingModuleTheme
        addModule("ticket", "TicketSystem", true, 9, "a34d4cdd-e339-4c19-b1ae-500b510a4209", "icon-lifebuoy"); // TicketTheme
        addModule("invoice", "Invoicing", true, 10, "a34d4cdd-e339-4c19-b1ae-500b510a4209", "icon-receipt"); // Invoicing system
        addModule("analytics", "Analytics", true, 11, "a34d4cdd-e339-4c19-b1ae-500b510a4209", "icon-chart-growth"); // Invoicing system
        addModule("comfort", "Comfort", true, 11, "c9c1f200-41c6-4f27-8676-5b63c4da30c3", "fa-thermometer"); // Comfort system
        addModule("getshopnone", "None", true, 12, "a34d4cdd-e339-4c19-b1ae-500b510a4209", "icon-none"); // None, used to block users from all other modules
    }

    private void addModule(String nameAndId, String displayName, boolean externalPageTemplate, int sequence, String themeApplicationId, String fontAwesome) {
        if (GetShopModules.modules.containsKey(nameAndId)) {
            return;
        }
        
        GetShopModule module = new GetShopModule();
        module.name = displayName;
        module.id = nameAndId;
        module.storeDataRemoteForThisModule = externalPageTemplate;
        module.sequence = sequence;
        module.themeApplicationId = themeApplicationId;
        module.fontAwesome = fontAwesome;
        
        GetShopModules.modules.put(module.id, module);
    }

    public List<GetShopModule> getModules() {
        ArrayList retListe = new ArrayList(GetShopModules.modules.values());
        
        Collections.sort(retListe, (GetShopModule m1, GetShopModule m2) -> {
            return m1.sequence.compareTo(m2.sequence);
        });
        
        return retListe;
    }

    public boolean shouldStoreRemote(String shopModule) {
        GetShopModule module = GetShopModules.modules.get(shopModule);
        
        if (module == null) {
            return false;
        }
        
        return module.storeDataRemoteForThisModule;
    }

    public String getThemApplicationId(String currentGetShopModule) {
        GetShopModule module = GetShopModules.modules.get(currentGetShopModule);
        
        if (module == null) {
            return "";
        }
        
        return module.themeApplicationId;
    }

    public GetShopModule getModule(String moduleId) {
        return modules.get(moduleId);
    }
    
}
