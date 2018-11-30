package com.thundashop.core.support;


import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.SupportDatabase;
import com.thundashop.core.storemanager.StoreManager;
import static com.thundashop.core.support.SupportCaseType.BUG;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ktonder
 */
@GetShopSession
@Component
public class SupportManager extends ManagerBase implements ISupportManager {

    @Autowired
    private SupportDatabase supportDatabase;
    
    @Autowired
    private StoreManager StoreManager;
    
    @Override
    public void helloWorld() {
        saveSupportCaseTest();
    }

    private void saveSupportCaseTest() throws ErrorException {
        SupportCase supportCase = new SupportCase();
        supportCase.type = BUG;
        supportDatabase.save(supportCase);
    }

    private List<SupportCase> getCases() {
        BasicDBObject query = new BasicDBObject();
        List<DataCommon> res = supportDatabase.query(query);
        List<SupportCase> cases = res.stream()
                .map(o -> (SupportCase)o)
                .collect(Collectors.toList());
        return cases;
    }

    @Override
    public SupportCase createSupportCase(SupportCase supportCase) {
        supportCase.state = SupportCaseState.CREATED;
        supportCase.byStoreId = storeId;
        return saveSupportCase(supportCase);
    }

    
    @Override
    public List<SupportCase> getSupportCases(SupportCaseFilter filter) {
        List<SupportCase> allCases = getCases();
        List<SupportCase> result = new ArrayList();
        for(SupportCase tmpCase : allCases) {
            if(!hasAccess(tmpCase.byStoreId)) {
                continue;
            }
            finalize(tmpCase);
            result.add(tmpCase);
        }
        return result;
    }

    @Override
    public void addToSupportCase(String supportCaseId, SupportCaseHistory history) {
        history.storeId = StoreManager.getStoreId();
        history.userId = getSession().currentUser.id;
        history.date = new Date();
        history.fullName = getSession().currentUser.fullName;
        SupportCase scase = getSupportCase(supportCaseId);
        scase.history.add(history);
        if(scase.byUserName.isEmpty()) {
            scase.byUserName = getSession().currentUser.fullName;
        }
        saveSupportCase(scase);
    }

    @Override
    public void changeSupportCaseType(String caseId, Integer typeId) {
        SupportCase toChange = getSupportCase(caseId);
        toChange.type = typeId;
        saveSupportCase(toChange);
    }

    @Override
    public void assignCareTakerForCase(String caseId, String userId) {
        SupportCase toChange = getSupportCase(caseId);
        toChange.handledByUser = userId;
        saveSupportCase(toChange);
    }

    @Override
    public void changeStateForCase(String caseId, Integer stateId) {
        SupportCase toChange = getSupportCase(caseId);
        toChange.state = stateId;
        saveSupportCase(toChange);
    }

    private boolean hasAccess(String storeIdToCheck) {
        if(storeId.equals("13442b34-31e5-424c-bb23-a396b7aeb8ca")) {
            return true;
        }
        boolean isEqual = storeIdToCheck.equals(storeId);
        return isEqual;
    }

    @Override
    public void changeModuleForCase(String caseId, Integer module) {
        SupportCase toChange = getSupportCase(caseId);
        toChange.module = module;
        saveSupportCase(toChange);
    }

    @Override
    public SupportCase getSupportCase(String supportCaseId) {
        List<SupportCase> allCases = getCases();
        for(SupportCase tmpCase : allCases) {
            if(!hasAccess(tmpCase.byStoreId)) {
                continue;
            }
            if(tmpCase.id.equals(supportCaseId)) {
                finalize(tmpCase);
                return tmpCase;
            }
        }
        return null;
    }

    private SupportCase saveSupportCase(SupportCase toChange) {
        supportDatabase.save(toChange);
        return toChange;
    }

    private void finalize(SupportCase tmpCase) {
        tmpCase.minutesSpent = 0;
        for(SupportCaseHistory hist : tmpCase.history) {
            hist.minutesUsed += hist.minutesUsed;
        }
    }
    
}
