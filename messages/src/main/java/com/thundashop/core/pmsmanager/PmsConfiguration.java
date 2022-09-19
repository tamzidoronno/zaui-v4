package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.mongodb.morphia.annotations.Transient;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.data.Store;

public class PmsConfiguration extends DataCommon {

    @Transient
    private Store store = null;

    @Transient
    public boolean isDeactivated = false;
    
    @Transient
    private boolean isPikStore = false;
    
    boolean requestForPmhr = false;
    boolean ignorePmhr = false;
    boolean checkedForErrorsInBookingComPrepaidTwo = false;
    
    void setTimeZone(Store store) {
        this.store = store;
    }
    
    void setIsPikStore(boolean isPikStore) {
        this.isPikStore = isPikStore;
    }

    public Date getCurrentTimeInTimeZone() {
        return store.getCurrentTimeInTimeZone();
    }
    
    private Date calculcateTimeZone(String toCheck, Date toDiff) {
        String[] splitted = toCheck.split(":");
        Integer hour = new Integer(splitted[0]);
        Integer minute = new Integer(splitted[1]);
        
        int diff = getTimeDifferenceInTimeZone();
        Calendar cal = Calendar.getInstance();
        cal.setTime(toDiff);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.add(Calendar.SECOND, diff);
        
        return cal.getTime();

    }

    /**
     * Difference in seconds between our timezone and the customers timezone.
     * @return 
     */
    public int getTimeDifferenceInTimeZone() {
        Date here = new Date();
        Date timezone = getCurrentTimeInTimeZone();
        long diff = (here.getTime() - timezone.getTime());
        return (int)(diff/1000);
    }

    Date convertToTimeZone(Date timing) {
        int diff = getTimeDifferenceInTimeZone();
        Calendar cal = Calendar.getInstance();
        cal.setTime(timing);
        cal.add(Calendar.SECOND, diff);
        return cal.getTime();
    }

    void setHasAccessControl(boolean hasLockSystemActive) {
        this.hasDoorLockSystem = hasLockSystemActive;
    }

    void addAddonConfiguration(Product prod) {
        int type = -1;
        for(PmsBookingAddonItem item : addonConfiguration.values()) {
            if(item.addonType > type) {
                type = item.addonType;
            }
        }
        type++;
        
        PmsBookingAddonItem item = new PmsBookingAddonItem();
        item.price = prod.price;
        item.setName(prod);
        item.count = 1;
        item.isSingle = true;
        item.productId = prod.id;
        item.addonType = type;
        addonConfiguration.put(type, item);
    }

    public static class PmsBookingTimeInterval {
        public static Integer HOURLY = 1;
        public static Integer DAILY = 2;
        public static Integer WEEKLY = 3;
        public static Integer MONTHLY = 4;
    }
    
    public HashMap<String, String> emails = new HashMap<>();
    public HashMap<String, String> emailTitles = new HashMap<>();
    public HashMap<String, String> smses = new HashMap<>();
    public HashMap<String, String> adminmessages = new HashMap<>();
    public HashMap<String, String> defaultMessage = new HashMap<>();
    public HashMap<String, String> priceCalcPlugins = new HashMap<>();
    public String emailTemplate = "{content}";
    public HashMap<Integer, PmsBudget> budget = new HashMap<>();
    public boolean sendMessagesRegardlessOfPayments = false;
    public List<TimeRepeaterData> closedOfPeriode = new ArrayList<>();
    public List<PmsBlockedUser> blockedUsers = new ArrayList<>();
    
