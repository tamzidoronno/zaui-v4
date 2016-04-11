package com.thundashop.core.storemanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.User;
import java.util.Date;
import java.util.List;

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
    
    public Date expiryDate;
    
    /**
     * This specifies if this store is setup as a template
     * If it is a template it can be cloned into another store :D
     */
    public boolean isTemplate = false;
    public User registrationUser;

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
}
