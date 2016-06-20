/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.questback.data.QuestBackOption;
import com.thundashop.core.questback.data.QuestBackQuestion;
import com.thundashop.core.questback.data.QuestBackResult;
import com.thundashop.core.questback.data.QuestTest;
import com.thundashop.core.questback.data.QuestionTreeItem;
import com.thundashop.core.questback.data.ResultRequirement;
import com.thundashop.core.questback.data.UserTestResult;
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
    
    public String getQuestionTitle(String pageId);
    
    @Administrator
    public QuestTest createTest(String testName);
    
    @Administrator
    public QuestTest saveTest(QuestTest test);
    
    @Administrator
    public List<QuestTest> getAllTests();
    
    @Administrator
    public void deleteTest(String testId);
    
    public QuestTest getTest(String testId);
    
    
    /**
     * Return a list of all tests that the logged in user is assigned to.
     * 
     * @return 
     */
    @Customer
    public List<QuestTest> getTests();
    
    public List<QuestTest> getTestsForUser(String userId);
    
    public String getNextQuestionPage(String testId);
    
    public String answerQuestions(String testId, String applicationId, String pageId, List<String> answers);
    
    public boolean hasAnswered(String pageId, String testId);
    
    @Administrator
    public void assignUserToTest(String testId, String userId);
    
    public int getProgress(String testId);
    
    @Customer
    public int getProgressForUser(String userId, String testId);
    
    @Administrator
    public UserTestResult getTestResults(String userId, String testId);
    
    @Customer
    public UserTestResult getTestResultForUser(String testId, String userId);
    
    @Customer
    public UserTestResult getTestResult(String testId);
    
    @Customer
    public List<QuestBackQuestion> getCategories();
    
    @Administrator
    public void saveQuestBackResultRequirement(ResultRequirement requirement);
    
    @Customer
    public ResultRequirement getResultRequirement();
    
    @Customer
    public QuestBackQuestion getQuestion(String id);
    
    @Customer
    public UserTestResult getBestCategoryResultForCompany(String userId, String catId);   
    
    /**
     * Returns the calculated result in percent (0 - 100)
     * @param userId
     * @param testId
     * @return 
     */
    public Integer getScoreForTest(String userId, String testId);
    
    public List<QuestBackQuestion> getCategoriesForTest(String testId);
    
    @Editor
    public QuestBackResult getResult(String testId);
    
    @Editor
    public QuestBackResult getResultWithReference(String testId, String referenceId);
    
    @Administrator
    public void sendQuestBack(String testId, String userId, String reference);
    
    @Editor
    public List<QuestBackOption> getOptionsByPageId(String pageId);
    
    @Editor
    public String getTypeByPageId(String pageId);
}