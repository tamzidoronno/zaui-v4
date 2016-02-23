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
public class MailMessage extends DataCommon {
    public String to = "";
    public String subject = "";
    public String content = "";
    public String status = "not_delivered";
    boolean hasAttachments = false;
}
