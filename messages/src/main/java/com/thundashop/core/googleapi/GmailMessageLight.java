/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.googleapi;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class GmailMessageLight extends DataCommon {
    public String messageId = "";
    public boolean inSent;
    public long historyId;
    public String snippet = "";
    public Date date;
    public String threadId = "";
    public String subject = "";
    public String transferEncoding = "";
    public String contentType = "";
    public String from = "";
    public String deliveredTo = "";
    public String to = "";
    public String replyTo = "";
    private List<GmailMessageAssigned> assignedTo = new ArrayList();
    private GmailMessageLightStatus status = GmailMessageLightStatus.NEW;

    public String connectedToCompanyId = "";
    
    @Transient
    public String connectedToCompanyName = "";
    
    public boolean isUnassigned() {
        return status == GmailMessageLightStatus.UNASSGINED || status == GmailMessageLightStatus.NEW;
    }
    
    /**
     * return true if this message needs processing
     * @return 
     */
    public boolean needsProcessing() {
        return status != GmailMessageLightStatus.ARCHIVED 
                && status != GmailMessageLightStatus.COMPLETED;
    }
    
    public void assignTo(String assignedByUser, String assignedToUserId) {
        assignedTo.stream().forEach(o -> o.deleted = true);
        
        GmailMessageAssigned assignedToMsg = new GmailMessageAssigned();
        assignedToMsg.assignedBy = assignedByUser;
        assignedToMsg.assignedTo = assignedToUserId;
        assignedTo.add(assignedToMsg);
        status = GmailMessageLightStatus.ASSIGNED;
    }

    public boolean isAssignedTo(String id) {
        return assignedTo.stream()
                .filter(o -> !o.deleted)
                .filter(o -> o.assignedTo.equals(id))
                .count() > 0;
    }
    
    public String getFromEmailAddress() {
        String[] froms = from.split("<");
        if (froms.length == 2) {
            return froms[1].replace(">", "");
        }
        
        return "";
    }

    public String getName() {
        String[] froms = from.split("<");
        if (froms.length == 2) {
            return froms[0];
        }
        return "";
    }

    public void markAsArchived(String id) {
        status = GmailMessageLightStatus.ARCHIVED;
    }    
}