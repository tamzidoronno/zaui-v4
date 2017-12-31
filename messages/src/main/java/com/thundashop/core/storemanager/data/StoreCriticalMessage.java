package com.thundashop.core.storemanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

public class StoreCriticalMessage extends DataCommon {
    public String message;
    public Date seenWhen;
    public String seenByUser;
}
