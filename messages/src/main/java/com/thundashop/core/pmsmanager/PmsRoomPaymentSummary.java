/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class PmsRoomPaymentSummary {
    public String pmsBookingId;
    public String pmsBookingRoomId;
    public List<PmsRoomPaymentSummaryRow> rows = new ArrayList();
    public List<String> orderIds = new ArrayList();
 
}
