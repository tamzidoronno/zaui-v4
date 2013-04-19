package com.thundashop.core.appmanager.data;

import com.google.code.morphia.annotations.Transient;
import com.thundashop.core.common.DataCommon;
import java.util.Date;

public class ApplicationSubscription extends DataCommon {
    public String appSettingsId;
    public Date from_date;
    public Date to_date;
    public boolean payedfor;
    @Transient
    public ApplicationSettings app;
    @Transient
    public int numberOfInstancesAdded;
}
