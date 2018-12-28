/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.support;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ISupportManager {
    @Administrator
    public void helloWorld();
    @Administrator
    public List<SupportCase> getSupportCases(SupportCaseFilter filter);
    @Administrator
    public SupportCase createSupportCase(SupportCase supportCase);
    @Administrator
    public void addToSupportCase(String supportCaseId, SupportCaseHistory history);
    @Administrator
    public void changeSupportCaseType(String caseId, Integer type);
    @Administrator
    public void assignCareTakerForCase(String caseId, String userId);
    @Administrator
    public void changeStateForCase(String caseId, Integer stateId);
    @Administrator
    public void changeModuleForCase(String caseId, Integer moduleId);
    @Administrator
    public SupportCase getSupportCase(String supportCaseId);
    @Administrator
    public SupportStatistics getSupportStatistics();
    @Administrator
    public void changeTitleOnCase(String caseId, String title);
    @Administrator
    public void saveFeatureThree(String moduleId, FeatureList list);
    @Administrator
    public FeatureList getFeatureThree(String moduleId);
    @Administrator
    public FeatureListEntry getFeatureListEntry(String entryId);
    @Administrator
    public void updateFeatureListEntry(String entryId, FeatureListEntryText text, String title, String language);
    @Administrator
    public ServerStatusList getServerStatusList();
}
