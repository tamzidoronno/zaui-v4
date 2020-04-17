package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TimeRepeaterData implements Serializable {

    /* 0, daily, 1. Weekly, 2. Monthly, 3. From start to end */
    public static class RepeatPeriodeTypes {
        public static Integer daily = 0;
        public static Integer weekly = 1;
        public static Integer monthly = 2;
        public static Integer continously = 3;
    }
    public static class TimePeriodeType {
        public static Integer open = 0;
        public static Integer closed = 1;
        public static Integer min_stay = 2;
        public static Integer max_stay = 3;
        public static Integer denySameDayBooking = 4;
        public static Integer autoconfirm = 5;
        public static Integer forceConfirmationSameDay = 6;
        public static Integer noCheckIn = 7;
        public static Integer noCheckOut = 8;
        public static Integer minGuests = 9;
        public static Integer denyPayLater = 10;
    }
    
    Integer repeatPeride = TimeRepeaterData.RepeatPeriodeTypes.daily;
    
    TimeRepeaterDateRange firstEvent;
    
    public String repeaterId = UUID.randomUUID().toString();
    
    Date endingAt = null;
    
    public Integer timePeriodeType = TimePeriodeType.open;
    public String timePeriodeTypeAttribute = "";
    
    
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
    
    boolean avoidFirstEvent = false;
    
    List<String> categories = new ArrayList();
    
    Date createdDate = new Date();

    public boolean containsCategory(String typeId) {
        if(categories.isEmpty()) {
            return true;
        }
        return categories.contains(typeId);
    }
    
}
