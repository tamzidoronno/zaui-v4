package com.thundashop.core.hotelmanager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.thundashop.core.hotelbookingmanager.ReservationPart;
import com.thundashop.core.hotelbookingmanager.Room;
import com.thundashop.core.hotelbookingmanager.RoomNotAvailableException;
import java.util.Calendar;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ktonder
 */
public class RoomTest {
    private Room room101;
    private Room room102;
    
    public RoomTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        room101 = new Room();
        room101.name = "101";
        
        room102 = new Room();
        room102.name = "102";
    }
    
    @After
    public void tearDown() {
    }
    
    /**
     * We need to make sure that the rooms return true if there has 
     * not been added any reservation yet.
     */
    @Test
    public void testReturnAvailableIfNoReservationsDone() {
        Date startDate = new Date();
        Date endDate = new Date();
        boolean available = room101.isAvailable(startDate, endDate);
        assertEquals(available, true);
    }
    
    /**
     * No reservation added yet, adding the first one.
     */
    @Test
    public void testCanAddFirstReservation() {
        room101.addReservation(getPartReservation1January());
    }
    
    /**
     * Making sure that the its not possible to add a period that overlaps in the beginning of another period.
     * 
     * Period added: 1 jan 2015 19:00 - 2 jan 2015 19:00
     * Trying to add: 1 jan 2015 18:00 - 1 jan 2015 20:00
     * 
     * Expected to not be available.
     */
    @Test
    public void testRoomAvailableWhenOverlappingBeginningOfPeriod() {
        room101.addReservation(getPartReservation1January());
        
        Calendar firstOfJanuary = getFirstOfJanuary();
        firstOfJanuary.add(Calendar.HOUR_OF_DAY, -1);
        Date startDate = firstOfJanuary.getTime();
        firstOfJanuary.add(Calendar.HOUR_OF_DAY, 2);
        Date endDate = firstOfJanuary.getTime();
        
        // Overlap by an hour beginning of period.
        boolean available = room101.isAvailable(startDate, endDate);
        assertEquals(false, available);
    }
    
    /**
     * Making sure that the its not possible to add a period that overlaps in the beginning of another period.
     * 
     * Period added: 1 jan 2015 19:00 - 2 jan 2015 19:00
     * Trying to add: 2 jan 2015 18:00 - 2 jan 2015 20:00
     * 
     * Expected to not be available.
     */
    @Test
    public void testRoomAvailableWhenOverlappingEndOfPeriod() {
        room101.addReservation(getPartReservation1January());
        
        Calendar firstOfJanuary = getFirstOfJanuary();
        firstOfJanuary.add(Calendar.DAY_OF_MONTH, 1);
        firstOfJanuary.add(Calendar.HOUR_OF_DAY, -1);
        Date startDate = firstOfJanuary.getTime();
        firstOfJanuary.add(Calendar.HOUR_OF_DAY, 2);
        Date endDate = firstOfJanuary.getTime();
        
        // Overlap by an hour beginning of period.
        boolean available = room101.isAvailable(startDate, endDate);
        assertEquals(false, available);
    }
    
    /**
     * Making sure that the its not possible to add a period that overlaps in the beginning of another period.
     * 
     * Period added: 1 jan 2015 19:00 - 2 jan 2015 19:00
     * Trying to add: 1 jan 2015 18:00 - 2 jan 2015 20:00
     * 
     * Expected to not be available.
     */
    @Test
    public void testRoomAvailableWhenOverlappingWholePeriod() {
        room101.addReservation(getPartReservation1January());
        
        Calendar firstOfJanuary = getFirstOfJanuary();
        firstOfJanuary.add(Calendar.HOUR_OF_DAY, -1);
        Date startDate = firstOfJanuary.getTime();
        
        firstOfJanuary.add(Calendar.DAY_OF_MONTH, 1);
        firstOfJanuary.add(Calendar.HOUR_OF_DAY, 2);
        Date endDate = firstOfJanuary.getTime();
        
        // Overlap by an hour beginning of period.
        boolean available = room101.isAvailable(startDate, endDate);
        assertEquals(false, available);
    }
    
    /**
     * Periods added:
     * 1 jan 2015 19:00 - 2 jan 2015 19:00
     * 3 jan 2015 19:00 - 4 jan 2015 19:00
     * 
     * Check if available = true
     * 2 jan 2015 19:00 - 3 jan 2015 19:00
     */
    @Test 
    public void testIfAvailableWhenMultiplePeriodsAdded() {
        room101.addReservation(getPartReservation1January());
        room101.addReservation(getPartReservation3January());
        
        Calendar cal = getFirstOfJanuary();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = cal.getTime();
        
        System.out.println(startDate + " " + endDate);
        boolean available = room101.isAvailable(startDate, endDate);
        assertEquals(true, available);
    }
    
    /**
     * Making sure that the its not possible to add a period that overlaps in the beginning of another period.
     * 
     * Period added: 1 jan 2015 19:00 - 2 jan 2015 19:00
     * Trying to add: 1 jan 2015 18:00 - 2 jan 2015 20:00
     * 
     * Expected to not be available.
     */
    @Test
    public void testRoomAvailableWhenOverlappingExactSamePeriod() {
        ReservationPart part = getPartReservation1January();
        room101.addReservation(part);
        
        // Check if available if it is the exact same period.
        boolean available = room101.isAvailable(part.startDate, part.endDate);
        assertEquals(false, available);
    }
    
    @Test
    public void throwsExepctionIfOverlappingPeriodIsTriedToBeAdded() {
        ReservationPart part = getPartReservation1January();
        room101.addReservation(part);
        
        try {
            room101.addReservation(part);
        } catch (RoomNotAvailableException ex) {
            return;
        }
        
        fail("Expected execption");
    }
    
    private Calendar getFirstOfJanuary() {
        Calendar firstOfJanuary = Calendar.getInstance();
        firstOfJanuary.set(Calendar.YEAR, 2015);
        firstOfJanuary.set(Calendar.MONTH, 0);
        firstOfJanuary.set(Calendar.DAY_OF_MONTH, 1);
        firstOfJanuary.set(Calendar.HOUR_OF_DAY, 7);
        firstOfJanuary.set(Calendar.MINUTE, 0);
        firstOfJanuary.set(Calendar.SECOND, 0);
        return firstOfJanuary;
    }
    
    /**
     * A reservation from 1 januar 2015 kl: 07:00:00 to 2 january 2015 kl: 07:00:00
     * @return 
     */
    private ReservationPart getPartReservation1January() {
        Calendar firstOfJanuary = getFirstOfJanuary();
        ReservationPart part = new ReservationPart();
        part.startDate = firstOfJanuary.getTime();
        
        firstOfJanuary.set(Calendar.DAY_OF_MONTH, 2);
        part.endDate = firstOfJanuary.getTime();
        
        return part;
    }
    
    /**
     * A reservation from 3 januar 2015 kl: 07:00:00 to 4 january 2015 kl: 07:00:00
     * @return 
     */
    private ReservationPart getPartReservation3January() {
        Calendar firstOfJanuary = getFirstOfJanuary();
        firstOfJanuary.set(Calendar.DAY_OF_MONTH, 3);
        ReservationPart part = new ReservationPart();
        part.startDate = firstOfJanuary.getTime();
        
        firstOfJanuary.set(Calendar.DAY_OF_MONTH, 4);
        part.endDate = firstOfJanuary.getTime();
        
        return part;
    }
}
