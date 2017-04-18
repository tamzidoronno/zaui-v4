package com.thundashop.core.timeregisteringmanager;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import org.mongodb.morphia.annotations.Transient;

public class TimeRegistered extends DataCommon {
    public Date start;
    public Date end;
    public String comment;
    public String userId;
    
    @Transient
    public Integer hours;
    
    @Transient
    public Integer minutes;
}
