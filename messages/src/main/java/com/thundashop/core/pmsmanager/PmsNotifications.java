package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.HashMap;

public class PmsNotifications extends DataCommon {
    public HashMap<String, String> emails = new HashMap();
    public HashMap<String, String> smses = new HashMap();
    public HashMap<String, String> adminmessages = new HashMap();
}
