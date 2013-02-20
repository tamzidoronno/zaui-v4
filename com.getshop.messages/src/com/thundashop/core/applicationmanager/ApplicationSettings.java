package com.thundashop.core.applicationmanager;

import com.thundashop.core.common.DataCommon;
import java.util.List;

public class ApplicationSettings extends DataCommon {
    public String appName;
    public String description;
    public List<String> allowedAreas;
    public boolean isSingleton;
    public boolean renderStandalone;
    public boolean isPublic;
    public Double price;
    public String userId;
    
}
