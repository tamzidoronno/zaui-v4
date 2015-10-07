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
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.questback.data.QuestBackQuestion;
import com.thundashop.core.questback.data.QuestionTreeItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        
        System.out.println("test: " + questions.get(nodeId).pageId);
        return questions.get(nodeId).pageId;
    }

    @Override
    public String getQuestionTitle(String pageId) {
        QuestBackQuestion question = questions.values().stream()
                .filter(o -> o.pageId != null && o.pageId.equals(pageId))
                .findFirst().orElse(null);
        
        if (question != null) {
            return question.name;
        }
            
        return "";
    }
}