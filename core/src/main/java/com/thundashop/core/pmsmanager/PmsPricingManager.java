package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.repository.pmsmanager.PmsPricingRepository;
import com.thundashop.repository.utils.SessionInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component
@GetShopSession
public class PmsPricingManager extends ManagerBase implements IPmsPricingManager {

    private static final Logger logger = LoggerFactory.getLogger(PmsPricingManager.class);

    private static final String defaultCode = "default";

    private static final Map<String, PmsPricing> pricingMap = new ConcurrentHashMap<>();

    private final PmsPricingRepository pmsPricingRepository;

    @Autowired
    public PmsPricingManager(PmsPricingRepository pmsPricingRepository) {
        this.pmsPricingRepository = pmsPricingRepository;
    }

    @Override
    public PmsPricing getByCodeOrDefaultCode(String code) {
        if (isEmpty(code)) {
            // New booking has empty string price code
            return pricingMap.computeIfAbsent(defaultCode, k -> getByDefaultCode());
        }
        return pricingMap.computeIfAbsent(code, this::getPmsPricing); // TODO refactor
    }

    private PmsPricing getPmsPricing(String code) {
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

    @Override
    public int deleteByCode(String code) {
        pricingMap.remove(code);
        return pmsPricingRepository.markDeleteByCode(code, getSessionInfo());
    }

    @Override
    public List<String> getPriceCodes() {
        return pmsPricingRepository.getPriceCodes(getStoreIdInfo());
    }

    @Override
    public boolean existByCode(String code) {
        return pmsPricingRepository.existByCode(code, getStoreIdInfo());
    }

    @Override
    public PmsPricing save(PmsPricing pmsPricing) {
        pricingMap.remove(pmsPricing.code); // TODO remove
        pricingMap.put(pmsPricing.code, pmsPricing);
        pmsPricingRepository.save(pmsPricing, getSessionInfo());
        return pmsPricing;
    }

    @Override
    public PmsPricing setPrices(String code, PmsPricing newPrices) {
        logger.debug("New prices set from setPrices call code {} , startDate {} , endDate {}", code,
                newPrices.getStartDate(), newPrices.getEndDate());

        PmsPricing prices = getByCodeOrDefaultCode(code);
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

        return save(prices);
    }

    @Override
    public Pair<Date, Date> updatePrices(List<PmsPricingDayObject> prices, Map<String, BookingItemType> types) {
        Date start = null, end = null;
        PmsPricing pricesToUpdate = getByDefaultCode();

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
                logger.info("New prices set from updatePrices: {} ,  date: {} , new price: {}", types.get(price.typeId).name, price.date, price.newPrice);
            }
        }

        setPrices(pricesToUpdate.code, pricesToUpdate);
        return Pair.of(start, end);
    }

    @Override
    public void createNewPricePlan(String code) {
        boolean exist = existByCode(code);

        if (exist) {
            return;
        }

        PmsPricing price = new PmsPricing();
        price.code = code;
        save(price);
    }
}
