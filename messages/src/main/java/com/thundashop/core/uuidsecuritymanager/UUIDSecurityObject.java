package com.thundashop.core.uuidsecuritymanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class UUIDSecurityObject extends DataCommon {
    String uuid;
    
    public List<String> readAccessUsers = new ArrayList();
    public List<String> writeAccessUsers = new ArrayList();

    boolean isEmpty() {
        return readAccessUsers.isEmpty() && writeAccessUsers.isEmpty();
    }
}
