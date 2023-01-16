package com.thundashop.constant;

public enum SchedulerTimerConstant {
    BOOKING_ENGINE_PMS_PROCESSOR( "pmsprocessor","0 6,16 * * *"),
    GET_SHOP_LOCK_PMS_PROCESSOR("pmsprocessor","* * * * *"),
    PMS_MANAGER_PMS_PROCESSOR("pmsprocessor","* * * * *"),
    ORDER_CAPTURE_CHECK_PROCESSOR ("ordercapturecheckprocessor","2,7,12,17,22,27,32,37,42,47,52,57 * * * *"),
    CHECK_ORDER_PAYMENT_STATUS("checkorderpaymentstatus", "*/30 * * * *"),
    ORDER_COLLECTOR("ordercollector","*/10 * * * *"),
    EHF_DATAHOTEL_DOWNLOADER ("ehf_datahotel_downloader","33 3 * * *"),
    STORE_OCR_PROCESSOR ("storeocrprocessor","20 17,3,5 * * *"),
    JOMRES_FETCH_BOOKING ("jomresFetchBooking", "*/6 * * * *"),
    JOMRES_UPDATE_AVAILABILITY ("jomresUpdateAvailability","*/7 * * * *"),
    FETCH_LOCK_LOCK ("fetchLockLock", "22 0,2,4,6,8,10,12,14,16,18,20,22 * * *"),
    PMS_PROCESSOR_LOCK ("pmsprocessor_lock", "30 23,04 * * *"),
    CHECK_CRON_GETSHOP_LOCK_SYSTEM_MANAGER ("checkCronGetShopLockSystemManager", "*/5 * * * *"),
    AUTO_EXPIRE_BOOKINGS ( "AutoExpireBookings","*/5 * * * *"),
    CARE_TAKER_PROCESSOR ("caretakerprocessor", "1 1 08 * *"),
    CHECK_INVOICE_DTO_DATE ("checkinvoicedtodate", "1 04 * * *"),
    PMS_MAIL_STATS ("pmsmailstats", "1 23 * * *"),
    PMS_PROCESSOR_2 ("pmsprocessor2", "5 * * * *"),
    PMS_PROCESSOR_3 ( "pmsprocessor3","7,13,33,53 * * * *"),
    TRIGGER ("trigger", "*/5 * * * *"),
    CHECK_SCORM_RESULTS ("checkScormResults", "* * * * *"),
    CHECK_REMOVAL_OF_ROUTES ("checkRemovalOfRoutes", "0 * * * *"),
    WUBOOK_PROCESSOR("wubookprocessor","* * * * *"),
    WUBOOK_PROCESSOR_2("wubookprocessor2","1 5,12,22 * * *"),
    EVENT_QUESTBACK_CHECKED("event_questback_checked","0 * * * *");

    private final String name;
    private final String time;

   SchedulerTimerConstant(String name, String time){
      this.name=name;
      this.time=time;
    }


}
