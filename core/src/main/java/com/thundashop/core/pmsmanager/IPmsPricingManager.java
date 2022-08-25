package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.BookingItemType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IPmsPricingManager {

    PmsPricing getByCodeOrDefaultCode(String code);

    PmsPricing getByDefaultCode();

    int deleteByCode(String code);

    List<String> getPriceCodes();

    boolean existByCode(String code);

    PmsPricing save(PmsPricing pmsPricing);

    PmsPricing setPrices(String code, PmsPricing pmsPricing);

    Pair<Date, Date> updatePrices(List<PmsPricingDayObject> prices, Map<String, BookingItemType> types);

    void createNewPricePlan(String code);

}
