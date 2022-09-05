package com.thundashop.services.pmspricing;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.core.pmsmanager.PmsPricingDayObject;
import com.thundashop.repository.utils.SessionInfo;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Naim Murad (naim)
 * @since 8/30/22
 */
public interface IPmsPricingService {
    PmsPricing getByCodeOrDefaultCode(String code, SessionInfo sessionInfo);

    PmsPricing getByDefaultCode(SessionInfo sessionInfo);

    int deleteByCode(String code, SessionInfo sessionInfo);

    List<String> getPriceCodes(SessionInfo sessionInfo);

    boolean existByCode(String code, SessionInfo sessionInfo);

    PmsPricing save(PmsPricing pmsPricing, SessionInfo sessionInfo);

    PmsPricing setPrices(String code, PmsPricing newPrices, SessionInfo sessionInfo);

    Pair<Date, Date> updatePrices(List<PmsPricingDayObject> prices, Map<String, BookingItemType> types, SessionInfo sessionInfo);

    void createNewPricePlan(String code, SessionInfo sessionInfo);
}
