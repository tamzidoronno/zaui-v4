/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.checklist;

import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class DiffByPaidAmountToTotal extends CheckProcessorBase implements CheckListProcessor {

    private final PmsInvoiceManager pmsInvoiceManager;

    public DiffByPaidAmountToTotal(OrderManager orderManager, PmsManager pmsManager, PmsInvoiceManager pmsInvoiceManager) {
        super(orderManager, pmsManager);
        this.pmsInvoiceManager = pmsInvoiceManager;
    }
    
    public static BigDecimal roundTwoDecimals(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String twoDec = df.format(value);
        
        return new BigDecimal(twoDec);
    }

    @Override
    public CheckListError getError(PmsBooking booking) {
        if (booking.ignoreUnsettledAmount) {
            return null;
        }
        
        for (PmsBookingRooms room : booking.rooms) {
            if (room.date.end.after(new Date())) {
                continue;
            }
            
            double totalPaidPrice = booking.orderIds
                    .stream()
                    .map(id -> getOrderManager().getOrder(id))
                    .filter(order -> order.status == Order.Status.PAYMENT_COMPLETED || order.isInvoice() || order.isSamleFaktura())
                    .filter(order -> pmsInvoiceManager.hasRoomItems(room.pmsBookingRoomId, order))
                    .flatMap(order -> order.cart.getItems().stream())
                    .filter(item -> item.getProduct().externalReferenceId.equals(room.pmsBookingRoomId))
                    .mapToDouble(item -> item.getTotalAmount())
                    .sum();
                    
            BigDecimal totalPaid = roundTwoDecimals(totalPaidPrice);
            BigDecimal totalRoom = roundTwoDecimals(room.totalCost);
            BigDecimal diff = totalPaid.subtract(totalRoom);
            
            if (diff.intValue() != 0) {
                CheckListError error = new CheckListError();
                error.filterType = getClass().getSimpleName();
                error.metaData.put("diff", diff);
                error.metaData.put("pmsbookingid", booking.id);
                error.metaData.put("pmsBookingRoomId", room.pmsBookingRoomId);
                error.metaData.put("deletedAndNotRefundable", room.isDeleted() && room.nonrefundable);
                return error;
            }
        }
        
        return null;
    }
    
}
