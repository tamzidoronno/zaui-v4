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
    
    /*
    1. Pay when booking.
    2. Pay on confirmation
    3. Pay at check in date.
    4. Pay after stay
    */
    public Integer whenToPay = 1;
    public String arxHostname = "";
    public String arxUsername = "";
    public String arxPassword = "";
}
