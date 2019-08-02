/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.thundashop.core.ticket.TicketManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.webmanager.WebManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class PushOver {
    @Autowired
    private WebManager webManager;
    
    @Autowired
    private UserManager userManager;
    
    private final String apiToken = "affey1yavkp5cg63vmiyxbzha98mrg";
    
    public void push(String userId, String title, String message) {
        User user = userManager.getUserById(userId);
        
        if (user != null) {
            String userToken = user.metaData.get("pushovertoken");
            
            if (userToken != null && !userToken.isEmpty()) {
                try {
                    webManager.htmlPost("https://api.pushover.net/1/messages.json", "token="+this.apiToken+"&user="+userToken+"&message="+message+"&title="+title, false, "UTF-8");
                } catch (Exception ex) {
                    Logger.getLogger(TicketManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
