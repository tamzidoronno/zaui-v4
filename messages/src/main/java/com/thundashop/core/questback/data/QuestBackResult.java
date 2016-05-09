/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class QuestBackResult implements Serializable {
    private HashMap<String, List<String>> answers = new HashMap();
    
    public void addAnswers(String questionId, List<String> addAnswers) {
        List<String> memAns = answers.get(questionId);

        if (memAns == null) {
            memAns = new ArrayList();
            answers.put(questionId, memAns);
        }
        
        if (addAnswers != null) {
            memAns.addAll(addAnswers);
        }
    }
}
