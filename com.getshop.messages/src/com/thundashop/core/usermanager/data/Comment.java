/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class Comment implements Serializable {
    private Date dateCreated = new Date();
    
    private String commentId = UUID.randomUUID().toString();
    
    public Date getDateCreated() {
        return dateCreated;
    }
    
    public String getCommentId() {
        return commentId;
    }
    
    /**
     * The application that posted the comment.
     */
    public String appId;
    
    /**
     * The comment itself.
     */
    public String comment;

    /**
     * Extra information if nescerry.
     * Could be used for storing an id.
     */
    public String extraInformation;
    
    /**
     * The editor / administrator that added this comment.
     */
    public String createdByUserId;
}
