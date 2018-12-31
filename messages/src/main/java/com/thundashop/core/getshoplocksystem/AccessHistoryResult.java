/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.util.Date;

/**
 *
 * @author ktonder
 */
public class AccessHistoryResult implements Comparable<AccessHistoryResult> {
    public String doorName;
    public Date time;
    public String name;

    @Override
    public int compareTo(AccessHistoryResult o) {
        return time.compareTo(o.time);
    }
}