    public HashMap<String, String> contracts = new HashMap<>();
    public String fireinstructions = "";
    public String otherinstructions = "";
    public String cleaninginstruction = "";
    public String maintanceinstruction = "";
    public String bookingTag = "";
    
    
    /* other configurations */
    public boolean avoidSendingBookingConfigurationsToOTA = false;
    public boolean sendToBookerIfGuestIsEmpty = false;
    public boolean conferenceSystemActive = false;
    public boolean needConfirmation = false;
    public boolean needToAgreeOnContract = false;
    public boolean exposeUnsecureBookings = false;
    public boolean autoconfirmRegisteredUsers = false;
    public boolean avoidRandomRoomAssigning = false;
    public Integer numberOfHoursToExtendLateCheckout = 3;
    public Integer minStay = 1;
    public Integer autosendPassportQuestion = -1;
    public Integer defaultNumberOfDaysBack = 3;
    private Integer hourOfDayToStartBoarding = 12;
    public boolean supportMoreDates = false;
    public boolean isItemBookingInsteadOfTypes = false;
    public boolean autoExtend = false;
    public boolean copyEmailsToOwnerOfStore = false;
    public boolean ignoreTimeIntervalsOnNotification = false;
    public boolean hasNoEndDate = false;
    public boolean autoDeleteUnpaidBookings = false;
    public boolean deleteAllWhenAdded = false;
    public boolean manualcheckincheckout = false;
    private boolean markBookingsWithNoOrderAsUnpaid = false;
    public boolean fastCheckIn = false;
    public boolean denyUpdateUserWhenTransferredToAccounting = false;
    public boolean functionsEnabled = false;
    public boolean needConfirmationInWeekEnds = false;
    public Date closedUntil = null;
    public HashMap<Integer, PmsBookingAddonItem> addonConfiguration = new HashMap<>();
    public HashMap<String, PmsInventory> inventoryList = new HashMap<>();
    public HashMap<String, CleaningStatistics> cleaningPriceConfig = new HashMap<>();
    public HashMap<String, List<String>> emailsToNotify = new HashMap<>();
    public HashMap<String, Double> extraCleaningCost = new HashMap<>();
    public HashMap<String, PmsMobileView> mobileViews = new HashMap<>();
    public HashMap<String, List<String>> mobileViewRestrictions = new HashMap<>();
    public String bookingProfile = "hotel";
    public boolean markDirtyEvenWhenCodeNotPressed = false;
    public boolean doNotRecommendBestPrice = false;
    public boolean automaticallyCheckInOutGuests = false;
    public String zReportProcessingTime = null;

    /* Invoice creation options */
    public boolean fastOrderCreation = false;
    public boolean ignoreGuestInformation = false;
    public boolean autoSendPaymentReminder;
    public boolean supportRemoveWhenFull = false;
    public boolean supportDiscounts = false;
    public boolean autoSendInvoice = false;
    public boolean autoMarkCreditNotesAsPaidFor = false;
    public boolean autoCreateInvoices = false;
    private boolean usePriceMatrixOnOrder = false;
    public boolean splitOrderIntoMonths = false;
    public boolean orderEndsFirstInMonth = false;
    public Integer whenInfinteDateFirstOrderTimeUnits = 1;
    public boolean autoGenerateChangeOrders = false;
    public boolean grantAccessEvenWhenNotPaid = false;
    public boolean runAutoPayWithCard = false;
    public int numberOfDaysToTryToPayWithCardAfterStayOrderHasBeenCreated = 1;
    public int warnWhenOrderNotPaidInDays = 3;
    public Integer chargeOrderAtDayInMonth = 0;
    private boolean prepayment = false;
    private boolean payAfterBookingCompleted = false;
    private boolean requirePayments = false;
    public boolean updatePriceWhenChangingDates = false;
    public Integer prepaymentDaysAhead = -1;
    public Integer increaseUnits = -1;
    boolean substractOneDayOnOrder = false;
    boolean includeGlobalOrderCreationPanel = false;
    public boolean autoSendToCreditor = false;
    public boolean forceRequiredFieldsForEditors = false;
    public boolean automarkInvoicesAsPaid = false;
    public boolean notifyGetShopAboutCriticalTransactions = false;
    public boolean autoSumarizeCartItems = false;
    public int numberOfDaysToSendPaymentLinkAheadOfStay = 2;
    public int ignorePaymentWindowDaysAheadOfStay = 0;
    public boolean ignoreRoomToEndDate = false;
    public boolean createVirtualOrders = false;
    public boolean enableCoveragePrices = false;
    public int warnIfOrderNotPaidFirstTimeInHours = 0;

    public Integer bookingTimeInterval = 1; //1 = hourly, 2 = daily
    private String defaultStart = "15:00";
    private String defaultEnd = "12:00";
    public String extraField = "";
    public String smsName = "GetShop";
    public Integer childMaxAge = 6;
    public String defaultRegistrationLanguage = "";
    
    
    //Lock system
    public HashMap<String, PmsLockServer> lockServerConfigs = new HashMap<>();
    public boolean useNewQueueCheck = false;

