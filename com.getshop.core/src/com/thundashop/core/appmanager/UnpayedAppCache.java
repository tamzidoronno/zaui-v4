package com.thundashop.core.appmanager;

import com.thundashop.core.appmanager.data.ApplicationSubscription;
import java.util.Date;
import java.util.List;

public class UnpayedAppCache {
    public Date expire;
    List<ApplicationSubscription> cache;
}
