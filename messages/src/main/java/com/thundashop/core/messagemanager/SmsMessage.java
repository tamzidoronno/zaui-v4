/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class SmsMessage extends DataCommon {
    public String to = "";
    public String from = "";
    public String message = "";
    public String prefix = "";
    public String status = "not_delivered";
    public String response = "";
}
