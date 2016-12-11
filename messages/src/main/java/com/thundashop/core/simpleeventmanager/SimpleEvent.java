/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.simpleeventmanager;

import com.thundashop.core.common.DataCommon;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class SimpleEvent extends DataCommon {

    public Date date;
    public String name;
    public String eventPageId;
    public String location;
    
    static Comparator<? super SimpleEvent> getDateSorter() {
        return (o1, o2) -> {
            return o2.date.compareTo(o1.date);
        };
    }
}
