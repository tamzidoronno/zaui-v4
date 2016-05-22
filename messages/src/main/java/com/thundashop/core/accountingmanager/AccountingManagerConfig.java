
package com.thundashop.core.accountingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class AccountingManagerConfig extends DataCommon {
    public List<Integer> statesToInclude = new ArrayList();
    
    /* ftp accounting stuff */
    public String username = "";
    public String password = "";
    public String hostname = "";
    public String path = "";
    public String extension = "";
    
    /* ftp creditor stuff */
    public String vendor = "svea";
    public String creditor_username = "";
    public String creditor_password = "";
    public String creditor_hostname = "";
    public String creditor_path = "";

    
}



