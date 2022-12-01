package com.thundashop.repository.zauiactivity;

import java.util.Optional;

import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

public interface IZauiActivityConfigRepository extends IRepository<ZauiActivityConfig> {
    Optional<ZauiActivityConfig> getZauiActivityConfig(SessionInfo sessionInfo) throws NotUniqueDataException;
}
