package com.thundashop.core.accountingmanager;

import java.io.Serializable;

public class TransferFtpConfig implements Serializable {
        public String username = "";
        public String password = "";
        public String hostname = "";
        public String path = "";
        public String extension = "";
        public boolean useSftp = false;
        public boolean useActiveMode = false;
        public Integer port = 21;
        
        //Commaseparated list of bookingengines (if nessesary with more).
        public String engineNames = "";
        
    }