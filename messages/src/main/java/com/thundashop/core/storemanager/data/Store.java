package com.thundashop.core.storemanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 *
 * @author ktonder
 */
public class Store extends DataCommon {
    public String webAddress;
    public String webAddressPrimary;
    public String identifier;
    public List<String> additionalDomainNames;
    public boolean registeredDomain=false;
    public boolean readIntroduction=false;
    public boolean isExtendedMode=true;
    public StoreConfiguration configuration;
    public boolean isVIS = false; //Very important shop.
    public boolean isDeepFreezed = false;
    public String deepFreezePassword = "";
    public boolean premium = false;
    public boolean mobileApp = false;
    public String favicon = "";
    public String masterStoreId = "";
    public List<String> acceptedSlaveIds = new ArrayList();
    
    /**
     * If this is set the it will default to this 
     * multilevelname unless its specified something else.
     * 
     */
    public String defaultMultilevelName = "default";
    
    public Date expiryDate;
    
    /**
     * This specifies if this store is setup as a template
     * If it is a template it can be cloned into another store :D
     */
    public boolean isTemplate = false;
    public User registrationUser;
    public String country;
    public String timeZone;
    public boolean acceptedGDPR = false;
    public Date acceptedGDPRDate = null;
    public String acceptedByUser = "";

    public String getDefaultMailAddress() {
        if (configuration.emailAdress == null || configuration.emailAdress.isEmpty()) {
            return "post@getshop.com";
        }
        
        return configuration.emailAdress;
    }

    public String getDefaultWebAddress() {
        if (webAddress != null && !webAddress.isEmpty()) {
            return webAddress;
        }
        
        if (webAddressPrimary != null && !webAddressPrimary.isEmpty()) {
            return webAddressPrimary;
        }
        
        if (additionalDomainNames != null && !additionalDomainNames.isEmpty()) {
            return additionalDomainNames.get(0);
        }
        
        return null;
    }
    
    public TimeZone getTimeZone() {
        if (timeZone == null || timeZone.isEmpty()) {
            return TimeZone.getTimeZone("Europe/Oslo");
        }
        
        return TimeZone.getTimeZone(timeZone);
    }
}
