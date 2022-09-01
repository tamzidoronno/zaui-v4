package com.thundashop.repository.pmsmanager;

import java.util.List;
import java.util.Optional;

import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;

public interface IPmsPricingRepository extends IRepository<PmsPricing> {    
    Optional<PmsPricing> findPmsPricingByCode(String code, SessionInfo sessionInfo);
    int markDeleteByCode(String code, SessionInfo sessionInfo);
    List<String> getPriceCodes(SessionInfo sessionInfo);
    boolean existByCode(String code, SessionInfo sessionInfo);
}
