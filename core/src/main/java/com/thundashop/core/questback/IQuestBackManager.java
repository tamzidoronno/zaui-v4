/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.questback.data.QuestTest;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IQuestBackManager {
    

    @Administrator
    public void createTemplatePageIfNotExists();
    
    @Administrator
    public void questionTreeChanged(String applicationId);
    
    @Customer
    public String getPageId(String questionId);
    
    @Customer
    public String getQuestionTitle(String pageId);
    
    @Administrator
    public QuestTest createTest(String testName);
    
    @Administrator
    public QuestTest saveTest(QuestTest test);
    
    @Administrator
    public List<QuestTest> getAllTests();
    
    @Administrator
    public void deleteTest(String testId);
    
    @Customer
    public QuestTest getTest(String testId);
    
    
    /**
     * Return a list of all tests that the logged in user is assigned to.
     * 
     * @return 
     */
    @Customer
    public List<QuestTest> getTests();
    
    @Customer
    public String getNextQuestionPage(String testId);
    
    @Customer
    public String answerQuestions(String testId, String applicationId, String pageId, List<String> answers);
    
    public boolean hasAnswered(String pageId, String testId);
}
