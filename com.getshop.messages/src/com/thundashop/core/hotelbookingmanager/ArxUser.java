package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArxUser extends DataCommon {
    public String userId;
    public String firstName;
    public String lastName;
    public Date startDate;
    public Date endDate;
    public Integer code;
    public Boolean needUpdate = true;
    public List<String> doorsToAccess = new ArrayList();
    public String reference;
}
