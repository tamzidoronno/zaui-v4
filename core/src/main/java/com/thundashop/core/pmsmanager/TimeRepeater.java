
package com.thundashop.core.pmsmanager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class TimeRepeater {
    public LinkedList<TimeRepeaterDateRange> generateRange(TimeRepeaterData data) {
        LinkedList<TimeRepeaterDateRange> result = new LinkedList<>();

        if (data.repeatPeride == 0) {
            result = generateDailyRepeats(data);
        }
        if (data.repeatPeride == 1) {
            result = generateWeeklyRepeats(data);
        }
        if (data.repeatPeride == 2) {
            result = generateMonthlyRepeats(data);
        }
        if (data.repeatPeride == 3) {
            result = generateContinously(data);
        }

        return result;
    }

    private LinkedList<TimeRepeaterDateRange> generateDailyRepeats(TimeRepeaterData data) {
        if (data.firstEvent == null) {
            return new LinkedList<>();
        }
        LinkedList<TimeRepeaterDateRange> list = new LinkedList<>();
        list.add(data.firstEvent);

        Date start = data.firstEvent.start;
        DateTime startTime = new DateTime(start);
        DateTime endTime = new DateTime(data.firstEvent.end);
        DateTime maxEnding = new DateTime(data.endingAt);
        while (true) {
            startTime = startTime.plusDays(data.repeatEachTime);
            endTime = endTime.plusDays(data.repeatEachTime);
            if (endTime.toDate().after(maxEnding.toDate()) && !isSameDay(endTime.toDate(), maxEnding.toDate())) {
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
        LinkedList<TimeRepeaterDateRange> list = new LinkedList<>();
        if (!data.avoidFirstEvent) {
            list.add(data.firstEvent);
        }
        Date start = data.firstEvent.start;
        DateTime startTime = new DateTime(start);

        DateTime endTime = new DateTime(data.firstEvent.end);
        DateTime startIterator = startTime.withDayOfWeek(DateTimeConstants.MONDAY);
        Date startingAt = data.firstEvent.start;

        if (data.avoidFirstEvent) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startingAt);
            cal.add(Calendar.MINUTE, -1);
            startingAt = cal.getTime();
            cal.setTime(start);
            cal.add(Calendar.MINUTE, -1);
            start = cal.getTime();
        }

        while (true) {
            startIterator = startIterator.plusWeeks(data.repeatEachTime);
            if (startIterator.toDate().after(data.endingAt) && !isSameDay(endTime.toDate(), data.endingAt)
                    && !isSameWeek(endTime.toDate(), data.endingAt)) {
                break;
            }
            TimeRepeaterDateRange range = null;

            DateTime starting = startTime.withDayOfWeek(DateTimeConstants.MONDAY);
            DateTime ending = endTime.withDayOfWeek(DateTimeConstants.MONDAY);
            if (data.repeatMonday && ending.toDate().before(data.endingAt)) {
                range = createWeeklyDateRangeFromDate(starting, data.firstEvent);
                if (range.start.after(start)) {
                    list.add(range);
                }
            }

            starting = startTime.withDayOfWeek(DateTimeConstants.TUESDAY);
            ending = endTime.withDayOfWeek(DateTimeConstants.TUESDAY);
            if (data.repeatTuesday && ending.toDate().before(data.endingAt) && starting.isAfter(startingAt.getTime())) {
                range = createWeeklyDateRangeFromDate(starting, data.firstEvent);
                if (range.start.after(start))
                    list.add(range);
            }

            starting = startTime.withDayOfWeek(DateTimeConstants.WEDNESDAY);
            ending = endTime.withDayOfWeek(DateTimeConstants.WEDNESDAY);
            if (data.repeatWednesday && ending.toDate().before(data.endingAt)
                    && starting.isAfter(startingAt.getTime())) {
                range = createWeeklyDateRangeFromDate(starting, data.firstEvent);
                if (range.start.after(start))
                    list.add(range);
            }

            starting = startTime.withDayOfWeek(DateTimeConstants.THURSDAY);
            ending = endTime.withDayOfWeek(DateTimeConstants.THURSDAY);
            if (data.repeatThursday && ending.toDate().before(data.endingAt)
                    && starting.isAfter(startingAt.getTime())) {
                range = createWeeklyDateRangeFromDate(starting, data.firstEvent);
                if (range.start.after(start))
                    list.add(range);
            }

            starting = startTime.withDayOfWeek(DateTimeConstants.FRIDAY);
            ending = endTime.withDayOfWeek(DateTimeConstants.FRIDAY);
            if (data.repeatFriday && ending.toDate().before(data.endingAt) && starting.isAfter(startingAt.getTime())) {
                range = createWeeklyDateRangeFromDate(starting, data.firstEvent);
                if (range.start.after(start))
                    list.add(range);
            }

            starting = startTime.withDayOfWeek(DateTimeConstants.SATURDAY);
            ending = endTime.withDayOfWeek(DateTimeConstants.SATURDAY);
            if (data.repeatSaturday && ending.toDate().before(data.endingAt)
                    && starting.isAfter(startingAt.getTime())) {
                range = createWeeklyDateRangeFromDate(starting, data.firstEvent);
                if (range.start.after(start))
                    list.add(range);
            }

            starting = startTime.withDayOfWeek(DateTimeConstants.SUNDAY);
            ending = endTime.withDayOfWeek(DateTimeConstants.SUNDAY);
            if (data.repeatSunday && ending.toDate().before(data.endingAt) && starting.isAfter(startingAt.getTime())) {
                range = createWeeklyDateRangeFromDate(starting, data.firstEvent);
                if (range.start.after(start))
                    list.add(range);
            }

            startTime = startTime.plusWeeks(data.repeatEachTime);
            endTime = endTime.plusWeeks(data.repeatEachTime);
        }

        // remove all overdues.
        LinkedList<TimeRepeaterDateRange> remove = new LinkedList<>();
        for (TimeRepeaterDateRange res : list) {
            if (res.start.after(data.endingAt)) {
                remove.add(res);
            }
        }

        list.removeAll(remove);

        return list;
    }

    private LinkedList<TimeRepeaterDateRange> generateMonthlyRepeats(TimeRepeaterData data) {
        LinkedList<TimeRepeaterDateRange> list = new LinkedList<>();
        list.add(data.firstEvent);
        DateTime startTime = new DateTime(data.firstEvent.start);
        int dayOfWeek = startTime.getDayOfWeek();
        DateTime endTime = new DateTime(data.firstEvent.end);
        while (true) {
            startTime = startTime.plusMonths(data.repeatEachTime);
            endTime = endTime.plusMonths(data.repeatEachTime);
            if (endTime.toDate().after(data.endingAt) && !isSameDay(endTime.toDate(), data.endingAt)) {
                break;
            }

            TimeRepeaterDateRange range = new TimeRepeaterDateRange();
            if (data.repeatAtDayOfWeek) {
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

    private LinkedList<TimeRepeaterDateRange> generateContinously(TimeRepeaterData data) {
        LinkedList<TimeRepeaterDateRange> res = new LinkedList<>();
        TimeRepeaterDateRange toAdd = new TimeRepeaterDateRange();
        toAdd.end = data.endingAt;
        toAdd.start = data.firstEvent.start;
        res.add(toAdd);
        return res;
    }

    public boolean isInTimeRange(TimeRepeaterData openingHoursData, Date date) {
        LinkedList<TimeRepeaterDateRange> ranges = generateRange(openingHoursData);
        for (TimeRepeaterDateRange range : ranges) {
            if (range.isBetweenTime(date))
                return true;
        }
        return false;
    }

    private TimeRepeaterDateRange createWeeklyDateRangeFromDate(DateTime starting, TimeRepeaterDateRange firstEvent) {
        Calendar firstDate = Calendar.getInstance();
        firstDate.setTime(firstEvent.start);

        TimeRepeaterDateRange result = new TimeRepeaterDateRange();
        Calendar cal = Calendar.getInstance();
        cal.setTime(starting.toDate());
        cal.set(Calendar.HOUR_OF_DAY, firstDate.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, firstDate.get(Calendar.MINUTE));
        cal.set(Calendar.MILLISECOND, firstDate.get(Calendar.MILLISECOND));
        cal.set(Calendar.SECOND, firstDate.get(Calendar.SECOND));
        result.start = cal.getTime();

        cal.add(Calendar.SECOND, (int) ((firstEvent.end.getTime() - firstEvent.start.getTime()) / 1000));
        result.end = cal.getTime();
        return result;
    }

    private boolean isSameWeek(Date toDate, Date endingAt) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(toDate);
        cal1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        int year1 = cal1.get(Calendar.YEAR);
        int week1 = cal1.get(Calendar.WEEK_OF_YEAR);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endingAt);
        cal2.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        int year2 = cal2.get(Calendar.YEAR);
        int week2 = cal2.get(Calendar.WEEK_OF_YEAR);

        if (year1 == year2 && week1 == week2) {
            return true;
        }
        return false;
    }
}
