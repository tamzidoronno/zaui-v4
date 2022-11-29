package com.thundashop.services.zauiactivityservice;

import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;


public interface IZauiActivityService {
    ZauiActivityConfig getZauiActivityConfig(SessionInfo sessionInfo);

    void importZauiActivities(Integer supplierId, SessionInfo sessionInfo) throws ZauiException;

    void setZauiActivityConfig(ZauiActivityConfig zauiActivityConfig, SessionInfo sessionInfo);
}
