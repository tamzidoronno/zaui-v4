package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.List;

public class SecurityListSchedulerConfig {
    /**
     * Receiver email list of security guest lists.
     */
    private List<String> emails = new ArrayList<>();
    /**
     * Hour of the day in 24 hour format.
     */
    private Integer schedule = 3; 
}
