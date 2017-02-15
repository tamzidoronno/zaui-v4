/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ktonder
 */
public class ConferenceDataDay {
    public String day;
    public List<ConferenceDataRow> conferences = new ArrayList();
    
    public Date getParsedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.M.yy hh:mm:ss");
        
        if (day == null)
            return null;
        
        try {
            Date date = sdf.parse(day+ " 00:00:00");
            return date;
        } catch (ParseException ex) {
            return null;
        }
    }
}
