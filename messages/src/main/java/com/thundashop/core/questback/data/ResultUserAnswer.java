/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback.data;

import com.thundashop.core.usermanager.data.User;

/**
 *
 * @author ktonder
 */
public class ResultUserAnswer {

    public ResultUserAnswer(String answer, User user) {
        this.answer = answer;
        if (user != null)
            this.userId = user.id;
    }
    
    
    public String answer;
    public String userId;
}
