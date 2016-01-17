package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.HashMap;

public class PmsConfiguration extends DataCommon {
    public HashMap<String, String> emails = new HashMap();
    public HashMap<String, String> emailTitles = new HashMap();
    public HashMap<String, String> smses = new HashMap();
    public HashMap<String, String> adminmessages = new HashMap();
    public String emailTemplate = "";
    
    public HashMap<String, String> contracts = new HashMap();
    
    /* other configurations */
    public boolean needConfirmation = true;
    public boolean requirePayments = true;
    public boolean needToAgreeOnContract = true;
    public boolean exposeUnsecureBookings = false;
    public boolean autoconfirmRegisteredUsers = false;
    public Integer cleaningInterval = 0;
    public boolean prepayment = true;
    public Integer createOrderAtDayInMonth = 0;
    public boolean supportMoreDates = true;
    
    public String arxHostname = "";
    public String arxUsername = "";
    public String arxPassword = "";
}
