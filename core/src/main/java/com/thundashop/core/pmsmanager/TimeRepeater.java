
package com.thundashop.core.pmsmanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class TimeRepeater {
    public LinkedList<TimeRepeaterDateRange> generateRange(TimeRepeaterData data) {
        LinkedList<TimeRepeaterDateRange> result = new LinkedList();
        
        
        if(data.repeatPeride == 0) {
            result = generateDailyRepeats(data);
        }
        if(data.repeatPeride == 1) {
            result = generateWeeklyRepeats(data);
        }
        if(data.repeatPeride == 2) {
            result = generateMonthlyRepeats(data);
        }
        
        return result;
    }

    private LinkedList<TimeRepeaterDateRange> generateDailyRepeats(TimeRepeaterData data) {
        if(data.firstEvent == null) {
            return new LinkedList();
        }
        LinkedList list = new LinkedList();
        list.add(data.firstEvent);

        Date start = data.firstEvent.start;
        DateTime startTime = new DateTime(start);
        DateTime endTime = new DateTime(data.firstEvent.end);
        DateTime maxEnding = new DateTime(data.endingAt);
        while(true) {
            startTime = startTime.plusDays(data.repeatEachTime);
            endTime = endTime.plusDays(data.repeatEachTime);
            if(endTime.toDate().after(data.endingAt) && !isSameDay(endTime.toDate(), data.endingAt)) {
                break;
            }
            
            TimeRepeaterDateRange range = new TimeRepeaterDateRange();
            range.end = endTime.toDate();
            range.start = startTime.toDate();
            list.add(range);
        }
        return list;
    }

    private LinkedList<TimeRepeaterDateRange> generateWeeklyRepeats(TimeRepeaterData data) {
        LinkedList list = new LinkedList();
        list.add(data.firstEvent);
        Date start = data.firstEvent.start;
        DateTime startTime = new DateTime(start);
        DateTime endTime = new DateTime(data.firstEvent.end);
        DateTime startIterator = startTime.withDayOfWeek(DateTimeConstants.MONDAY);
        Date startingAt = data.firstEvent.start;
        while(true) {
            startTime = startTime.plusWeeks(data.repeatEachTime);
            endTime = endTime.plusWeeks(data.repeatEachTime);
            startIterator = startIterator.plusWeeks(data.repeatEachTime);
            if(startIterator.toDate().after(data.endingAt) && !isSameDay(endTime.toDate(), data.endingAt)) {
                break;
            }
            TimeRepeaterDateRange range = null;
            
            DateTime starting = startTime.withDayOfWeek(DateTimeConstants.MONDAY);
            DateTime ending = endTime.withDayOfWeek(DateTimeConstants.MONDAY);
            if(data.repeatMonday && ending.toDate().before(data.endingAt)) {
                range = new TimeRepeaterDateRange();
                range.start = starting.toDate();
                range.end = ending.toDate();
                list.add(range);
            }
            
            starting = startTime.withDayOfWeek(DateTimeConstants.TUESDAY);
            ending = endTime.withDayOfWeek(DateTimeConstants.TUESDAY);
            if(data.repeatTuesday && ending.toDate().before(data.endingAt) && starting.isAfter(startingAt.getTime())) {
                range = new TimeRepeaterDateRange();
                range.start = starting.toDate();
                range.end = ending.toDate();
                list.add(range);
            }
            
            starting = startTime.withDayOfWeek(DateTimeConstants.WEDNESDAY);
            ending = endTime.withDayOfWeek(DateTimeConstants.WEDNESDAY);
            if(data.repeatWednesday && ending.toDate().before(data.endingAt) && starting.isAfter(startingAt.getTime())) {
                range = new TimeRepeaterDateRange();
                range.start = starting.toDate();
                range.end = ending.toDate();
                list.add(range);
            }
            
            starting = startTime.withDayOfWeek(DateTimeConstants.THURSDAY);
            ending = endTime.withDayOfWeek(DateTimeConstants.THURSDAY);
            if(data.repeatThursday && ending.toDate().before(data.endingAt) && starting.isAfter(startingAt.getTime())) {
                range = new TimeRepeaterDateRange();
                range.start = starting.toDate();
                range.end = ending.toDate();
                list.add(range);
            }
            
            starting = startTime.withDayOfWeek(DateTimeConstants.FRIDAY);
            ending = endTime.withDayOfWeek(DateTimeConstants.FRIDAY);
            if(data.repeatFriday && ending.toDate().before(data.endingAt) && starting.isAfter(startingAt.getTime())) {
                range = new TimeRepeaterDateRange();
                range.start = starting.toDate();
                range.end = ending.toDate();
                list.add(range);
            }
            
            starting = startTime.withDayOfWeek(DateTimeConstants.SATURDAY);
            ending = endTime.withDayOfWeek(DateTimeConstants.SATURDAY);
            if(data.repeatSaturday && ending.toDate().before(data.endingAt) && starting.isAfter(startingAt.getTime())) {
                range = new TimeRepeaterDateRange();
                range.start = starting.toDate();
                range.end = ending.toDate();
                list.add(range);
            }
            
            starting = startTime.withDayOfWeek(DateTimeConstants.SUNDAY);
            ending = endTime.withDayOfWeek(DateTimeConstants.SUNDAY);
            if(data.repeatSunday && ending.toDate().before(data.endingAt) && starting.isAfter(startingAt.getTime())) {
                range = new TimeRepeaterDateRange();
                range.start = starting.toDate();
                range.end = ending.toDate();
                list.add(range);
            }
        }
        return list;
    }

    private LinkedList<TimeRepeaterDateRange> generateMonthlyRepeats(TimeRepeaterData data) {
        LinkedList list = new LinkedList();
        list.add(data.firstEvent);
        DateTime startTime = new DateTime(data.firstEvent.start);
        int dayOfWeek = startTime.getDayOfWeek();
        DateTime endTime = new DateTime(data.firstEvent.end);
        while(true) {
            startTime = startTime.plusMonths(data.repeatEachTime);
            endTime = endTime.plusMonths(data.repeatEachTime);
            if(endTime.toDate().after(data.endingAt) && !isSameDay(endTime.toDate(), data.endingAt)) {
                break;
            }
            
            TimeRepeaterDateRange range = new TimeRepeaterDateRange();
            if(data.repeatAtDayOfWeek) {
                DateTime starting = startTime.withDayOfWeek(dayOfWeek);
                DateTime ending = endTime.withDayOfWeek(dayOfWeek);
                range.start = starting.toDate();
                range.end = ending.toDate();
                list.add(range);
            } else {
                range.start = startTime.toDate();
                range.end = endTime.toDate();
                list.add(range);
            }
            
        }
        return list;
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }
}
