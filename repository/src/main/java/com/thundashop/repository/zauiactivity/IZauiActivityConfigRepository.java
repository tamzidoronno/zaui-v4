package com.thundashop.repository.zauiactivity;

import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

import java.util.Optional;

public interface IZauiActivityConfigRepository extends IRepository<ZauiActivityConfig> {
    Optional<ZauiActivityConfig> getZauiActivityConfig(SessionInfo sessionInfo);

    Class<ZauiActivityConfig> getEntityClass();
}
