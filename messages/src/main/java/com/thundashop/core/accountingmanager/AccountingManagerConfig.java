
package com.thundashop.core.accountingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class AccountingManagerConfig extends DataCommon {
    public List<Integer> statesToInclude = new ArrayList();
    
    /* ftp stuff */
    public String username = "";
    public String password = "";
    public String hostname = "";
    public String path = "";
    public String extension = "";
    
}



