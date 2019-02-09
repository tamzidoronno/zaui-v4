/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class CashPoint extends DataCommon {
    public String cashPointName = "";
    public List<String> productListIds = new ArrayList();
    
    /**
     * Key = userId;
     * Value = viewId;
     */
    public HashMap<String, String> selectedUserView = new HashMap();
    
    public String receiptPrinterGdsDeviceId = "";
    public String kitchenPrinterGdsDeviceId = "";
    
    public String departmentId = "";

    public void setUserView(String userId, String viewId) {
        selectedUserView.put(userId, viewId);
    }
}
