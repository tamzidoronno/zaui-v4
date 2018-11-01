/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.checklist;

import com.thundashop.core.pmsmanager.PmsBooking;

/**
 *
 * @author ktonder
 */
public interface CheckListProcessor {
    public CheckListError getError(PmsBooking booking);
}
