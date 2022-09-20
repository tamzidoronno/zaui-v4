package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IntervalResultEntry implements Serializable {
    public Integer count = 0;
    public Long time;
    public List<String> bookingIds;
    public String name = "";
    public List<String> virtuallyAssigned;
    public String state = "";
    public String roomId = "";
    public String typeId = "";
    public PmsConference conference;
}
