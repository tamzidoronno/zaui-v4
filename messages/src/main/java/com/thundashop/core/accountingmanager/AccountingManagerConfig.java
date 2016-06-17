
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
    public boolean useSftp = false;
    public boolean useActiveMode = false;
    public Integer port = 21;
    
    /* ftp creditor stuff */
    public String vendor = "svea";
    public String creditor_username = "";
    public String creditor_password = "";
    public String creditor_hostname = "";
    public String creditor_path = "";
    public Integer creditor_port = 21;
    public boolean creditor_useSftp = false;
    public boolean creditor_useActiveMode = false;
}