    public SecurityListSchedulerConfig securityListSchedule = new SecurityListSchedulerConfig();
    
    public PmsLockServer getDefaultLockServer() {
        PmsLockServer defaultsv = lockServerConfigs.get("default");
        if(defaultsv == null) {
            defaultsv = new PmsLockServer();
        }
        return defaultsv;
    }
    
    public boolean getUsePriceMatrixOnOrder() {
        if(isPikStore) {
            return true;
        }
        return usePriceMatrixOnOrder;
    }
    
    public boolean getRequirePayments() {
        if(isPikStore) {
            return true;
        }
        return requirePayments;
    }
    
    public boolean payAfterBookingCompleted() {
        if(isPikStore) {
            return true;
        }
        return payAfterBookingCompleted;
    }
    
    public boolean getMarkBookingsWithNoOrderAsUnpaid() {
        if(isPikStore) {
            return true;
        }
        return markBookingsWithNoOrderAsUnpaid;
    }
    
    public boolean getPrepayment() {
        if(isPikStore) {
            return true;
        }
        return prepayment;
    }
    
    public PmsLockServer getLockServer(String serverSource) {
        if(serverSource == null || serverSource.trim().isEmpty()) {
            serverSource = "default";
        }
        PmsLockServer retLock = lockServerConfigs.get(serverSource);
        
        finalizePmsLockServer(retLock);
        return retLock;
    }
    
    public PmsLockServer getLockServerBasedOnHostname(String hostName) {
        
        for (PmsLockServer server : lockServerConfigs.values()) {
            if (server.arxHostname != null && server.arxHostname.equals(hostName)) {
                finalizePmsLockServer(server);
                return server;
            }
        }
        
        return null;
    }

    
    private String locktype = "";
    private String arxHostname = "";
    @Administrator
    private String arxUsername = "";
    @Administrator
    private String arxPassword = "";
    private String arxCardFormat = "";
    private String arxCardFormatsAvailable = "";
    private Integer codeSize = 4;
    private boolean keepDoorOpenWhenCodeIsPressed = false;
    private String closeAllDoorsAfterTime = "22:00";
    public boolean hasDoorLockSystem = false;

    
    //Cleaning options
    private Integer closeRoomNotCleanedAtHour = 16;
    public Integer cleaningInterval = 0;
    public HashMap<Integer, Boolean> cleaningDays = new HashMap<>();
    public Integer numberOfCheckoutCleanings = 0;
    public Integer numberOfIntervalCleaning = 0;
    public boolean cleaningNextDay = false;
    boolean unsetCleaningIfJustSetWhenChangingRooms = false;
    boolean automaticallyCloseRoomIfDirtySameDay = false;
    public boolean autoAddMissingItemsToRoom = false;
    public boolean autoNotifyCareTakerForMissingInventory = false;
    private boolean whenCleaningEndStayForGuestCheckinOut = false;

    public boolean getWhenCleaningEndStayForGuestCheckinOut() {
        if(isPikStore) {
            return true;
        }
        return whenCleaningEndStayForGuestCheckinOut;
    }
    
    /* Mail settings */
    public String senderName = "";
    public String senderEmail = "";
    public String sendAdminTo = "";
    
    /* Wubook settings */
    public boolean wubookAutoCharging = false;
    public Integer autochargeCardDaysBefore = -1;
    public String wubookusername = "";
    @Administrator
    public String wubookpassword = "";
    public String wubookproviderkey = "";
    public String wubooklcode = "";
    public boolean wubookcallbackactivated = false;
    public int numberOfRoomsToRemoveFromBookingCom = 1;
    public HashMap<String, Integer> maxNumberForEachCategory = new HashMap<>();
    public boolean usePricesFromChannelManager = false;
    public boolean useGetShopPricesOnExpedia = false;
    public boolean ignoreNoShow = false;
    public Integer increaseByPercentage = 0;
    public Integer daysAllowedInTheFuture = 310;
    
