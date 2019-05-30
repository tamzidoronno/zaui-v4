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
        
        ApplicationModule ecommercetemplatestuff = new ApplicationModule();
        ecommercetemplatestuff.moduleName = "WebShop TemplateStuff";
        ecommercetemplatestuff.needToShowInMenu = false;
        ecommercetemplatestuff.id = "ecommercetemplate";
        ecommercetemplatestuff.faIcon = "fa-shopping-cart";
        ecommercetemplatestuff.description = "This is a group of applications that is used for setting up an ecommerce platform, highly adaptable productpages and stuff.!";
        modules.put(ecommercetemplatestuff.id, ecommercetemplatestuff);
        
        ApplicationModule module = new ApplicationModule();
        module.moduleName = "E-Commerce ";
        module.needToShowInMenu = true;
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
        
        ApplicationModule questback = new ApplicationModule();
        questback.moduleName = "QuestBack";
        questback.id = "questback";
        questback.faIcon = "fa-question";
        questback.description = "Questback applications, this module is containing all the applications that is required to setup a proper questback";
        modules.put(questback.id, questback);
        
        ApplicationModule booking = new ApplicationModule();
        booking.moduleName = "PMS Booking";
        booking.id = "booking";
        booking.faIcon = "fa-calendar";
        booking.description = "Booking for properties.";
        modules.put(booking.id, booking);
        
        ApplicationModule eventBooking = new ApplicationModule();
        eventBooking.moduleName = "Event Booking";
        eventBooking.id = "eventbooking";
        eventBooking.faIcon = "fa-calendar";
        eventBooking.description = "Booking for events.";
        modules.put(eventBooking.id, eventBooking);
        
        ApplicationModule sedoxPerformance = new ApplicationModule();
        sedoxPerformance.moduleName = "Sedox Performance";
        sedoxPerformance.id = "sedox";
        sedoxPerformance.faIcon = "fa-gears";
        sedoxPerformance.allowedStoreIds.add("eafea78d-1eea-403f-abbb-3b23a6e61dae");
        sedoxPerformance.description = "Special apps for sedox performance.";
        modules.put(sedoxPerformance.id, sedoxPerformance);
        
        ApplicationModule mecaFleetModule = new ApplicationModule();
        mecaFleetModule.id = "mecafleet";
        mecaFleetModule.moduleName = "MECA Fleet module";
        mecaFleetModule.faIcon = "fa-gears";
        mecaFleetModule.allowedStoreIds = new ArrayList();
        mecaFleetModule.description = "Special apps for meca fleet system.";
        modules.put(mecaFleetModule.id, mecaFleetModule);
        
        ApplicationModule c3AccountingModule = new ApplicationModule();
        c3AccountingModule.id = "c3accountingmodule";
        c3AccountingModule.moduleName = "Timesystem";
        c3AccountingModule.faIcon = "fa-gears";
        c3AccountingModule.needToShowInMenu = true;
        c3AccountingModule.allowedStoreIds.add("f2d0c13c-a0f7-41a7-8584-3c6fa7eb68d1");
        c3AccountingModule.description = "Special apps for C3, accounting stuff.";
        modules.put(c3AccountingModule.id, c3AccountingModule);
        
        ApplicationModule pms = new ApplicationModule();
        pms.moduleName = "GetShop Module - Property Management";
        pms.id = "pms";
        pms.faIcon = "fa-calendar";
        pms.description = "GetShop Module.";
        modules.put(pms.id, pms);
        
        ApplicationModule apac = new ApplicationModule();
        apac.moduleName = "GetShop Module - APAC";
        apac.id = "apac";
        apac.faIcon = "fa-calendar";
        apac.description = "GetShop Module.";
        modules.put(apac.id, apac);
        
        ApplicationModule salespoint = new ApplicationModule();
        salespoint.moduleName = "GetShop Module - SALESPOINT";
        salespoint.id = "salespoint";
        salespoint.faIcon = "fa-money";
        salespoint.description = "GetShop Module.";
        modules.put(salespoint.id, salespoint);
        
        ApplicationModule pmsconference = new ApplicationModule();
        pmsconference.moduleName = "GetShop Module - Conference";
        pmsconference.id = "pmsconference";
        pmsconference.faIcon = "fa-group";
        pmsconference.description = "GetShop Module.";
        modules.put(pmsconference.id, pmsconference);
        
        ApplicationModule ecommerce = new ApplicationModule();
        ecommerce.moduleName = "GetShop Module - ECOMMERCE";
        ecommerce.id = "ecommerce";
        ecommerce.faIcon = "fa-shopping-cart";
        ecommerce.description = "GetShop Module.";
        modules.put(ecommerce.id, ecommerce);
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
