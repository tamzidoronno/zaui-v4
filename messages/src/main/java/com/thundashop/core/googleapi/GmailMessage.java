/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.googleapi;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class GmailMessage extends DataCommon {
    /**
     * This is the ID from the Gmail Message
     */
    public String messageId;
    public String threadId;
    public Date date = null;
    public String rawMessage;
    public long historyId;
    public String snippet;
   
}
