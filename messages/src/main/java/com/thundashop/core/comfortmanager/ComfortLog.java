/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.comfortmanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.PermenantlyDeleteData;

/**
 *
 * @author boggi
 */
@PermenantlyDeleteData
public class ComfortLog extends DataCommon {
    public String pmsEvent = "";
    public String roomId = "";
    public String text = "";
    public ComfortState state = null;
}
