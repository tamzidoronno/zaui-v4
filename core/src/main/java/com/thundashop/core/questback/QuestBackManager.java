/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.common.ApplicationInstance;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.questback.data.QuestBackOption;
import com.thundashop.core.questback.data.QuestBackQuestion;
import com.thundashop.core.questback.data.QuestTest;
import com.thundashop.core.questback.data.QuestionTreeItem;
import com.thundashop.core.questback.data.UserTestResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class QuestBackManager extends ManagerBase implements IQuestBackManager {
    
    @Autowired
    private PageManager pageManager;
    
    @Autowired
    private StoreApplicationInstancePool instancePool;
    
    private Map<String, QuestBackQuestion> questions = new HashMap();
    
    private Map<String, QuestTest> tests = new HashMap();

    private List<UserTestResult> results = new ArrayList();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream().forEach(o -> addObject(o));
    }

    @Override
    public void initialize() {
        super.initialize(); //To change body of generated methods, choose Tools | Templates.
        createTemplatePage();
    }
    
    
    private void createTemplatePage() {
        // Can add multiple template pages.
        
    }
    
    public QuestBackQuestion createNewQuestion(String id) {
        QuestBackQuestion question = new QuestBackQuestion();
        question.id = id;
        
        Page templateCopies = pageManager.createPageFromTemplatePage("questback_template_1");
        question.pageId = templateCopies.id;
        questions.put(question.id, question);
        
        saveObject(question);
        return question;
    }

    private void addObject(DataCommon o) {
        if (o instanceof QuestBackQuestion) {
            QuestBackQuestion quest = (QuestBackQuestion)o;
            questions.put(quest.id, quest);
        }
        
        if (o instanceof QuestTest) {
            QuestTest quest = (QuestTest)o;
            tests.put(quest.id, quest);
        }
        
        if (o instanceof UserTestResult) {
            UserTestResult result = (UserTestResult)o;
            if (results.stream().anyMatch(o2 -> o2.userId.equals(result.userId))) {
                deleteObject(o);
            } else {
                results.add(result);
            }
        }
    }

    @Override
    public void createTemplatePageIfNotExists() {
        String pageId = "questback_template_1";
        Page page = pageManager.getPage(pageId);
        
        if (page == null) {
            page = pageManager.createPageWithName(pageId);
            String cellId = pageManager.addLayoutCell(pageId, null, null, "ROW", "body");
            pageManager.addLayoutCell(pageId, cellId, null, "COLUMN", "body");
            pageManager.addLayoutCell(pageId, cellId, null, "COLUMN", "body");

            cellId = pageManager.addLayoutCell(pageId, null, null, "ROW", "body");
            pageManager.addLayoutCell(pageId, cellId, null, "COLUMN", "body");
        }
    }

    @Override
    public void questionTreeChanged(String applicationId) {
        ApplicationInstance questionApplication = instancePool.getApplicationInstance(applicationId);
        String jsonList = questionApplication.getSetting("list");
        
        Gson gson = new Gson();
        ArrayList<QuestionTreeItem> items = gson.fromJson(jsonList, new TypeToken<ArrayList<QuestionTreeItem>>(){}.getType());
        
        for (QuestionTreeItem item : items) {
            checkItem(item);
        }
    }

    private void checkItem(QuestionTreeItem item) {
        for (QuestionTreeItem child : item.children) {
            checkItem(child);
        }
        
        if (item.li_attr.nodeid != null && !item.li_attr.nodeid.isEmpty() && questions.get(item.li_attr.nodeid) == null) {
            System.out.println("Creating question");
            createNewQuestion(item.li_attr.nodeid);
        }
        
        QuestBackQuestion quest = questions.get(item.li_attr.nodeid);
        if (quest != null) {
            if (quest.name == null || !quest.name.equals(item.text)) {
                quest.name = item.text;
                saveObject(quest);
            }
        }
        
    }

    @Override
    public String getPageId(String nodeId) {
        if (questions.get(nodeId) == null) {
            return "";
        }
        
        return questions.get(nodeId).pageId;
    }
    
    private QuestBackQuestion getQuestionBasedOnPageId(String pageId) {
        return  questions.values().stream()
                .filter(o -> o.pageId != null && o.pageId.equals(pageId))
                .findFirst().orElse(null);
    }

    @Override
    public String getQuestionTitle(String pageId) {
        QuestBackQuestion question = getQuestionBasedOnPageId(pageId);
        
        if (question != null) {
            return question.name;
        }
            
        return "";
    }

    @Override
    public QuestTest createTest(String testName) {
        QuestTest test = new QuestTest();
        test.name = testName;
        saveObject(test);
        tests.put(test.id, test);
        return test;
    }

    @Override
    public List<QuestTest> getAllTests() {
        tests.values().stream().forEach(o -> finalizeTest(o));
        return new ArrayList(tests.values());
    }

    private void finalizeTest(QuestTest o) {
        return;
    }

    @Override
    public void deleteTest(String testId) {
        QuestTest test = tests.remove(testId);
        if (test != null)
            deleteObject(test);
    }

    @Override
    public QuestTest getTest(String testId) {
        return tests.get(testId);
    }

    @Override
    public QuestTest saveTest(QuestTest test) {
        tests.put(test.id, test);
        saveObject(test);
        return test;
    }

    @Override
    public List<QuestTest> getTests() {
        if (getSession() == null || getSession().currentUser == null) {
            return new ArrayList();
        }
        
        String userId = getSession().currentUser.id;
        
        return tests.values().stream()
            .filter(test -> test.userIds.contains(userId))
            .collect(Collectors.toList());
    }

    @Override
    public String getNextQuestionPage(String testId) {
        if (getSession() == null || getSession().currentUser == null) {
            throw new ErrorException(26);
        }
        
        UserTestResult result = getResultTest(testId, getSession().currentUser.id);
        QuestTest test = tests.get(testId);
        
        if (test == null) {
            testDeleted();
        }
        
        for (String questionId : test.questions) {
            if (!result.hasAnswered(questionId, test.forceCorrectAnswer)) {
                return getPageId(questionId);
            }
        }
        
        return "done";
    }
    
    private UserTestResult getResultTest(String testId, String userId) {
        UserTestResult result = results.stream()
            .filter( answer -> answer.userId.equals(userId) && answer.testId.equals(testId))
            .findFirst()
            .orElse(null);
        
        if (result == null) {
            result = new UserTestResult();
            result.testId = testId;
            result.userId = userId;
            results.add(result);
        }
        
        return result;
    }

    @Override
    public String answerQuestions(String testId, String applicationId, String pageId, List<String> answers) {
        QuestTest test = tests.get(testId);
        UserTestResult testResult = getResultTest(testId, getSession().currentUser.id);
        QuestBackQuestion question = getQuestionBasedOnPageId(pageId);
        ApplicationInstance application = instancePool.getApplicationInstance(applicationId);
        
        String jsonEncodedAnswers = application.getSetting("options");
        
        Gson gson = new Gson();
        List<QuestBackOption> options = gson.fromJson(jsonEncodedAnswers, new TypeToken<ArrayList<QuestBackOption>>(){}.getType());
        
        boolean allCorrect = true;
        for (QuestBackOption option : options) {
            if (option.correctAnswer && !answers.contains(option.id)) {
                allCorrect = false;
            }
        }
        
        if (!allCorrect && test.forceCorrectAnswer) {
            return "wrong";
        }
        
        testResult.answer(question.id, allCorrect);
        saveObject(testResult);
        
        return getNextQuestionPage(test.id);
    }

    @Override
    public boolean hasAnswered(String pageId, String testId) {
        QuestTest test = getTest(testId);
        if (test == null)
            testDeleted();
        
        QuestBackQuestion question = getQuestionBasedOnPageId(pageId);
        return getResultTest(test.id, getSession().currentUser.id).hasAnswered(question.id, test.forceCorrectAnswer);
    }

    private void testDeleted() {
        throw new ErrorException(103);
    }
}