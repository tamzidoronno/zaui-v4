/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.thundashop.core.pmsmanager.PmsBooking;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class CanCloseZReport {
    public boolean canClose = true;
    
    public List<PmsBooking> bookingsWithProblems = new ArrayList();
    public long fReportErrorCount = 0;
    
    public void finalize() {
        if (!bookingsWithProblems.isEmpty()) {
            canClose = false;
        }
        
        if (fReportErrorCount > 0) {
            canClose = false;
        }
    }
}
