/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.googleapi;

import java.util.Date;

/**
 *
 * @author ktonder
 */
public class GmailMessageAssigned {
    public String assignedBy;
    public String assignedTo;
    public Date date = new Date();
    public boolean deleted = false;
}
