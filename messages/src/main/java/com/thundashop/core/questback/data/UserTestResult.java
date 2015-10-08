/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class UserTestResult extends DataCommon {
    public List<UserQuestionAnswer> answers = new ArrayList();
    public String testId = "";
    public String userId = "";

    public boolean hasAnswered(String questionId, boolean forceAnswer) {
        UserQuestionAnswer answerForQuestion = answers.stream()
                .filter(o -> o.questionId.equals(questionId))
                .findFirst()
                .orElse(null);
        
        if (answerForQuestion == null) {
            return false;
        }
        
        if (forceAnswer) {
            return answerForQuestion.hasAnsweredCorrectly;
        }
        
        return answerForQuestion.tries > 0;
    }

    public void answer(String id, boolean allCorrect) {
        UserQuestionAnswer answerForQuestion = answers.stream()
                .filter(o -> o.questionId.equals(id))
                .findFirst()
                .orElse(null);
        
        if (answerForQuestion == null) {
            answerForQuestion = new UserQuestionAnswer();
            answerForQuestion.questionId = id;
            answers.add(answerForQuestion);
        }
        
        answerForQuestion.tries++;
        answerForQuestion.hasAnsweredCorrectly = allCorrect;
    }
}
