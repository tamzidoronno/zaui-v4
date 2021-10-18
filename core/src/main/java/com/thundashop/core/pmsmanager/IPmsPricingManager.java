package com.thundashop.core.pmsmanager;

import java.util.List;

public interface IPmsPricingManager {

    PmsPricing getByCodeOrDefaultCode(String code);

    PmsPricing getByDefaultCode();

    int deleteByCode(String code);

    List<String> getPriceCodes();

    boolean existByCode(String code);

    PmsPricing save(PmsPricing pmsPricing);

}
