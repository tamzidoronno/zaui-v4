/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.calendarmanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class ReminderHistory extends DataCommon implements Comparable<ReminderHistory> {
    public List<User> users = new ArrayList();
    public boolean byEmail = false;
    public String text = "";
    public Date date = new Date();
    public String subject = "";
    public String eventId = "";

    @Override
    public int compareTo(ReminderHistory o) {
        if(o.date == null || date == null) {
            return 0;
        }
        return date.compareTo(o.date);
    }
}
