
package com.thundashop.core.accountingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountingManagerConfig extends DataCommon {
    
    
    public List<Integer> statesToInclude = new ArrayList();
    private HashMap<String, TransferFtpConfig> configrations = new HashMap();
    
    /* ftp accounting stuff */
    public String vendor = "svea";
    public String username = "";
    public String password = "";
    public String hostname = "";
    public String path = "";
    public String invoice_path = "";
    public String extension = "";
    public boolean useSftp = false;
    public boolean useActiveMode = false;
    public boolean transferAllUsersConnectedToOrders = false;
    public Integer port = 21;
    
    public TransferFtpConfig getCreditorConfig() {
        TransferFtpConfig config = configrations.get("creditor");
        if(config == null) {
            config = new TransferFtpConfig();
        }
        return config;
    }
    
    public TransferFtpConfig getBComConfig() {
        TransferFtpConfig config = configrations.get("bookingcomratemanager");
        if(config == null) {
            config = new TransferFtpConfig();
        }
        return config;
    }
    
}



