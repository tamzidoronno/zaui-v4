/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.support;

import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ISupportManager {
    public void helloWorld();
    public List<SupportCase> getSupportCases(SupportCaseFilter filter);
    public SupportCase createSupportCase(SupportCase supportCase);
    public void addToSupportCase(String supportCaseId, SupportCaseHistory history);
    public void changeSupportCaseType(String caseId, Integer type);
    public void assignCareTakerForCase(String caseId, String userId);
    public void changeStateForCase(String caseId, Integer stateId);
    public void changeModuleForCase(String caseId, Integer moduleId);
    public SupportCase getSupportCase(String supportCaseId);
    public SupportStatistics getSupportStatistics();
}
