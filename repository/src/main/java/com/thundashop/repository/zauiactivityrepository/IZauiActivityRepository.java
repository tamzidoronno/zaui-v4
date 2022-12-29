package com.thundashop.repository.zauiactivityrepository;

import java.util.List;
import java.util.Optional;

import com.thundashop.core.common.DataCommon;
import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.ZauiActivity;

public interface IZauiActivityRepository extends IRepository<ZauiActivity> {
    Optional<ZauiActivity> getById(String id, SessionInfo sessionInfo);

    ZauiActivity getByOptionId(String optionId, SessionInfo sessionInfo);

    ZauiActivity getBySupplierAndProductId(int supplierId, int productId, SessionInfo sessionInfo);

    int markDeleted(List<String> zauiActivityIds, SessionInfo sessionInfo);

    DataCommon update(ZauiActivity zauiActivity, SessionInfo sessionInfo);
}
