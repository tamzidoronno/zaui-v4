package com.thundashop.repository.zauiactivity;

import java.util.Optional;

import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.ZauiActivity;

public interface IZauiActivityRepository extends IRepository<ZauiActivity> {
    Optional<ZauiActivity> getZauiActivity(SessionInfo sessionInfo);
}
