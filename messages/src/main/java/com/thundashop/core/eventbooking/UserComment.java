/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class UserComment implements Serializable {
    public String commentId = UUID.randomUUID().toString();
    
    public Date commentCreatedDate = new Date();
    public String comment = "";
    public String userId;
    public String addedByUserId = "";
}
