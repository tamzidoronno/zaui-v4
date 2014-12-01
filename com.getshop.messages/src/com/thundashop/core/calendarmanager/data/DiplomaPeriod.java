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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class DiplomaPeriod extends DataCommon {
    @Transient
    public List<Signature> signatures = new ArrayList();
    
    private Map<String, Signature> allSignatures = new HashMap();
    public String backgroundImage;
    public String textColor;
    public Date startDate;
    public Date stopDate;
    
    public void addUser(User user) {
        if (allSignatures.get(user.id) != null) {
            return;
        }
        
        Signature signature = new Signature();
        signature.userid = user.id;
         
        allSignatures.remove(user.id);
        allSignatures.put(user.id, signature);
    }

    public void addSignature(String userid, String signature) {
        System.out.println("H");
        Signature sign = allSignatures.get(userid);
        if (sign != null) {
            sign.signature = signature;
        }
    }

    public void unsetSignature(String userId) {
        Signature sign = allSignatures.get(userId);
        if (sign != null) {
            sign.signature = null;
        }
    }
    
    public void finalizeObject() {
        signatures = new ArrayList<>(allSignatures.values());
    }
}
