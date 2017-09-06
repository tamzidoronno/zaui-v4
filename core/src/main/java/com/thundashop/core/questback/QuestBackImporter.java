/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.common.ApplicationInstance;
import com.thundashop.core.common.Setting;
import com.thundashop.core.common.Settings;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.questback.data.LiAttr;
import com.thundashop.core.questback.data.QuestionTreeItem;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
//
/**
 *
 * @author ktonder
 */
public class QuestBackImporter {
    private StoreApplicationInstancePool instancePool;
    private ArrayList<String> content;
    private String languageName = "eng";
    private PageManager pageManager;

    public QuestBackImporter(StoreApplicationInstancePool instancePool,  PageManager pageManager) {
        this.instancePool = instancePool;
        this.pageManager = pageManager;
    }
    
    
    public List<QuestBackImportRow> importExcel(String base64) {
        QuestBackImporterTree tree = new QuestBackImporterTree();
        List<QuestBackImportRow> treeList = tree.create();
        
        for (QuestBackImportRow row : treeList) {
            QuestionTreeItem topLevel = new QuestionTreeItem();
            topLevel.id = UUID.randomUUID().toString();
            topLevel.text = row.col1 + "(" + languageName + ")";
            
            for (QuestBackImportRow question : row.children) {
                QuestionTreeItem questionLevel = new QuestionTreeItem();
                questionLevel.id = UUID.randomUUID().toString();
                questionLevel.parentId = topLevel.id;
                questionLevel.li_attr = new LiAttr();
                questionLevel.li_attr.nodeid = question.col4 + "_" + languageName;
                questionLevel.li_attr.className = "qb_question";
                
                questionLevel.text = question.col2;
                if (question.col3 != null && !question.col3.isEmpty()) {
                    questionLevel.type = new Integer(question.col3);
                } else {
                    questionLevel.type = 0;
                }
                questionLevel.pageId = question.col4;
                topLevel.children.add(questionLevel);
            }
            
            addToSettings(topLevel);
        }
        
        return treeList;
    }

    private void addToSettings(QuestionTreeItem topLevel) {
        ApplicationInstance questionApplication = instancePool.getApplicationInstance("eed3197f-8e24-4cc1-a03e-95931f572653");
        String jsonList = questionApplication.getSetting("list");
        Gson gson = new Gson();
        ArrayList<QuestionTreeItem> items = gson.fromJson(jsonList, new TypeToken<ArrayList<QuestionTreeItem>>(){}.getType());
        
        items.get(0).children.add(topLevel);
        
        Setting setting = new Setting();
        setting.id = "list";
        setting.value = gson.toJson(items);
        setting.name = "list";
        setting.secure = false;
        
        Settings settings = new Settings();
        settings.settings = new ArrayList();
        settings.settings.add(setting);
        
        settings.appId = "eed3197f-8e24-4cc1-a03e-95931f572653";
        
        instancePool.setApplicationSettings(settings);

    }

}
