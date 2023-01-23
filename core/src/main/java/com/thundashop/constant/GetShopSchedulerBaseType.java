package com.thundashop.constant;

import com.thundashop.core.bookingengine.CheckConsistencyCron;
import com.thundashop.core.bookingengine.CheckSendQuestBackScheduler;
import com.thundashop.core.eventbooking.EventBookingScheduler;
import com.thundashop.core.getshop.FetchEhfProcessor;
import com.thundashop.core.getshoplock.CheckAllOkGetShopLocks;
import com.thundashop.core.getshoplock.GetShopLogFetcherStarter;
import com.thundashop.core.getshoplock.UpdateLockList;
import com.thundashop.core.getshoplocksystem.ZwaveTriggerCheckCron;
import com.thundashop.core.gotohub.schedulers.GotoExpireBookingScheduler;
import com.thundashop.core.jomres.JomresFetchBookingScheduler;
import com.thundashop.core.jomres.JomresUpdateAvailabilityScheduler;
import com.thundashop.core.ocr.OcrProcessor;
import com.thundashop.core.ordermanager.CheckOrderCollector;
import com.thundashop.core.ordermanager.CheckOrdersNotCaptured;
import com.thundashop.core.pmsmanager.CareTakerDailyProcessor;
import com.thundashop.core.pmsmanager.CheckPmsFiveMin;
import com.thundashop.core.pmsmanager.CheckPmsProcessing;
import com.thundashop.core.pmsmanager.CheckPmsProcessingHourly;
import com.thundashop.core.pmsmanager.DailyInvoiceChecker;
import com.thundashop.core.pmsmanager.PmsMailStatistics;
import com.thundashop.core.pullserver.CheckForPullMessages;
import com.thundashop.core.scheduler.PaymentStatusCheckScheduler;
import com.thundashop.core.scormmanager.FetchScormResult;
import com.thundashop.core.trackandtrace.CheckRemovalOfFinishedRoutes;
import com.thundashop.core.wubook.WuBookHourlyProcessor;
import com.thundashop.core.wubook.WuBookManagerProcessor;

public enum GetShopSchedulerBaseType {
    BOOKING_ENGINE_PMS_PROCESSOR("pmsprocessor", "0 6,16 * * *", CheckConsistencyCron.class),
    BOOKING_ENGINE_NEW_PMS_PROCESSOR("pmsprocessor", "0 6,16 * * *",CheckConsistencyCron.class),
    GET_SHOP_LOCK_PMS_PROCESSOR("pmsprocessor", "* * * * *", CheckAllOkGetShopLocks.class),
    PMS_MANAGER_PMS_PROCESSOR("pmsprocessor", "* * * * *", CheckPmsProcessing.class),
    ORDER_CAPTURE_CHECK_PROCESSOR("ordercapturecheckprocessor", "2,7,12,17,22,27,32,37,42,47,52,57 * * * *", CheckOrdersNotCaptured.class),
    CHECK_ORDER_PAYMENT_STATUS("checkorderpaymentstatus", "*/30 * * * *", PaymentStatusCheckScheduler.class),
    ORDER_COLLECTOR("ordercollector", "*/10 * * * *", CheckOrderCollector.class),
    EHF_DATAHOTEL_DOWNLOADER("ehf_datahotel_downloader", "33 3 * * *", FetchEhfProcessor.class),
    STORE_OCR_PROCESSOR("storeocrprocessor", "20 17,3,5 * * *", OcrProcessor.class),
    JOMRES_FETCH_BOOKING("jomresFetchBooking", "*/6 * * * *", JomresFetchBookingScheduler.class),
    JOMRES_UPDATE_AVAILABILITY("jomresUpdateAvailability", "*/7 * * * *", JomresUpdateAvailabilityScheduler.class),
    FETCH_LOCK_LOCK("fetchLockLock", "22 0,2,4,6,8,10,12,14,16,18,20,22 * * *", GetShopLogFetcherStarter.class),
    CHECK_CRON_GETSHOP_LOCK_SYSTEM_MANAGER("checkCronGetShopLockSystemManager", "*/5 * * * *", ZwaveTriggerCheckCron.class),
    AUTO_EXPIRE_BOOKINGS("AutoExpireBookings", "*/5 * * * *", GotoExpireBookingScheduler.class),
    CARE_TAKER_PROCESSOR("caretakerprocessor", "1 1 08 * *", CareTakerDailyProcessor.class),
    CHECK_INVOICE_DTO_DATE("checkinvoicedtodate", "1 04 * * *", DailyInvoiceChecker.class),
    PMS_MAIL_STATS("pmsmailstats", "1 23 * * *", PmsMailStatistics.class),
    PMS_MANAGER_PMS_PROCESSOR_2("pmsprocessor2", "5 * * * *", CheckPmsProcessingHourly.class),
    PMS_MANAGER_PMS_PROCESSOR_3("pmsprocessor3", "7,13,33,53 * * * *", CheckPmsFiveMin.class),
    TRIGGER("trigger", "*/5 * * * *", CheckForPullMessages.class),
    CHECK_SCORM_RESULTS("checkScormResults", "* * * * *", FetchScormResult.class),
    CHECK_REMOVAL_OF_ROUTES("checkRemovalOfRoutes", "0 * * * *", CheckRemovalOfFinishedRoutes.class),
    WUBOOK_PROCESSOR("wubookprocessor", "* * * * *", WuBookManagerProcessor.class),
    WUBOOK_PROCESSOR_2("wubookprocessor2", "1 5,12,22 * * *", WuBookHourlyProcessor.class),
    EVENT_QUESTBACK_CHECKED("event_questback_checked", "0 * * * *", CheckSendQuestBackScheduler.class),
    EVENT_BOOKING_SCHEDULER("event_booking_scheduler", "", EventBookingScheduler.class),
    PMS_PROCESSOR_LOCK("pmsprocessor_lock", "30 23,04 * * *", UpdateLockList.class);

    public final String name;
    public final String time;
    public final Class className;

    GetShopSchedulerBaseType(String name, String time, Class className) {
        this.name = name;
        this.time = time;
        this.className=className;
    }


}
