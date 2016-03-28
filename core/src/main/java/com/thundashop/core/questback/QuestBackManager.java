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
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.questback.data.QuestBackOption;
import com.thundashop.core.questback.data.QuestBackQuestion;
import com.thundashop.core.questback.data.QuestTest;
import com.thundashop.core.questback.data.QuestionTreeItem;
import com.thundashop.core.questback.data.ResultRequirement;
import com.thundashop.core.questback.data.UserQuestionAnswer;
import com.thundashop.core.questback.data.UserTestResult;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
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
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private StoreApplicationPool storeApplicationPool;
    
    @Autowired
    private MessageManager messageManager;
    
    private Map<String, QuestBackQuestion> questions = new HashMap();
    
    private Map<String, QuestTest> tests = new HashMap();
    
    private ResultRequirement resultRequirement = null;

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
        UserTestResult testResult = getResultTest(testId, getSession().currentUser.id);
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
        
        testResult.answer(question.id, allCorrect, text);
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
        
        QuestBackQuestion question = getQuestionBasedOnPageId(pageId);
        return getResultTest(test.id, getSession().currentUser.id).hasAnswered(question.id, test.forceCorrectAnswer);
    }

    private void testDeleted() {
        throw new ErrorException(103);
    }

    @Override
    public void assignUserToTest(String testId, String userId) {
        QuestTest test = getTest(testId);
        if (test == null)
            testDeleted();
        
        if (!test.userIds.contains(userId)) {
            test.userIds.add(userId);
            saveObject(test);
        }
        
        sendMail(userId, test);
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
        message = message.replace("{Test.Name}", test.name);
        message = message.replace("{User.Email}", user.emailAddress);
        
        return message;
    }

    @Override
    public int getProgress(String testId) {
        return getProgressForUser(getSession().currentUser.id, testId);
        
    }
    
    public int getProgressForUser(String userId, String testId) {
        User user = userManager.getUserById(userId);
        
        if (user == null) {
            return 0;
        }
        
        userManager.checkUserAccess(user);
        
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
        return questions.get(id);
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
}