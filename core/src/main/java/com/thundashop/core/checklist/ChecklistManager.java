/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.checklist;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.pmsmanager.IPmsManager;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingFilter;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class ChecklistManager extends GetShopSessionBeanNamed implements IChecklistManager {
    @Autowired
    private PmsManager pmsManager;
    
    @Autowired
    private OrderManager ordreManager;
    
    @Autowired
    private PmsInvoiceManager pmsInvoiceManager;
    
    private List<CheckListError> cachedResult;
    
    private HashMap<String, CheckListError> cachedErrors = new HashMap();
    
    @Override
    public List<CheckListError> getErrors(Date from, Date to) {
        List<PmsBooking> bookings = getBookings(from, to);
        List<CheckListError> errors = new ArrayList();
        
        bookings.stream().forEach(booking -> {
            try {
                CheckListError errorForBooking = null;
                if(cachedErrors.containsKey(booking.id)) {
                    errorForBooking = cachedErrors.get(booking.id);
                } else {
                    errorForBooking = runTroughProcessors(booking);
                    cachedErrors.put(booking.id, errorForBooking);
                }
                if (errorForBooking != null) {
                    errors.add(errorForBooking);
                }
            }catch(Exception e) {
                logPrint("Problem with booking: " + booking.incrementBookingId);
                e.printStackTrace();
            }
        });
        
        return errors;
    }

    private List<PmsBooking> getBookings(Date from, Date to) {
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.startDate = from;
        filter.endDate = to;
        filter.filterType = PmsBookingFilter.PmsBookingFilterTypes.active;
        List<PmsBooking> boomingsCheckingIn = pmsManager.getAllBookings(filter);
        
        return boomingsCheckingIn;
    }

    private CheckListError runTroughProcessors(PmsBooking booking) {
        List<CheckListProcessor> processors = getProcessors();
        
        for (CheckListProcessor processor : processors) {
            CheckListError error = processor.getError(booking);
            if (error != null) {
                return error;
            }
        }
        
        return null;
    }
    
    private List<CheckListProcessor> getProcessors() {
        List<CheckListProcessor> retList = new ArrayList();
        retList.add(new ExpediaVirtualCreditCardCheckProcessor(ordreManager, pmsManager));
        retList.add(new BookingComVirtualCreditCardCheckProcessor(ordreManager, pmsManager));
        retList.add(new UnpaidPaymentLinksProcessor(ordreManager, pmsManager));
        retList.add(new UnsettledAmountProcessor(ordreManager, pmsManager));
        retList.add(new DiffByPaidAmountToTotal(ordreManager, pmsManager, pmsInvoiceManager));
        retList.add(new AccruedPaymentChecklist(ordreManager, pmsManager));
        
        return retList;
    }

    public void clearCache(String bookingId) {
        cachedErrors.remove(bookingId);
    }
    
}
