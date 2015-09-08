package com.thundashop.core.getshop.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class Partner extends DataCommon {
    public String partnerId;
    public String userId;
    public List<String> availableApplications = new ArrayList();
}
