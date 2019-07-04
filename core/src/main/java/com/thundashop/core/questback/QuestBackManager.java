/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.ApplicationInstance;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.common.Settings;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.eventbooking.Event;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.questback.data.QuestBackOption;
import com.thundashop.core.questback.data.QuestBackQuestion;
import com.thundashop.core.questback.data.QuestBackResult;
import com.thundashop.core.questback.data.QuestBackSendt;
import com.thundashop.core.questback.data.QuestTest;
import com.thundashop.core.questback.data.QuestionTreeItem;
import com.thundashop.core.questback.data.ResultRequirement;
import com.thundashop.core.questback.data.ResultUserAnswer;
import com.thundashop.core.questback.data.UserQuestionAnswer;
import com.thundashop.core.questback.data.UserTestResult;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private StoreApplicationPool storeApplicationPool; 
    
    @Autowired
    private StoreApplicationInstancePool storeApplicationInstancePool; 
    
    @Autowired
    private MessageManager messageManager;
    
    private Map<String, QuestBackQuestion> questions = new HashMap();
    
    private Map<String, QuestTest> tests = new HashMap();
    
    private ResultRequirement resultRequirement = null;

    private List<UserTestResult> results = new ArrayList();
    
    private List<QuestBackSendt> sentQuestBacks = new ArrayList();
    
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
            results.add(result);
        }
        
        if (o instanceof ResultRequirement) {
            this.resultRequirement = (ResultRequirement)o;
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
        
        checkForEmtyNodeIdsOn(items, applicationId);
        
        for (QuestionTreeItem item : items) {
            checkItem(item, null);
        }
    }

    private void checkItem(QuestionTreeItem item, String parentId) {
        for (QuestionTreeItem child : item.children) {
            checkItem(child, item.li_attr.nodeid);
        }
        
        if (item.li_attr.nodeid != null && !item.li_attr.nodeid.isEmpty() && questions.get(item.li_attr.nodeid) == null) {
            createNewQuestion(item.li_attr.nodeid);
        }
        
        QuestBackQuestion quest = questions.get(item.li_attr.nodeid);
        if (quest != null) {
            if (!quest.parentId.equals(parentId)) {
                quest.parentId = parentId;
                saveObject(quest);
            }
            
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
        QuestTest test = tests.get(testId);
        
        User user = getUserByTest(test);
        UserTestResult result = getResultTest(testId, user.id);
        
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

    private User getUserByTest(QuestTest test) {
        User user = null;
        
        if (test.type.equals("questback")) {
            user = userManager.getVirtualSessionUser();
        } else {
            user = getSession().currentUser;
        }
        
        if (user == null) {
            throw new ErrorException(26);
        }    
        
        return user;
    }
    
    private UserTestResult getResultTest(String testId, String userId) {
        if (userId == null || testId == null) {
            return null;
        }
        
        UserTestResult result = results.stream()
            .filter( answer -> answer.userId.equals(userId) && answer.testId != null && answer.testId.equals(testId))
            .findFirst()
            .orElse(null);
        
        if (result == null) {
            result = new UserTestResult();
            result.testId = testId;
            result.userId = userId;
            results.add(result);
        }
        
        finalizeTestResult(result);
        return result;
    }

    @Override
    public String answerQuestions(String testId, String applicationId, String pageId, List<String> answers) {
        if (answers == null) {
            answers = new ArrayList();
        }
        
        QuestTest test = tests.get(testId);
        User user = getUserByTest(test);
        UserTestResult testResult = getResultTest(testId, user.id);
        QuestBackQuestion question = getQuestionBasedOnPageId(pageId);
        ApplicationInstance application = instancePool.getApplicationInstance(applicationId);
        
        boolean allCorrect = true;
        String text = "";
        
        if (application.getSetting("type").equals("4")) {
            allCorrect = checkPicutureAnswer(application, answers);
        } else if (application.getSetting("type").equals("3")) {
            text = answers.get(0);
            allCorrect = true;
        } else {
            allCorrect = checkAnswer(application, answers);
        }
        
        testResult.answer(question.id, allCorrect, text, answers);
        saveObject(testResult);

        if (!allCorrect && test.forceCorrectAnswer) {
            return "wrong";
        }
        
        return getNextQuestionPage(test.id);
    }

    private boolean checkAnswer(ApplicationInstance application, List<String> answers) throws JsonSyntaxException {
        boolean allCorrect = true;
        
        String jsonEncodedAnswers = application.getSetting("options");
        Gson gson = new Gson();
        List<QuestBackOption> options = gson.fromJson(jsonEncodedAnswers, new TypeToken<ArrayList<QuestBackOption>>(){}.getType());
        // Checks if a correct answer has not been selected
        for (QuestBackOption option : options) {
            if (option.correctAnswer && !answers.contains(option.id)) {
                allCorrect = false;
            }
        }
        // Checks if a wrong answer has been selected
        for (QuestBackOption option : options) {
            for (String optionId : answers) {
                if (option.id.equals(optionId) && !option.correctAnswer) {
                    allCorrect = false;
                }
            }
        }
        return allCorrect;
    }

    private boolean checkPicutureAnswer(ApplicationInstance application, List<String> answers) {
        boolean allCorrect = true;
        
        try {
            int x = Integer.parseInt(application.getSetting("x"));
            int y = Integer.parseInt(application.getSetting("y"));
            int x2 = Integer.parseInt(application.getSetting("x2"));
            int y2 = Integer.parseInt(application.getSetting("y2"));
            int ux = Integer.parseInt(answers.get(0));
            int uy = Integer.parseInt(answers.get(1));
            
            if (!(x <= ux && ux <= x2 && y <= uy && uy <= y2)) {
                allCorrect = false;
            }
        } catch (NumberFormatException x) {
            allCorrect = false;
        }
        
        return allCorrect;
    }

    @Override
    public boolean hasAnswered(String pageId, String testId) {
        QuestTest test = getTest(testId);
        if (test == null)
            testDeleted();
        
        User user = getUserByTest(test);
        QuestBackQuestion question = getQuestionBasedOnPageId(pageId);
        return getResultTest(test.id, user.id).hasAnswered(question.id, test.forceCorrectAnswer);
    }

    private void testDeleted() {
        throw new ErrorException(103);
    }

    @Override
    public void assignUserToTest(String testId, String userId) {
        QuestTest test = assignUserToTestInternal(testId, userId);
        sendMail(userId, test);
    }

    private QuestTest assignUserToTestInternal(String testId, String userId) throws ErrorException {
        QuestTest test = getTest(testId);
        if (test == null)
            testDeleted();
        if (!test.userIds.contains(userId)) {
            test.userIds.add(userId);
            saveObject(test);
        }
        return test;
    }

    private void sendMail(String userId, QuestTest test) {
        User user = userManager.getUserById(userId);
        
        Application application = storeApplicationPool.getApplication("3ff6088a-43d5-4bd4-a5bf-5c371af42534");
        if (application.getSetting("shouldSendEmail").equals("true")) {
            String subject = application.getSetting("ordersubject");
            String message = application.getSetting("orderemail");
            message = manipulateText(message, user, test);
            subject = manipulateText(subject, user, test);
            
            messageManager.sendMail(user.emailAddress, user.fullName, subject, message, "post@getshop.com", null);
        }
    }

    private String manipulateText(String message, User user, QuestTest test) {
        message = message.replaceAll("(\r\n|\n)", "<br />");
        message = message.replace("{User.Name}", user.fullName);
        message = message.replace("{Test.Name}", test != null ? test.name : "");
        message = message.replace("{User.Email}", user.emailAddress);
        
        return message;
    }

    @Override
    public int getProgress(String testId) {
        QuestTest test = getTest(testId);
        User user = getUserByTest(test);
        return getProgressForUser(user.id, testId);
        
    }
    
    public int getProgressForUser(String userId, String testId) {
        User user = userManager.getUserById(userId);
        
        if (user == null) {
            return 0;
        }
        
        if (!getTest(testId).type.equals("questback")) {
            userManager.checkUserAccess(user);
        }
        
        UserTestResult testResult = getResultTest(testId, user.id);
        if (testResult == null) {
            return 0;
        }
        
        if (getTest(testId).questions.isEmpty()) {
            return 100;
        }
        
        double percent = (double)testResult.answers.size() / (double)getTest(testId).questions.size();
        percent = percent * 100;
        
        return (int)percent;
    }

    @Override
    public UserTestResult getTestResults(String userId, String testId) {
        return getResultTest(testId, userId);
    }

    @Override
    public UserTestResult getTestResult(String testId) {
        return getResultTest(testId, getSession().currentUser.id);
    }

    private void finalizeTestResult(UserTestResult result) {
        for (UserQuestionAnswer answer : result.answers) {
            answer.question = questions.get(answer.questionId);
            answer.parent = questions.get(answer.question.parentId);
        }
    }
    
    @Override
    public List<QuestBackQuestion> getCategories() {
        return questions.values().stream()
                .filter(o -> o.parentId == null || o.parentId.isEmpty())
                .collect(Collectors.toList());
       
    }

    @Override
    public void saveQuestBackResultRequirement(ResultRequirement requirement) {
        saveObject(requirement);
        this.resultRequirement = requirement;
    }

    @Override
    public ResultRequirement getResultRequirement() {
        return this.resultRequirement;
    }
    
    @Override
    public QuestBackQuestion getQuestion(String id) {
        QuestBackQuestion ret = questions.get(id);
        return ret;
    }

    @Override
    public UserTestResult getTestResultForUser(String testId, String userId) {
        User user = userManager.getUserById(userId);
        
        if (user == null) {
            return null;
        }
        
        userManager.checkUserAccess(user);
        return getResultTest(testId, userId);
    }

    @Override
    public UserTestResult getBestCategoryResultForCompany(String testId, String catId) {
        User loggedOnUser = userManager.getLoggedOnUser();
        if (loggedOnUser.companyObject == null) {
            return null;
        }
        
        List<User> users = userManager.getUsersByCompanyId(loggedOnUser.companyObject.id);
        int topResult = 0;
        UserTestResult retRes = null;
        
        for (User user : users) {
            UserTestResult result = getResultTest(testId, user.id);
            
            if (result == null) {
                continue;
            }
            
            int res = result.answers
                    .stream()
                    .filter(o -> o.parent.id.equals(catId))
                    .mapToInt(o -> o.percentageOfCorrect).sum();
            
            if (res >= topResult) {
                topResult = res;
                retRes = result;
            }
        }
        
        return retRes;
    }

    @Override
    public List<QuestTest> getTestsForUser(String userId) {
        User user = userManager.getUserById(userId);
        
        if (user == null) {
            return new ArrayList();
        }
        
        userManager.checkUserAccess(user);
        
        return tests.values().stream()
            .filter(test -> test.userIds.contains(userId))
            .collect(Collectors.toList());
    }

    @Override
    public Integer getScoreForTest(String userId, String testId) {
        User user = userManager.getUserById(userId);
        userManager.checkUserAccess(user);
        
        UserTestResult testResult = getTestResultForUser(testId, userId);
        if (testResult != null) {
            return testResult.getAverageResult();
        }
        
        return null;
    }

    @Override
    public List<QuestBackQuestion> getCategoriesForTest(String testId) {
        QuestTest test = getTest(testId);
        if (test == null)
            return new ArrayList();
        
        User user = getUserByTest(test);
        UserTestResult testResult = getResultTest(testId, user.id);
        
        if (testResult == null) {
            return new ArrayList();
        }
        
        List<QuestBackQuestion> returnResult = test.questions.stream()
                .map(o -> getQuestion(o))
                .map(question -> getQuestion(question.parentId))
                .filter(o -> o != null)
                .distinct()
                .collect(Collectors.toList());
        
        return returnResult;
    }

    @Override
    public QuestBackResult getResult(String testId) {
        List<UserTestResult> testResults = results.stream()
                .filter(result -> result.testId.equals(testId))
                .collect(Collectors.toList());

        QuestBackResult res = new QuestBackResult();
        
        for (UserTestResult testResult : testResults) {
            String referenceId = getReference(testResult.userId);
            final User user = userManager.getUserById(referenceId);
            
            for (UserQuestionAnswer ans : testResult.answers) {
                List<ResultUserAnswer> addAnswers = ans.answers.stream()
                        .map(ians -> new ResultUserAnswer(ians, user, ans))
                        .collect(Collectors.toList());
                res.addAnswers(ans.questionId, addAnswers);
            }
        }
                 
        return res;
    }

    @Override
    public void sendQuestBack(String testId, String userId, String reference, Event event) {
        QuestTest test = getTest(testId);
        sendQuestBackMail(userId, test, reference, event);
        saveSentQuestback(testId, userId, reference);
    }

    
    public void sendQuestBackMail(String userId, QuestTest test, String reference, Event event) {
        User user = userManager.getUserById(userId);
        
        Application application = storeApplicationPool.getApplication("3ff6088a-43d5-4bd4-a5bf-5c371af42534");
        
        String subject = application.getSetting("questback_subject");
        String message = application.getSetting("questback_body");
        
        message = manipulateText(message, user, test);
        subject = manipulateText(subject, user, test);
        
        if (event != null) {
            message = message.replace("{Event.Name}", event.bookingItemType.name);
            message = message.replace("{Event.Location}", event.location.name + " - " + event.subLocation.name);
        }
        
        String testLink = "http://" + getStoreDefaultAddress() + "/?page=do_questback&gs_testId=" + test.id + "&referenceId="+reference;
                     
        message = message.replace("{Test.Link}", "<a href='"+testLink+"'>"+testLink+"</a>");

        String storeEmail = getStoreEmailAddress();
        messageManager.sendMail(user.emailAddress, user.fullName, subject, message, storeEmail, null);
    }

    @Override
    public QuestBackResult getResultWithReference(String testId, String referenceId) {
        List<UserTestResult> testResults = results.stream()
                .filter(result -> result.testId.equals(testId))
                .filter(result -> hasReference(result.userId, referenceId))
                .collect(Collectors.toList());

        QuestBackResult res = new QuestBackResult();
        
        for (UserTestResult testResult : testResults) {
            for (UserQuestionAnswer ans : testResult.answers) {
                
                List<ResultUserAnswer> addAnswers = ans.answers.stream()
                        .map(ians -> new ResultUserAnswer(ians, null, ans))
                        .collect(Collectors.toList());
                
                res.addAnswers(ans.questionId, addAnswers);
            }
        }
                
        return res;
    }
    
    private String getReference(String userId) {
        User user = userManager.getUserById(userId);
        if (user == null)
            return null;
        
        return user.metaData.get("questback_referenceId");
    }

    private boolean hasReference(String userId, String referenceId) {
        User user = userManager.getUserById(userId);
        if (user == null)
            return false;
        
        return user.metaData.get("questback_referenceId") != null && user.metaData.get("questback_referenceId").equals(referenceId);
    }

    @Override
    public List<QuestBackOption> getOptionsByPageId(String pageId) {
        ApplicationInstance application = pageManager.getApplicationsForPage(pageId).stream()
                .filter(app -> app.appSettingsId.equals("07422211-7818-445e-9f16-ad792320cb10"))
                .findFirst()
                .orElse(null);
        
        if (application != null) {
            String jsonEncodedAnswers = application.getSetting("options");
            Gson gson = new Gson();
            List<QuestBackOption> options = gson.fromJson(jsonEncodedAnswers, new TypeToken<ArrayList<QuestBackOption>>(){}.getType());
            return options;
        }
        
        return null;
    }

    @Override
    public String getTypeByPageId(String pageId) {
        ApplicationInstance appInstance = pageManager.getApplicationsForPage(pageId).stream()
                .filter(app -> app.appSettingsId.equals("07422211-7818-445e-9f16-ad792320cb10"))
                .findFirst()
                .orElse(null);
        
        if (appInstance != null) {
            return appInstance.getSetting("type");
        }
        
        return null;
    }

    @Override
    public boolean isQuestBackSent(String userId, String testId, String reference) {
        return sentQuestBacks.stream()
                .filter(o -> o.testId.equals(testId) && o.userId.equals(userId) && o.reference.equals(reference))
                .count() > 0;
    }

    private void saveSentQuestback(String testId, String userId, String reference) {
        QuestBackSendt sent = new QuestBackSendt();
        sent.testId = testId;
        sent.userId = userId;
        sent.reference = reference;
        
        sentQuestBacks.add(sent);
        saveObject(sent);
    }

    /**
     * Returns an avarage value of the test results, does not take into concideration if a user has not 
     * answered the test.
     * 
     * @param testId
     * @return 
     */
    @Override
    public Integer getCompanyScoreForTestForCurrentUser(String testId) {
        if (getSession() == null || getSession().currentUser == null || getSession().currentUser.companyObject == null)
            return 0;
        
        List<User> users = userManager.getUsersByCompanyId(getSession().currentUser.companyObject.id);
        for (User user : users) {
            userManager.checkUserAccess(user);
        }
        
        HashMap<String, Integer> allResults = new HashMap();
        for (User user : users) {
            UserTestResult testResult = getResultTest(testId, user.id);
            if (testResult == null || testResult.answers.isEmpty())
                continue;
            
            allResults.put(user.id, testResult.getAverageResult());
        }
        
        if (allResults.isEmpty())
            return 0;
        
        int summary = allResults.values().stream().mapToInt(o -> o.intValue()).sum();
        int avg = summary / allResults.size();
        
        return avg;
    }

    @Override
    public void assignTestsToUsers(List<String> testIds, List<String> userids) {
        if (testIds == null || testIds.isEmpty())
            return;
        
        for (String testId : testIds) {
            for (String userId : userids) {
                assignUserToTestInternal(testId, userId);
            }
        }
        
        for (String userId : userids) {
            sendMail(userId, null);
        }
    }

    @Override
    public void saveQuestBackAnswerResponse(String answerId, String answer) {
        for(UserTestResult res : results) {
            for(UserQuestionAnswer questanswer : res.answers) {
                if(questanswer.answerId.equals(answerId)) {
                    questanswer.reply = answer;
                    saveObject(res);
                }
            }
        }
        
    }
    
    /**
     * Returns a Base64 encoded excellist with all the data
     * 
     * @return 
     */
    public String exportToExcel() {
        List<QuestBackQuestion> topQuestions = questions.values().stream()
                .filter(q -> q.parentId == null || q.parentId.isEmpty())
                .collect(Collectors.toList());
        
        Gson gson = new Gson();
        
        for (QuestBackQuestion question : topQuestions) {
            List<QuestBackQuestion> subQuestions = questions.values().stream()
                .filter(q -> q.parentId != null && q.parentId.equals(question.id))
                .collect(Collectors.toList());
            
            for (QuestBackQuestion subQuestion : subQuestions) {
                
                Page page = pageManager.getPage(subQuestion.pageId);
                
                List<ApplicationInstance> instances = page.getCellsFlatList().stream()
                        .map(cell -> cell.appId)
                        .map(appId -> storeApplicationInstancePool.getApplicationInstance(appId))
                        .filter(appInstance -> appInstance != null && appInstance.appSettingsId != null && appInstance.appSettingsId.equals("07422211-7818-445e-9f16-ad792320cb10"))
                        .collect(Collectors.toList());
                
                if (!instances.isEmpty()) {
                    String jsonEncodedAnswers = instances.get(0).getSetting("options");
                    String type = instances.get(0).getSetting("type");
                    String headingText = instances.get(0).getSetting("headingtext");
                    List<QuestBackOption> options = gson.fromJson(jsonEncodedAnswers, new TypeToken<ArrayList<QuestBackOption>>(){}.getType());
                }
            }
        }
        
        return "";
    }

    @Override
    public String importExcel(String base64, String language) {
        List<QuestBackImportRow>  treeList = new QuestBackImporter(instancePool, pageManager, language).importExcel(base64);
        questionTreeChanged("eed3197f-8e24-4cc1-a03e-95931f572653");
        
        for (QuestBackImportRow topLevel : treeList) {
            for (QuestBackImportRow question : topLevel.children) {
                Page originalPage = pageManager.getPage(question.col4);
                
                String pageId = getPageId(question.col4 + "_" + language);
                Page page = pageManager.getPage(pageId);
                
                if (originalPage != null && originalPage.getCell("dbe86a45-7353-474c-ac78-55c9bf838c78") != null && originalPage.getCell("dbe86a45-7353-474c-ac78-55c9bf838c78").appId != null && !originalPage.getCell("dbe86a45-7353-474c-ac78-55c9bf838c78").appId.isEmpty()) {
                    ApplicationInstance application = instancePool.makeDuplicatedApplication(originalPage.getCell("dbe86a45-7353-474c-ac78-55c9bf838c78").appId);
                    
                    page.addApplication("dbe86a45-7353-474c-ac78-55c9bf838c78", application.id);
                    pageManager.savePage(page);
                }
                
                List<ApplicationInstance> instances = page.getCellsFlatList().stream()
                        .map(cell -> cell.appId)
                        .map(appId -> storeApplicationInstancePool.getApplicationInstance(appId))
                        .filter(appInstance -> appInstance != null && appInstance.appSettingsId != null && appInstance.appSettingsId.equals("07422211-7818-445e-9f16-ad792320cb10"))
                        .collect(Collectors.toList());
                
                if (!instances.isEmpty()) {
                    setSettingsToApplication("type", question.col3, instances.get(0).id);
                    
                    String headerText = getHeaderText(question.children);
                    if (headerText != null) {
                        setSettingsToApplication("headingtext", headerText, instances.get(0).id);
                    }
                    
                    List<QuestBackOption> options = getOptions(question.children);
                    Gson gson = new Gson();
                    setSettingsToApplication("options", gson.toJson(options), instances.get(0).id);
                }
            }
        }
        return "";
    }
    
    public void setSettingsToApplication(String key, String value, String appId) {
        Setting setting = new Setting();
        setting.id = key;
        setting.value = value;
        setting.name = key;
        setting.secure = false;
        
        Settings settings = new Settings();
        settings.settings = new ArrayList();
        settings.settings.add(setting);
        
        settings.appId = appId;
        
        instancePool.setApplicationSettings(settings);
    }
    
    private List<QuestBackOption> getOptions(List<QuestBackImportRow> children) {
        List<QuestBackOption> retList = new ArrayList();
        
        for (QuestBackImportRow row : children) {
            if (row.col5 != null && !row.col5.equals("heading")) {
                QuestBackOption option = new QuestBackOption();
                option.correctAnswer = row.col5.equals("TRUE");
                option.text = row.col4;
                option.id = UUID.randomUUID().toString();
                
                retList.add(option);
            }
        }
        
        return retList;
    }

    private String getHeaderText(List<QuestBackImportRow> children) {
        for (QuestBackImportRow row : children) {
            if (row.col5 != null && row.col5.equals("heading")) {
                return row.col4;
            }
        }
        
        return null;
    }

    private void checkForEmtyNodeIdsOn(ArrayList<QuestionTreeItem> items, String applicationId) {
        if (items == null) 
            return;
        
        if (items.size() == 1) {
            QuestionTreeItem item = items.get(0);
            boolean found = false;
            for (QuestionTreeItem iItem : item.children) {
                if (iItem.li_attr.nodeid.isEmpty()) {
                    iItem.li_attr.nodeid = UUID.randomUUID().toString();
                    found = true;
                }
            }
            
            Gson gson = new Gson();
            setSettingsToApplication("list", gson.toJson(items), applicationId);
        }
    }

}