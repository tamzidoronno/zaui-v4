package com.thundashop.core.pmsmanager;

public interface IPmsPricingManager {

    PmsPricing getByCodeOrDefaultCode(String code);

    PmsPricing getByDefaultCode();

    int deleteByCode(String code);

}
