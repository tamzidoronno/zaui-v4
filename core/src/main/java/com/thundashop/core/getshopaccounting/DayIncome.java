/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author ktonder
 */
public class DayIncome {
    public Date start;
    public Date end;
    public List<DayEntry> dayEntries = new ArrayList();

    public boolean within(Date date) {
        long startLong = start.getTime();
        long endLong = end.getTime();
        long dateLong = date.getTime();
        
        return startLong <= dateLong && dateLong < endLong;
    }
}
