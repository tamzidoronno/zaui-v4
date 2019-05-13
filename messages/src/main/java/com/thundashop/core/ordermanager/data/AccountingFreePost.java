/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import java.util.stream.Stream;

/**
 *
 * @author ktonder
 */
public class AccountingFreePost extends DataCommon {
    public String createdByUserId;
    public String debitAccountNumber;
    public String creditAccountNumber;
    public double amount;
    public String comment = "";
    public Date date;
    public boolean closed = false;

    public boolean isBetween(Date start, Date end) {
        long startTime = start.getTime();
        long endTime = end.getTime();
        return startTime <= date.getTime() && date.getTime() < endTime;
    }
}
