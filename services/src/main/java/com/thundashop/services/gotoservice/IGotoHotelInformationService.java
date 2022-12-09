package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.Hotel;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.core.storemanager.data.Store;

public interface IGotoHotelInformationService {
    Hotel getHotelInformation(Store store, PmsConfiguration pmsConfiguration, String currencyCode);
}
