/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class ZReport extends DataCommon implements Comparable<ZReport> {
    public List<String> orderIds = new ArrayList();
    public String createdByUserId = "";
    public double totalAmount;
    
    @Override
    public int compareTo(ZReport o) {
        return rowCreatedDate.compareTo(o.rowCreatedDate);
    }
}