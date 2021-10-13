package com.thundashop.core.pmsmanager;

public interface IPmsPricingManager {

    PmsPricing getByCodeOrDefaultCode(String code);

}
