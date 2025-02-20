/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author boggi
 */
public class TimeRepeaterTest {
    
    public TimeRepeaterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testDailyRepeatIntervalOne() {
        doTestDailyRepeatInterval(1);
        doTestDailyRepeatInterval(3);
        doTestDailyRepeatInterval(5);
        doTestDailyRepeatInterval(10);
    }
    
    @Test
    public void testWeeklyRepeatIntervalMondays() {
        doTestWeeklyRepeatInterval(1, true, false, false, false, false, false, false,4);
        doTestWeeklyRepeatInterval(2, true, false, false, false, false, false, false,2);
        doTestWeeklyRepeatInterval(5, true, false, false, false, false, false, false,1);
        doTestWeeklyRepeatInterval(10, true, false, false, false, false, false, false,1);
    }
    
    @Test
    public void testWeeklyRepeatIntervalMondaysWednesDays() {
        doTestWeeklyRepeatInterval(1, true, false, true, false, false, false, false,8);
        doTestWeeklyRepeatInterval(2, true, false, true, false, false, false, false,4);
        doTestWeeklyRepeatInterval(5, true, false, true, false, false, false, false,1);
        doTestWeeklyRepeatInterval(10, true, false, true, false, false, false, false,1);
    }
    
    @Test
    public void testWeeklyRepeatIntervalMondaysWednesDayFriday() {
        doTestWeeklyRepeatInterval(1, true, false, true, false, true, false, false,12);
        doTestWeeklyRepeatInterval(2, true, false, true, false, true, false, false,6);
        doTestWeeklyRepeatInterval(5, true, false, true, false, true, false, false,1);
        doTestWeeklyRepeatInterval(10, true, false, true, false, true, false, false,1);
    }
    
    @Test
    public void testWeeklyRepeatIntervalWeekEnds() {
        doTestWeeklyRepeatInterval(1, false, false, false, false, false, true, true,9);
        doTestWeeklyRepeatInterval(2, false, false, false, false, false, true, true,5);
        doTestWeeklyRepeatInterval(5, false, false, false, false, false, true, true,1);
        doTestWeeklyRepeatInterval(10, false, false, false, false, false, true, true,1);
        doSpecialWeeklyTest();
    }
    
    @Test
    public void testMonthlyRepeatDayOfMonth() {
        doMonthlyRepeat(false, 1);
        doMonthlyRepeat(false, 2);
        doMonthlyRepeat(false, 5);
        doMonthlyRepeat(false, 10);
    }
    
    @Test
    public void testMonthlyRepeatDayOfWeek() {
        doMonthlyRepeat(true, 1);
        doMonthlyRepeat(true, 2);
        doMonthlyRepeat(true, 5);
        doMonthlyRepeat(true, 10);
    }
    

    private void doTestDailyRepeatInterval(int repeatEachDay) {
        TimeRepeaterData data = new TimeRepeaterData();
        
        TimeRepeaterDateRange firstEvent = new TimeRepeaterDateRange();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 15);
        firstEvent.start = cal.getTime();
        firstEvent.end = new Date(firstEvent.start.getTime()+(60*1000*60*1));
        
        //Ending 10 days from now.
        data.firstEvent = firstEvent;
        data.endingAt = new Date(firstEvent.end.getTime() + (86400*1000*10));
        data.repeatEachTime = repeatEachDay;
        
        
        TimeRepeater instance = new TimeRepeater();
        List<TimeRepeaterDateRange> result = instance.generateRange(data);
        assertTrue(result.size() == (10/repeatEachDay)+1);
        
