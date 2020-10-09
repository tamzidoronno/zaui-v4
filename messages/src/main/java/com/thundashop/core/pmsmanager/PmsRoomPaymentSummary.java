/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author ktonder
 */
public class PmsRoomPaymentSummary {
    public String pmsBookingId;
    public String pmsBookingRoomId;
    public List<PmsRoomPaymentSummaryRow> rows = new ArrayList();
    public List<String> orderIds = new ArrayList();

    public List<PmsOrderCreateRowItemLine> getCheckoutRows() {
        return rows.stream()
                .filter(o -> o.count != 0)
                .map(o -> createPmsOrderCreateRow(o))
                .collect(Collectors.toList());
    }

    private PmsOrderCreateRowItemLine createPmsOrderCreateRow(PmsRoomPaymentSummaryRow o) {
        PmsOrderCreateRowItemLine ret = new PmsOrderCreateRowItemLine();
        ret.createOrderOnProductId = o.createOrderOnProductId;
        ret.isAccomocation = o.isAccomocation;
        ret.includedInRoomPrice = o.includedInRoomPrice;
        ret.count = o.count;
        ret.price = o.priceToCreateOrders;
        ret.date = o.date;
        ret.textOnOrder = o.textOnOrder;
        ret.addonId = o.addonId;
        return ret;
    }

    /**
     * This function is used for debugging only.
     */
    public void print() {
        System.out.println("===================== Summary ===================");
        rows.stream().forEach(o -> {
            o.print();
        });
        System.out.println("===================== End Summary ===================");
    }
 
}
