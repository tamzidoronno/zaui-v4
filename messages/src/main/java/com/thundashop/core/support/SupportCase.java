/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.support;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class SupportCase extends DataCommon {
    public Integer type;
    public Integer module;
    public Integer state;
    String byStoreId = "";
    String byUserName = "";
    String title = "";
    public Date closedDate;
    String handledByUser = "";
    public String featureRequestId = "";
    public List<SupportCaseHistory> history = new ArrayList();
    
    @Transient
    Integer minutesSpent;
    @Transient
    String emailAdress;
    @Transient
    String webAddress;

    void finalizeCase() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
