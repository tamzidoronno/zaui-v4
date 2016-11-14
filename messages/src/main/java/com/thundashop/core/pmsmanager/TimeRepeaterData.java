package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class TimeRepeaterData implements Serializable {
    /* 0, daily, 1. Weekly, 2. Monthly, 3. From start to end */
    public static class RepeatPeriodeTypes {
        public static Integer daily = 0;
        public static Integer weekly = 1;
        public static Integer monthly = 2;
        public static Integer continously = 3;
    }
    
    Integer repeatPeride = TimeRepeaterData.RepeatPeriodeTypes.daily;
    
    TimeRepeaterDateRange firstEvent;
    
    public String repeaterId = UUID.randomUUID().toString();
    
    Date endingAt = null;
    
    /* Interval to repeat between */
    Integer repeatEachTime = 1;
    
    /* When repeatPeride = 1 */
    boolean repeatMonday = false;
    boolean repeatTuesday = false;
    boolean repeatWednesday = false;
    boolean repeatThursday = false;
    boolean repeatFriday = false;
    boolean repeatSaturday = false;
    boolean repeatSunday = false;
    
    /* When repeatPeride = 2 */
    boolean repeatAtDayOfWeek = false;

}
