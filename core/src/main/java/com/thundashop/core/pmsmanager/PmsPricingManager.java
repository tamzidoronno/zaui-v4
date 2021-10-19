package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.repository.pmsmanager.PmsPricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@GetShopSession
public class PmsPricingManager extends ManagerBase implements IPmsPricingManager {

    private static final String defaultCode = "default";

    private static final Map<String, PmsPricing> pricingMap = new ConcurrentHashMap<>();

    private final PmsPricingRepository pmsPricingRepository;

    @Autowired
    public PmsPricingManager(PmsPricingRepository pmsPricingRepository) {
        this.pmsPricingRepository = pmsPricingRepository;
    }

    @Override
    public PmsPricing getByCodeOrDefaultCode(String code) {
        return pricingMap.computeIfAbsent(code, this::getPmsPricing); // TODO refactor
    }

    private PmsPricing getPmsPricing(String _code) {
        SessionInfo storeIdInfo = getStoreIdInfo();
        return pmsPricingRepository.findPmsPricingByCode(_code, storeIdInfo)
                .orElseGet(() -> pmsPricingRepository.findPmsPricingByCode(defaultCode, storeIdInfo)
                        .orElse(null));
    }

    @Override
    public PmsPricing getByDefaultCode() {
        return pmsPricingRepository.findPmsPricingByCode(defaultCode, getStoreIdInfo())
                .orElse(null);
    }

    @Override
    public int deleteByCode(String code) {
        pricingMap.remove(code);
        return pmsPricingRepository.markDeleteByCode(code, getSessionInfo());
    }

    @Override
    public List<String> getPriceCodes() {
        return new ArrayList<>(pricingMap.keySet());
    }

    @Override
    public boolean existByCode(String code) {
        return pmsPricingRepository.existByCode(code, getStoreIdInfo());
    }

    @Override
    public PmsPricing save(PmsPricing pmsPricing) {
        pricingMap.remove(pmsPricing.code); // TODO remove
        pricingMap.put(pmsPricing.code, pmsPricing);
        saveObject(pmsPricing); // TODO move to repository
        return pmsPricing;
    }
}
