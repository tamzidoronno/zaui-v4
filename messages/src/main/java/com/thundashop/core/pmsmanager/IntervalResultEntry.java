package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IntervalResultEntry implements Serializable {
    public Integer count = 0;
    public Long time;
    public List<String> bookingIds;
}
