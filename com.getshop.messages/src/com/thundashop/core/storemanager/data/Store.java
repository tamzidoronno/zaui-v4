package com.thundashop.core.storemanager.data;

import com.thundashop.core.common.DataCommon;
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
    public String partnerId;
    public boolean isVIS = false; //Very important shop.
}