        //Check if dates are returned correctly
        int iteration = 0;
        DateTime start = new DateTime(data.firstEvent.start);
        DateTime end = new DateTime(data.firstEvent.end);
        for(TimeRepeaterDateRange returnDateRange : result) {
            DateTime correctStart = start.plusDays((iteration*repeatEachDay));
            DateTime correctEnd = end.plusDays((iteration*repeatEachDay));
            assertTrue(returnDateRange.start.equals(correctStart.toDate()));
            assertTrue(returnDateRange.end.equals(correctEnd.toDate()));
            iteration++;
        }
    }

    private void doTestWeeklyRepeatInterval(int interval, boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun, long expect) {
        TimeRepeaterData data = new TimeRepeaterData();
        
        TimeRepeaterDateRange firstEvent = new TimeRepeaterDateRange();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            firstEvent.start = sdf.parse("2016-01-04");
            data.endingAt = sdf.parse("2016-02-01");
        }catch(Exception e) {
            assertTrue(false);
        }
        
        firstEvent.end = new Date(firstEvent.start.getTime()+(60*1000*60*3));
        
        data.firstEvent = firstEvent;
        data.repeatPeride = 1;
        data.repeatEachTime = interval;
        
        data.repeatMonday = mon;
        data.repeatTuesday = tue;
        data.repeatWednesday = wed;
        data.repeatThursday = thu;
        data.repeatFriday = fri;
        data.repeatSaturday = sat;
        data.repeatSunday = sun;
        
        
        TimeRepeater instance = new TimeRepeater();
        List<TimeRepeaterDateRange> result = instance.generateRange(data);
        
        int numberOfRepeats = 0;
        if(mon) { numberOfRepeats++; }
        if(tue) { numberOfRepeats++; }
        if(wed) { numberOfRepeats++; }
        if(thu) { numberOfRepeats++; }
        if(fri) { numberOfRepeats++; }
        if(sat) { numberOfRepeats++; }
        if(sun) { numberOfRepeats++; }
        
        assertEquals(expect, result.size());
        
        for(TimeRepeaterDateRange res : result) {
            assertNotNull(res.start);
            assertNotNull(res.end);
        }
    }

    private void doMonthlyRepeat(boolean dayOfWeek, int interval) {
        
        TimeRepeaterData data = new TimeRepeaterData();
        
        TimeRepeaterDateRange firstEvent = new TimeRepeaterDateRange();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            firstEvent.start = sdf.parse("2016-01-01");
            data.endingAt = sdf.parse("2017-01-02");
        }catch(Exception e) {
            assertTrue(false);
        }
        
        firstEvent.end = new Date(firstEvent.start.getTime()+(60*1000*60*3));
        
        data.firstEvent = firstEvent;
        data.repeatPeride = TimeRepeaterData.RepeatPeriodeTypes.monthly;
        data.repeatEachTime = interval;
        data.repeatAtDayOfWeek = dayOfWeek;
        
        TimeRepeater instance = new TimeRepeater();
        List<TimeRepeaterDateRange> result = instance.generateRange(data);
        
        int resultNumber = (12 / interval) +1;

        if(resultNumber != result.size()) {
            GetShopLogHandler.logPrintStatic("Failed to generate correct result: " + result.size() + " ", null);
        }
        assertEquals(resultNumber, result.size());
        
        for(TimeRepeaterDateRange res : result) {
            assertNotNull(res.start);
            assertNotNull(res.end);
        }
        
        if(dayOfWeek) {
            for(TimeRepeaterDateRange range : result) {
                DateTime start = new DateTime(range.start);
                assertEquals(5, start.getDayOfWeek());
            }
        } else {
            for(TimeRepeaterDateRange range : result) {
                DateTime start = new DateTime(range.start);
                assertEquals(1, start.getDayOfMonth());
            }
        }
        
    }

    private void doSpecialWeeklyTest() {
        TimeRepeaterData data = new TimeRepeaterData();
        int expect = 6;
        TimeRepeaterDateRange firstEvent = new TimeRepeaterDateRange();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            firstEvent.start = sdf.parse("2016-01-04");
            data.endingAt = sdf.parse("2016-01-14");
        }catch(Exception e) {
            assertTrue(false);
        }
        
        firstEvent.end = new Date(firstEvent.start.getTime()+(60*1000*60*3));
        
        data.firstEvent = firstEvent;
        data.repeatPeride = 1;
        data.repeatEachTime = 1;
        
        data.repeatMonday = true;
        data.repeatTuesday = true;
        data.repeatWednesday = true;
        data.repeatThursday = false;
        data.repeatFriday = false;
        data.repeatSaturday = false;
        data.repeatSunday = false;
        
        
        TimeRepeater instance = new TimeRepeater();
        List<TimeRepeaterDateRange> result = instance.generateRange(data);
        
        assertEquals(expect, result.size());
        
        for(TimeRepeaterDateRange res : result) {
            assertNotNull(res.start);
            assertNotNull(res.end);
        }
    }

    
}
