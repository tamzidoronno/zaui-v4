/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class AccessGroupUserAccess extends DataCommon implements Comparable<AccessGroupUserAccess> {
    public LockCode lockCode = null;
    public String fullName = "";
    public int prefix;
    public String email;
    public String lockGroupId;  
    public int phonenumber;
    public List<String> emailMessages = new ArrayList();
    public List<String> smsMessages = new ArrayList();

    @Override
    public int compareTo(AccessGroupUserAccess o) {
        if(o.rowCreatedDate == null || rowCreatedDate == null) {
            return 0;
        }
        return rowCreatedDate.compareTo(o.rowCreatedDate);
    }
}
