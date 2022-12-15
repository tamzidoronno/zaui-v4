package com.thundashop.repository.zauiactivityrepository;

import java.util.Optional;

import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.ZauiActivity;

public interface IZauiActivityRepository extends IRepository<ZauiActivity> {
    Optional<ZauiActivity> getById(String id, SessionInfo sessionInfo);

    ZauiActivity getByOptionId(String optionId, SessionInfo sessionInfo);
}
