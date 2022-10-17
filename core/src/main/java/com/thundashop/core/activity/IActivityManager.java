package com.thundashop.core.activity;

import com.thundashop.core.activity.dto.ActivityConfig;

public interface IActivityManager {
    ActivityConfig getActivityConfig();

    void updateActivityConfig(ActivityConfig newActivityConfig);
}
