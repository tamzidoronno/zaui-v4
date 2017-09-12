package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
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
    public HashMap<String, String> priceCalcPlugins = new HashMap();
    public String emailTemplate = "{content}";
    public HashMap<Integer, PmsBudget> budget = new HashMap();
    public boolean sendMessagesRegardlessOfPayments = false;
    
    public HashMap<String, String> contracts = new HashMap();
    public String fireinstructions = "";
    public String otherinstructions = "";
    
    /* other configurations */
    public boolean needConfirmation = false;
    public boolean needToAgreeOnContract = false;
    public boolean exposeUnsecureBookings = false;
    public boolean autoconfirmRegisteredUsers = false;
    public boolean avoidRandomRoomAssigning = false;
    public Integer numberOfHoursToExtendLateCheckout = 3;
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
    public boolean denyUpdateUserWhenTransferredToAccounting = false;
    public boolean functionsEnabled = false;
    public boolean needConfirmationInWeekEnds = false;
    public Date closedUntil = null;
    public HashMap<Integer, PmsBookingAddonItem> addonConfiguration = new HashMap();
    public HashMap<String, PmsInventory> inventoryList = new HashMap();
    public HashMap<String, CleaningStatistics> cleaningPriceConfig = new HashMap();
    public HashMap<String, List<String>> emailsToNotify = new HashMap();
    public HashMap<String, Double> extraCleaningCost = new HashMap();
    public HashMap<String, PmsMobileView> mobileViews = new HashMap();
    public HashMap<String, List<String>> mobileViewRestrictions = new HashMap();
    public String bookingProfile = "hotel";
    public boolean markDirtyEvenWhenCodeNotPressed = false;

    /* Invoice creation options */
    public boolean fastOrderCreation = false;
    public boolean autoSendPaymentReminder;
    public boolean supportRemoveWhenFull = false;
    public boolean supportDiscounts = false;
    public boolean autoSendInvoice = false;
    public boolean autoMarkCreditNotesAsPaidFor = false;
    public boolean autoCreateInvoices = false;
    public boolean usePriceMatrixOnOrder = false;
    public boolean splitOrderIntoMonths = false;
    public boolean orderEndsFirstInMonth = false;
    public Integer whenInfinteDateFirstOrderTimeUnits = 1;
    public boolean autoGenerateChangeOrders = false;
    public boolean grantAccessEvenWhenNotPaid = false;
    public boolean runAutoPayWithCard = false;
    public int numberOfDaysToTryToPayWithCardAfterStayOrderHasBeenCreated = 1;
    public int warnWhenOrderNotPaidInDays = 3;
    public Integer chargeOrderAtDayInMonth = 0;
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
    public boolean autoSumarizeCartItems = false;
    public int numberOfDaysToSendPaymentLinkAheadOfStay = 2;
    public boolean ignoreRoomToEndDate = false;
    public boolean createVirtualOrders = false;
    public boolean enableCoveragePrices = false;

    public Integer bookingTimeInterval = 1; //1 = hourly, 2 = daily
    public String defaultStart = "15:00";
    public String defaultEnd = "12:00";
    public String extraField = "";
    public String smsName = "GetShop";
    
    //Lock system
    public HashMap<String, PmsLockServer> lockServerConfigs = new HashMap();
    public boolean useNewQueueCheck = false;
    
    public PmsLockServer getDefaultLockServer() {
        PmsLockServer defaultsv = lockServerConfigs.get("default");
        if(defaultsv == null) {
            defaultsv = new PmsLockServer();
        }
        return defaultsv;
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

    
    //Cleaning options
    public Integer closeRoomNotCleanedAtHour = 16;
    public Integer cleaningInterval = 0;
    public HashMap<Integer, Boolean> cleaningDays = new HashMap();
    public Integer numberOfCheckoutCleanings = 0;
    public Integer numberOfIntervalCleaning = 0;
    public boolean cleaningNextDay = false;
    boolean unsetCleaningIfJustSetWhenChangingRooms = false;
    boolean automaticallyCloseRoomIfDirtySameDay = false;
    public boolean autoAddMissingItemsToRoom = false;
    public boolean autoNotifyCareTakerForMissingInventory = false;
    public boolean whenCleaningEndStayForGuestCheckinOut = false;

    
    /* Mail settings */
    public String senderName = "";
    public String senderEmail = "";
    public String sendAdminTo = "";
    
    /* Wubook settings */
    public String wubookusername = "";
    @Administrator
    public String wubookpassword = "";
    public String wubookproviderkey = "";
    public String wubooklcode = "";
    public int numberOfRoomsToRemoveFromBookingCom = 1;
    public boolean usePricesFromChannelManager = false;
    public boolean useGetShopPricesOnExpedia = false;
    public boolean ignoreNoShow = false;

    
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
        HashMap<Integer, PmsBookingAddonItem> newAddons = new HashMap();
        for(PmsBookingAddonItem item : addonConfiguration.values()) {
            newAddons.put(item.addonType, item);
        }
        addonConfiguration = newAddons;
        
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
