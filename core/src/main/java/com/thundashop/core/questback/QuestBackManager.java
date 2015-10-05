/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.questback.data.QuestBackQuestion;
import java.util.HashMap;
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
    
    @Override
    public QuestBackQuestion createNewQuestion(String name, int type) {
        QuestBackQuestion question = new QuestBackQuestion();
        question.name = name;
        saveObject(question);
        
        Page templateCopies = pageManager.createPageFromTemplatePage("questback_template_1");
        question.pageId = templateCopies.id;
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
}