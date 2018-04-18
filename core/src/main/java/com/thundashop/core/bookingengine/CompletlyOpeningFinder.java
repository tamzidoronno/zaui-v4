/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingTimeLine;
import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class CompletlyOpeningFinder implements MagicCutter {
    private List<Booking> bookingsWithinDaterange;
    private Date start;
    private Date end;

    public CompletlyOpeningFinder(List<Booking> bookingsWithinDaterange, Date start, Date end) {
        this.bookingsWithinDaterange = bookingsWithinDaterange;
        this.start = start;
        this.end = end;
    }
   
    @Override
    public Date getCutDate() {
        BookingTimeLineFlatten flatten = new BookingTimeLineFlatten(0, "all");
        bookingsWithinDaterange.stream()
                .forEach(b -> flatten.add(b));
        
        List<BookingTimeLine> ret = flatten.getTimelinesIncludedNulls();
        for (BookingTimeLine line : ret) {
            if (line.count == 0 && line.start.after(end)) {
                return line.start;
            }
        }
        
        return null;
    }
    
}
