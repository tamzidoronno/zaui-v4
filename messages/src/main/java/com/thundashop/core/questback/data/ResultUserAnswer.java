/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback.data;

import com.thundashop.core.usermanager.data.User;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class ResultUserAnswer {

    public ResultUserAnswer(String answer, User user, UserQuestionAnswer ans) {
        this.answer = answer;
        this.answerId = ans.answerId;
        this.reply = ans.reply;
        
        if (user != null)
            this.userId = user.id;
    }
    
    public String answer;
    public String userId;
    public String reply = "";
    public String answerId = "";
}

