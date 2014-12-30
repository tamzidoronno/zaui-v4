package com.thundashop.core.storemanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.User;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Store extends DataCommon {
    public String webAddress;
    public String webAddressPrimary;
    public List<String> additionalDomainNames;
    public boolean registeredDomain=false;
    public boolean readIntroduction=false;
    public boolean isExtendedMode=true;
    public StoreConfiguration configuration;
    public boolean isVIS = false; //Very important shop.
    public boolean isDeepFreezed = false;
    public String deepFreezePassword = "";
    
    /**
     * This specifies if this store is setup as a template
     * If it is a template it can be cloned into another store :D
     */
    public boolean isTemplate = false;
    public User registrationUser;
}
