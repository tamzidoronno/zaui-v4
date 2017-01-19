/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class ConferenceData extends DataCommon {
    public String note;
    
    /**
     * Always finalized to the count of guests on the booking
     */
    @Transient
    public int attendeesCount;
    
    @Transient
    public Date date;
    
    List<ConferenceDataRow> conferences = new ArrayList();
}
