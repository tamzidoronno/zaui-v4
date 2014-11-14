/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.applications;

import com.thundashop.core.appmanager.data.ApplicationModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
class ApplicationModulePool {
    
    Map<String, ApplicationModule> modules = new HashMap();
    
    @PostConstruct
    public void setup() {
        ApplicationModule other = new ApplicationModule();
        other.moduleName = "Other applications";
        other.id = "other";
        other.faIcon = "fa-heart-o";
        other.description = "We have not found a module that this applications fits into. If you have a suggestion where this applications belongs, please let us know by emailing post@getshop.com.";
        modules.put(other.id, other);
        
        ApplicationModule module = new ApplicationModule();
        module.moduleName = "E-Commerce";
        module.id = "WebShop";
        module.faIcon = "fa-shopping-cart";
        module.description = "Upgrade your website to become a fully customizable e-commerce solution. We offer a variouse of payment methods!";
        modules.put(module.id, module);
        
        ApplicationModule marketing = new ApplicationModule();
        marketing.moduleName = "Marketing";
        marketing.id = "Marketing";
        marketing.faIcon = "fa-area-chart";
        marketing.description = "Add more attraction to your website by activeting our marketing module";
        modules.put(marketing.id, marketing);
        
        ApplicationModule cms = new ApplicationModule();
        cms.moduleName = "Content Management";
        cms.id = "cms";
        cms.faIcon = "fa-file-text-o";
        cms.description = "CMS, activate this module to get all the tools you need to add text, images, movies etc.";
        modules.put(cms.id, cms);
        
        ApplicationModule reporting = new ApplicationModule();
        reporting.moduleName = "Reports";
        reporting.id = "reporting";
        reporting.faIcon = "fa-line-chart";
        reporting.description = "Are you interested in numbers? how are you performing, how many sms have you sent etc.";
        modules.put(reporting.id, reporting);
        
        
    }
    
    public ApplicationModule getModule(String id) {
        return modules.get(id);
    }
    
    public List<ApplicationModule> getModules() {
        ArrayList list = new ArrayList(modules.values());
        Collections.sort(list, new Comparator<ApplicationModule>() {

            @Override
            public int compare(ApplicationModule o1, ApplicationModule o2) {
                if (o1 == null || o2 == null) {
                    return 0;
                }
                
                return o1.moduleName.compareTo(o2.moduleName);
            }
        });
        return list;
    }
}
