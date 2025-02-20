/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class UserQuestionAnswer implements Serializable {
    public int percentageOfCorrect = 0;
    public boolean hasAnsweredCorrectly = false;
    public boolean hasAnswered = false;
    public String questionId = "";
    public int tries = 0;
    public String text = "";
    public List<String> answers;
    public String reply = "";
    public Date answerDate = null;
    public String answerId = UUID.randomUUID().toString();
    
    @Transient
    public QuestBackQuestion question;
    
    @Transient
    public QuestBackQuestion parent;
    
}
