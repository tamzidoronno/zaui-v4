/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.chatmanager;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class ChatMessage extends DataCommon implements Comparable<ChatMessage> {
    public String message;
    public String userId;

    @Override
    public int compareTo(ChatMessage o) {
        if (o.rowCreatedDate.after(rowCreatedDate))
            return -1;
     
        if (o.rowCreatedDate.before(rowCreatedDate))
            return 1;
        
        return 0;
    }
}
