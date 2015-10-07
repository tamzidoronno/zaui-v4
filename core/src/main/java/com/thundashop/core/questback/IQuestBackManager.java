/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.questback.data.QuestBackQuestion;

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
    public String getPageId(String nodeId);
    
    @Customer
    public String getQuestionTitle(String pageId);
}
