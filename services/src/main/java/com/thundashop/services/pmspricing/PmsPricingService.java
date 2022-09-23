package com.thundashop.services.pmspricing;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.core.pmsmanager.PmsPricingDayObject;
import com.thundashop.repository.pmsmanager.PmsPricingRepository;
import com.thundashop.repository.utils.SessionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * @author Naim Murad (naim)
 * @since 8/30/22
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class PmsPricingService implements IPmsPricingService {
    private static final String defaultCode = "default";
    private final PmsPricingRepository pmsPricingRepository;
    private final Map<String, Map<String, PmsPricing>> storeWisePricingMap = new HashMap<>();

    @Override
    public PmsPricing getByCodeOrDefaultCode(String code, SessionInfo sessionInfo) {
        Map<String, PmsPricing> pricingMap = storeWisePricingMap.getOrDefault(sessionInfo.getStoreId(), new HashMap<>());
        String pricingCode = isEmpty(code) ? defaultCode : code;
        PmsPricing pricing = pricingMap.computeIfAbsent(pricingCode, k -> getPmsPricing(pricingCode, sessionInfo));
        storeWisePricingMap.putIfAbsent(sessionInfo.getStoreId(), pricingMap);
        return pricing;
    }

    private PmsPricing getPmsPricing(String code, SessionInfo sessionInfo) {
        return pmsPricingRepository.findPmsPricingByCode(code, sessionInfo)
                .orElseGet(() -> {
                    PmsPricing price = pmsPricingRepository.findPmsPricingByCode(defaultCode, sessionInfo)
                            .orElse(null);
                    if(price == null) log.warn("No default code found for PmsPricing");
                    return price;
                });
    }

    @Override
    public PmsPricing getByDefaultCode(SessionInfo sessionInfo) {
        return getByCodeOrDefaultCode(defaultCode, sessionInfo);
    }

    @Override
    public int deleteByCode(String code, SessionInfo sessionInfo) {
        Map<String, PmsPricing> pricingMap = storeWisePricingMap.getOrDefault(sessionInfo.getStoreId(), new HashMap<>());
        if(!pricingMap.containsKey(code)) {
            log.warn("Code: {} does not found in store {}", code, sessionInfo.getStoreId());
            return 0;
        }
        pricingMap.remove(code);
        return pmsPricingRepository.markDeleteByCode(code, sessionInfo);
    }

    @Override
    public List<String> getPriceCodes(SessionInfo sessionInfo) {
        return pmsPricingRepository.getPriceCodes(sessionInfo);
    }

    @Override
    public boolean existByCode(String code, SessionInfo sessionInfo) {
        return pmsPricingRepository.existByCode(code, sessionInfo);
    }

    @Override
    public PmsPricing save(PmsPricing pmsPricing, SessionInfo sessionInfo) {
        Map<String, PmsPricing> pricingMap = storeWisePricingMap.getOrDefault(sessionInfo.getStoreId(), new HashMap<>());
        pricingMap.put(pmsPricing.code, pmsPricing);
        storeWisePricingMap.putIfAbsent(sessionInfo.getStoreId(), pricingMap);
        pmsPricingRepository.save(pmsPricing, sessionInfo);
        return pmsPricing;
    }

    @Override
    public PmsPricing setPrices(String code, PmsPricing newPrices, SessionInfo sessionInfo) {
        log.debug("New prices set from setPrices call code {} , startDate {} , endDate {}", code,
                newPrices.getStartDate(), newPrices.getEndDate());

        PmsPricing prices = getByCodeOrDefaultCode(code, sessionInfo);
        prices.defaultPriceType = newPrices.defaultPriceType;
        prices.progressivePrices = newPrices.progressivePrices;
        prices.pricesExTaxes = newPrices.pricesExTaxes;
        prices.privatePeopleDoNotPayTaxes = newPrices.privatePeopleDoNotPayTaxes;
        prices.channelDiscount = newPrices.channelDiscount;
        prices.derivedPrices = newPrices.derivedPrices;
        prices.derivedPricesChildren = newPrices.derivedPricesChildren;
        prices.productPrices = newPrices.productPrices;
        prices.longTermDeal = newPrices.longTermDeal;
        prices.coveragePrices = newPrices.coveragePrices;
        prices.coverageType = newPrices.coverageType;

        for (String typeId : newPrices.dailyPrices.keySet()) {
            HashMap<String, Double> priceMap = newPrices.dailyPrices.get(typeId);
            for (String date : priceMap.keySet()) {
                HashMap<String, Double> existingPriceRange = prices.dailyPrices.computeIfAbsent(typeId, k -> new HashMap<>());
                Double price = priceMap.get(date);
                if (price == -999999.0) {
                    existingPriceRange.remove(date);
                } else {
                    existingPriceRange.put(date, priceMap.get(date));
                }
            }
        }

        return save(prices, sessionInfo);
    }

    @Override
    public Pair<Date, Date> updatePrices(List<PmsPricingDayObject> prices, Map<String, BookingItemType> types, SessionInfo sessionInfo) {
        Date start = null, end = null;
        PmsPricing pricesToUpdate = getByDefaultCode(sessionInfo);

        for(PmsPricingDayObject price : prices) {
            Date dayPrice = PmsBookingRooms.convertOffsetToDate(price.date);

            if(start == null || dayPrice.before(start)) {
                start = dayPrice;
            }

            if(end == null || dayPrice.after(end)) {
                end = dayPrice;
            }

            HashMap<String, Double> dailyPriceMatrix = pricesToUpdate.dailyPrices.get(price.typeId);

            if(dailyPriceMatrix != null) {
                dailyPriceMatrix.put(price.date, price.newPrice);
                log.info("New prices set from updatePrices: {} ,  date: {} , new price: {}", types.get(price.typeId).name, price.date, price.newPrice);
            }
        }

        setPrices(pricesToUpdate.code, pricesToUpdate, sessionInfo);
        return Pair.of(start, end);
    }

    @Override
    public void createNewPricePlan(String code, SessionInfo sessionInfo) {
        boolean exist = existByCode(code, sessionInfo);

        if (exist) {
            return;
        }

        PmsPricing price = new PmsPricing();
        price.code = code;
        save(price, sessionInfo);
    }
}
