package com.thundashop.core.arx;

import com.thundashop.core.common.DataCommon;

public class Door extends DataCommon {
    public String externalId = "";
    public String name = "";  
    public String hostOwner = "";
    /** this one has not been implemented yet, figuring it out seems to be harder then it supposed to be. */
    public String state = "";
    public Boolean forcedOpen = false;
    public Boolean forcedClose = false;
}
