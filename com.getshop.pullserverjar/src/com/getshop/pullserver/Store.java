/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.pullserver;

import java.util.Date;
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
    public boolean isVIS = false; //Very important shop.
    public boolean isDeepFreezed = false;
    public String deepFreezePassword = "";
    public boolean premium = false;
    public boolean mobileApp = false;
    
    public Date expiryDate;
    
    /**
     * This specifies if this store is setup as a template
     * If it is a template it can be cloned into another store :D
     */
    public boolean isTemplate = false;

}
