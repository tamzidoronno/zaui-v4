package com.thundashop.core.appmanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import org.mongodb.morphia.annotations.Transient;

public class ApplicationSubscription extends DataCommon {
    public String appSettingsId;
    public Date from_date;
    public Date to_date;
    public boolean payedfor;
    @Transient
    public Application app;
    @Transient
    public int numberOfInstancesAdded;
}
