package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.repository.pmsmanager.PmsPricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsPricingManager extends ManagerBase implements IPmsPricingManager {

    private static final String defaultCode = "default";

    private final PmsPricingRepository pmsPricingRepository;

    @Autowired
    public PmsPricingManager(PmsPricingRepository pmsPricingRepository) {
        this.pmsPricingRepository = pmsPricingRepository;
    }

    @Override
    public PmsPricing getByCodeOrDefaultCode(String code) {
        SessionInfo storeIdInfo = getStoreIdInfo();
        return pmsPricingRepository.findPmsPricingByCode(code, storeIdInfo)
                .orElseGet(() -> pmsPricingRepository.findPmsPricingByCode(defaultCode, storeIdInfo)
                        .orElse(null));
    }

    @Override
    public PmsPricing getByDefaultCode() {
        return pmsPricingRepository.findPmsPricingByCode(defaultCode, getStoreIdInfo())
                .orElse(null);
    }

}
