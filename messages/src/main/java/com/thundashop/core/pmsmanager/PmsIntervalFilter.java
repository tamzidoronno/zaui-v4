package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;

public class PmsIntervalFilter implements Serializable {
    Date start;
    Date end;
    Integer interval;
}
