/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class PmsOrderCreateRow {
    public String roomId;
    public String conferenceId;
    public List<PmsOrderCreateRowItemLine> items = new ArrayList();

    void removeRowsNotWithin(Date start, Date end) {
        items.removeIf(o -> !o.isWithin(start, end));
    }
}
