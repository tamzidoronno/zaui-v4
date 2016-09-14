package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PmsConfiguration extends DataCommon {

    public static class PmsBookingTimeInterval {
        public static Integer HOURLY = 1;
        public static Integer DAILY = 2;
        public static Integer WEEKLY = 3;
        public static Integer MONTHLY = 4;
    }
    
    public HashMap<String, String> emails = new HashMap();
    public HashMap<String, String> emailTitles = new HashMap();
    public HashMap<String, String> smses = new HashMap();
    public HashMap<String, String> adminmessages = new HashMap();
    public HashMap<String, String> defaultMessage = new HashMap();
    public String emailTemplate = "{content}";
    public HashMap<Integer, PmsBudget> budget = new HashMap();
    
    public HashMap<String, String> contracts = new HashMap();
    public String fireinstructions = "";
    public String otherinstructions = "";
    
    /* other configurations */
    public boolean needConfirmation = false;
    public boolean needToAgreeOnContract = false;
    public boolean exposeUnsecureBookings = false;
    public boolean autoconfirmRegisteredUsers = false;
    public Integer minStay = 1;
    public Integer defaultNumberOfDaysBack = 3;
    public Integer hourOfDayToStartBoarding = 12;
    public boolean supportMoreDates = false;
    public boolean isItemBookingInsteadOfTypes = false;
    public boolean autoExtend = false;
    public boolean copyEmailsToOwnerOfStore = false;
    public boolean ignoreTimeIntervalsOnNotification = false;
    public boolean hasNoEndDate = false;
    public boolean autoDeleteUnpaidBookings = false;
    public boolean deleteAllWhenAdded = false;
    public boolean manualcheckincheckout = false;
    public boolean markBookingsWithNoOrderAsUnpaid = false;
    public boolean fastCheckIn = false;
    public HashMap<Integer, PmsBookingAddonItem> addonConfiguration = new HashMap();
    public HashMap<String, CleaningStatistics> cleaningPriceConfig = new HashMap();
    public HashMap<String, List<String>> emailsToNotify = new HashMap();
    public HashMap<String, Double> extraCleaningCost = new HashMap();
    public String bookingProfile = "hotel";

    /* Invoice creation options */
    public boolean supportRemoveWhenFull = false;
    public boolean autoSendInvoice = false;
    public boolean autoCreateInvoices = false;
    public boolean usePriceMatrixOnOrder = false;
    public Integer whenInfinteDateFirstOrderTimeUnits = 1;
    public boolean autoGenerateChangeOrders = false;
    public boolean runAutoPayWithCard = false;
    public Integer createOrderAtDayInMonth = 0;
    public boolean prepayment = false;
    public boolean payAfterBookingCompleted = false;
    public boolean requirePayments = false;
    public boolean updatePriceWhenChangingDates = false;
    public Integer prepaymentDaysAhead = -1;
    public Integer increaseUnits = -1;
    boolean substractOneDayOnOrder = false;
    boolean includeGlobalOrderCreationPanel = false;
    public boolean autoSendToCreditor = false;
    public boolean forceRequiredFieldsForEditors = false;
    public boolean automarkInvoicesAsPaid = false;
    public boolean notifyGetShopAboutCriticalTransactions = false;
    

    public Integer bookingTimeInterval = 1; //1 = hourly, 2 = daily
    public String defaultStart = "15:00";
    public String defaultEnd = "12:00";
    public String extraField = "";
    public String smsName = "GetShop";
    
    
    public String locktype = "";
    public String arxHostname = "";
    public String arxUsername = "";
    public String arxPassword = "";
    public String arxCardFormat = "";
    public String arxCardFormatsAvailable = "";
    public Integer codeSize = 4;
    public boolean keepDoorOpenWhenCodeIsPressed = false;
    public String closeAllDoorsAfterTime = "22:00";

    
    //Cleaning options
    public Integer cleaningInterval = 0;
    public HashMap<Integer, Boolean> cleaningDays = new HashMap();
    public Integer numberOfCheckoutCleanings = 0;
    public Integer numberOfIntervalCleaning = 0;
    public boolean cleaningNextDay = false;
    boolean unsetCleaningIfJustSetWhenChangingRooms = false;
    
    /* Mail settings */
    public String senderName = "";
    public String senderEmail = "";
    public String sendAdminTo = "";
    
    /* Wubook settings */
    public String wubookusername = "";
    public String wubookpassword = "";
    public String wubookproviderkey = "";
    public String wubooklcode = "";
    
    
    public HashMap<String, PmsChannelConfig> channelConfiguration = new HashMap();
    
    public boolean isGetShopHotelLock() {
        if(locktype != null && !locktype.isEmpty() && locktype.equals("getshophotellock")) {
            return true;
        }
        return false;
    }

    boolean channelExists(String channel) {
        return channelConfiguration.containsKey(channel);
    }
    
    HashMap<String, PmsChannelConfig> getChannels() {
        return channelConfiguration;
    }
    
    public PmsChannelConfig getChannelConfiguration(String channel) {
        if(!channelConfiguration.containsKey(channel)) {
            PmsChannelConfig config = new PmsChannelConfig();
            config.channel = channel;
            channelConfiguration.put(channel, config);
        }
        return channelConfiguration.get(channel);
    }
    
    void removeChannel(String channel) {
        channelConfiguration.remove(channel);
    }
    
    PmsBookingAddonItem getAddonFromProductId(String productId) {
        for(PmsBookingAddonItem item : addonConfiguration.values()) {
            if(item != null && item.productId != null && item.productId.equals(productId)) {
                return item;
            }
        }
        return null;
    }
        
    boolean hasAddons() {
        for(PmsBookingAddonItem item : addonConfiguration.values()) {
            if(item.isActive) {
                return true;
            }
        }
        return false;
    }

    public boolean hasLockSystem() {
        return (arxHostname != null && !arxHostname.isEmpty());
    }

    
    boolean isArx() {
        if(!hasLockSystem()) {
            return true;
        }
        if(locktype != null && !locktype.isEmpty() && locktype.equals("arx")) {
            return true;
        }
        
        return false;
    }
}
