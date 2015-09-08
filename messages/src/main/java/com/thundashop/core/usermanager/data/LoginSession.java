/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author boggi
 */
public class LoginSession implements Serializable {
    public Date loginDate;
    public Date logoffDate;
    public String sessionId; 
    public String userId; 
    public int editorActionsCount = 0;
    public int adminActionsCount = 0;

    LoginSession() {
        
    }
    
    LoginSession(User user, String sessionId) {
        this.loginDate = new Date();
        this.userId = user.id;
        this.sessionId = sessionId;
    }
}