    public String tripTeaseHotelId = "";

    
    public HashMap<String, PmsChannelConfig> channelConfiguration = new HashMap<>();
    
    public boolean isGetShopHotelLock() {
        if(locktype != null && !locktype.isEmpty() && locktype.equals("getshophotellock")) {
            return true;
        }
        return false;
    }
    
    /**
     * Timezone related data.
     */
    public Integer getBoardingHour(Date dateToCheckAgainst) {
        return hourOfDayToStartBoarding - store.getTimeZoneDifferenceInHours(dateToCheckAgainst);
    }
    public String getDefaultEnd() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDefaultEnd(new Date()));
        
        int minute = cal.get(Calendar.MINUTE);
        String minuteText = minute + "";
        if(minute < 10) {
            minuteText = "0" + minute;
        }
        
        return cal.get(Calendar.HOUR_OF_DAY) + ":" + minuteText;
    }
    public String getDefaultStart() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDefaultStart(new Date()));
        
        int minute = cal.get(Calendar.MINUTE);
        String minuteText = minute + "";
        if(minute < 10) {
            minuteText = "0" + minute;
        }
        return cal.get(Calendar.HOUR_OF_DAY) + ":" + minuteText;
    }
    
    /**
     * Not localized by timezone.
     * @return 
     */
    public String getDefaultStartRaw() {
        return defaultStart;
    }
    
    public Date getDefaultEnd(Date inputDate) {
        return calculcateTimeZone(defaultEnd, inputDate);
    }
    public Date getDefaultStart(Date inputDate) {
        return calculcateTimeZone(defaultStart, inputDate);
    }
    public Integer getCloseRoomNotCleanedAtHour(Date dateToCheckAgainst) {
        return closeRoomNotCleanedAtHour + store.getTimeZoneDifferenceInHours(dateToCheckAgainst);
    }
    
    
    boolean channelExists(String channel) {
        return channelConfiguration.containsKey(channel);
    }
    
    public boolean isPIKTime(Date storeDate) {
        return true;
    }
    
    public void finalize() {
        for(PmsBookingAddonItem item : addonConfiguration.values()) {
            int next = 100;
            if(item.addonType == null) {
                for(PmsBookingAddonItem item2 : addonConfiguration.values()) {
                    if(item2.addonType != null && item2.addonType >= next) {
                        next = item2.addonType + 1;
                    }
                }
                item.addonType = next;
            }
        }
        HashMap<Integer, PmsBookingAddonItem> newAddons = new HashMap<>();
        for(PmsBookingAddonItem item : addonConfiguration.values()) {
            newAddons.put(item.addonType, item);
        }
        
        
        List<String> replace = new ArrayList<>();
        for(String key : channelConfiguration.keySet()) {
            if(key.contains(".")) {
                replace.add(key);
            }
        }
        
        for(String rep : replace) {
            PmsChannelConfig config = channelConfiguration.get(rep);
            channelConfiguration.remove(rep);
            rep = rep.replace(".", "");
            channelConfiguration.put(rep, config);
        }
        
        addonConfiguration = newAddons;
        
    }
    
    HashMap<String, PmsChannelConfig> getChannels() {
        return channelConfiguration;
    }
    
    public PmsChannelConfig getChannelConfiguration(String channel) {
        channel = channel.replace(".", "");
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
    
    void convertLockConfigToDefault() {
        PmsLockServer server = new PmsLockServer();
        server.locktype = locktype;
        server.arxHostname = arxHostname;
        server.arxUsername = arxUsername;
        server.arxPassword = arxPassword;
        server.arxCardFormat = arxCardFormat;
        server.arxCardFormatsAvailable = arxCardFormatsAvailable;
        server.codeSize = codeSize;
        server.keepDoorOpenWhenCodeIsPressed = keepDoorOpenWhenCodeIsPressed;
        server.closeAllDoorsAfterTime = closeAllDoorsAfterTime;
        lockServerConfigs.put("default", server);
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
    
    private void finalizePmsLockServer(PmsLockServer retLock) {
        for (String serverSource : lockServerConfigs.keySet()) {
            if (lockServerConfigs.get(serverSource).equals(retLock)) {
                retLock.serverSource = serverSource;
            }
        }
    }

}
