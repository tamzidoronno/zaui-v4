package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import org.mongodb.morphia.annotations.Transient;

public class PmsConference extends DataCommon {
    public String meetingTitle = "";
    public String contact = "";
    public String source = "";
    public String createdByUserId = "";
    public String forUser = "";
    public String memo = "";
    public Date conferenceDate = null;
    
    public String contactName = "";
    public String contactEmail = "";
    public String contactPhone = "";
    
    public int attendeeCount = 0;
    
    public String state = "";
}
