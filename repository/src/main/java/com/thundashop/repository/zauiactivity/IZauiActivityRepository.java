package com.thundashop.repository.zauiactivity;

import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.ZauiActivity;

import java.util.Optional;

public interface IZauiActivityRepository extends IRepository<ZauiActivity> {
    Class<ZauiActivity> getEntityClass();

    Optional<ZauiActivity> getZauiActivity(SessionInfo sessionInfo);
}
