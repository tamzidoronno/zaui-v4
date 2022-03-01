package com.thundashop.core.pmsbookingprocess;

public class BookingEmbedConfig extends BookingConfig {
    public String serverTimeZone = "Europe/Oslo";
    public BookingEmbedConfig (BookingConfig bookingConfig) {
        childAge = bookingConfig.childAge;
        currencyText = bookingConfig.currencyText;
        doNotRecommendBestPrice = bookingConfig.doNotRecommendBestPrice;
        defaultCheckinTime = bookingConfig.defaultCheckinTime;
        phonePrefix = bookingConfig.phonePrefix;
        startYesterday = bookingConfig.startYesterday;
        ignoreGuestInformation = bookingConfig.ignoreGuestInformation;
    }
}
