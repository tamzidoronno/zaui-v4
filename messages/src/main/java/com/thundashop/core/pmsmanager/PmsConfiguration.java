package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.HashMap;

public class PmsConfiguration extends DataCommon {

    public static class PmsBookingTimeInterval {
        public static Integer HOURLY = 1;
        public static Integer DAILY = 2;
    }
    
    public HashMap<String, String> emails = new HashMap();
    public HashMap<String, String> emailTitles = new HashMap();
    public HashMap<String, String> smses = new HashMap();
    public HashMap<String, String> adminmessages = new HashMap();
    public HashMap<String, String> defaultMessage = new HashMap();
    public String emailTemplate = "{content}";
    
    public HashMap<String, String> contracts = new HashMap();
    
    /* other configurations */
    public boolean needConfirmation = false;
    public boolean requirePayments = false;
    public boolean needToAgreeOnContract = false;
    public boolean exposeUnsecureBookings = false;
    public boolean autoconfirmRegisteredUsers = false;
    public boolean prepayment = false;
    public boolean payAfterBookingCompleted = false;
    public Integer createOrderAtDayInMonth = 0;
    public Integer minStay = 1;
    public boolean supportMoreDates = false;
    public boolean isItemBookingInsteadOfTypes = false;
    public boolean autoExtend = false;
    public boolean autoCreateInvoices = false;
    public boolean copyEmailsToOwnerOfStore = false;
    boolean substractOneDayOnOrder = false;


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

    
}
