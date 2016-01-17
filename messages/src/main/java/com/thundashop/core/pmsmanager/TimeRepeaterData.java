package com.thundashop.core.pmsmanager;

import java.util.Date;

public class TimeRepeaterData {
    /* 0, daily, 1. Weekly, 2. Monthly */
    public static class RepeatPeriodeTypes {
        public static Integer daily = 0;
        public static Integer weekly = 1;
        public static Integer monthly = 2;
    }
    
    Integer repeatPeride = TimeRepeaterData.RepeatPeriodeTypes.daily;
    
    TimeRepeaterDateRange firstEvent;
    
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
