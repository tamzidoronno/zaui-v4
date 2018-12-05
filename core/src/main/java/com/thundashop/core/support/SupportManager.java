package com.thundashop.core.support;


import com.getshop.scope.GetShopSession;
import com.ibm.icu.util.Calendar;
import com.mongodb.BasicDBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.SupportDatabase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshoplock.GetShopLogFetcherStarter;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
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
    private StoreManager storeManager;
    
    @Autowired
    private StorePool storePool;
    
    @Autowired
    private MessageManager messageManager;
    
    private SupportStatistics storeSupportStats = new SupportStatistics();
    private String getshopStoreId = "13442b34-31e5-424c-bb23-a396b7aeb8ca";
    private SupportStatistics globalStats;
    
    private SupportStore mySupportStores = new SupportStore();
    private List<SupportStore> allSupportStores = new ArrayList();
    private Date lastUpdatedAllStores;
    
    @Override
    public void helloWorld() {
        saveSupportCaseTest();
    }
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon dataCommon : data.data) {
            if(dataCommon instanceof SupportStatistics) {
                storeSupportStats = (SupportStatistics) dataCommon;
            }
            if(dataCommon instanceof SupportStore) {
                mySupportStores = (SupportStore)dataCommon;
            }
        }
    }
    
    @Override
    public void initialize() throws SecurityException {
        super.initialize(); //To change body of generated methods, choose Tools | Templates.
    }

    private void saveSupportCaseTest() throws ErrorException {
        SupportCase supportCase = new SupportCase();
        supportCase.type = BUG;
        supportDatabase.save(supportCase);
    }
    
    private SupportStatistics getGlobalSupportStatitics() {
        if(this.globalStats != null) {
            return globalStats;
        }
        
        BasicDBObject query = new BasicDBObject();
        List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
        obj.add(new BasicDBObject("className", "com.thundashop.core.support.SupportStatistics"));
        query.put("$and", obj);
        
        SupportStatistics stats = new SupportStatistics();
        List<DataCommon> res = supportDatabase.query(query);
        for(DataCommon r : res) {
            if(r instanceof SupportStatistics) {
                stats = (SupportStatistics) r;
            }
        }
        this.globalStats = stats;
        return stats;
    }
    
    private List<SupportStore> getSupportStores() {
        if(!this.allSupportStores.isEmpty() && !isAllSupportStoresExpired()) {
            return allSupportStores;
        }
        
        lastUpdatedAllStores = new Date();
        BasicDBObject query = new BasicDBObject();
        List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
        obj.add(new BasicDBObject("className", "com.thundashop.core.support.SupportStore"));
        query.put("$and", obj);
        
        SupportStatistics stats = new SupportStatistics();
        List<DataCommon> res = supportDatabase.query(query);
        for(DataCommon r : res) {
            if(r instanceof SupportStore) {
                SupportStore tmp = (SupportStore) r;
                allSupportStores.add(tmp);
            }
        }
        return allSupportStores;
    }
    
    private SupportStore getSupportStore(String storeId) {
        List<SupportStore> allStores = getSupportStores();
        for(SupportStore s : allStores) {
            if(s.supportStoreId.equals(storeId)) {
                return s;
            }
        }
        return null;
    }

    private List<SupportCase> getCases(SupportCaseFilter filter) {
        if(!isGetShop()) {
            filter.storeId = getStoreId();
        }
        notifySupportCenter();
        BasicDBObject query = new BasicDBObject();
        List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
        if(filter.state >= 0) {
            obj.add(new BasicDBObject("state", filter.state));
        }
        if(!filter.caseId.isEmpty()) {
            obj.add(new BasicDBObject("_id", filter.caseId));
        }
        if(!filter.userId.isEmpty()) {
            obj.add(new BasicDBObject("handledByUser", filter.userId));
        }
        if(!isBacklogProgressOrSolved(filter.state)) {
            if(!filter.storeId.isEmpty()) {
                obj.add(new BasicDBObject("byStoreId", filter.storeId));
            }
        }
        if(!obj.isEmpty()) {
            query.put("$and", obj);
        }
        
        List<DataCommon> res = supportDatabase.query(query);
        List<SupportCase> cases = new ArrayList();
        for(DataCommon r : res) {
            if(r instanceof SupportCase) {
                cases.add((SupportCase) r);
            }
        }
        return cases;
    }

    
    private List<FeatureList> getFeatureLists() {
        List<FeatureList> retval = new ArrayList();
        BasicDBObject query = new BasicDBObject();
        List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
        obj.add(new BasicDBObject("className", "com.thundashop.core.support.FeatureList"));
        
        List<DataCommon> res = supportDatabase.query(query);
        List<SupportCase> cases = new ArrayList();
        for(DataCommon r : res) {
            if(r instanceof FeatureList) {
                retval.add((FeatureList) r);
            }
        }
        return retval;
    }

    
    
    @Override
    public SupportCase createSupportCase(SupportCase supportCase) {
        supportCase.state = SupportCaseState.CREATED;
        supportCase.byStoreId = storeId;
        if(isGetShop()) {
            supportCase.state = SupportCaseState.MOVEDTOBACKLOG;
        }
        updateStatisticsCounter(supportCase);
        String msg = "Support case has been added to support center";
        messageManager.sendMail("support@getshop.com", "support@getshop.com", "Added to support center: " + supportCase.title, msg, "noreply@getshop.com", "noreply@getshop.com");
        return saveSupportCase(supportCase);
    }

    
    @Override
    public List<SupportCase> getSupportCases(SupportCaseFilter filter) {
        List<SupportCase> allCases = getCases(filter);
        List<SupportCase> result = new ArrayList();
        for(SupportCase tmpCase : allCases) {
            if(!hasAccess(tmpCase.byStoreId)) {
                if(isBacklogProgressOrSolved(filter.state) && isBacklogProgressOrSolved(tmpCase.state)) {
                    SupportCase tmpCaseFixed = new SupportCase();
                    tmpCaseFixed.rowCreatedDate = tmpCase.rowCreatedDate;
                    tmpCaseFixed.title = tmpCase.title;
                    tmpCaseFixed.type = tmpCase.type;
                    result.add(tmpCaseFixed);
                }
                continue;
            }
            finalize(tmpCase);
            result.add(tmpCase);
        }
        return result;
    }

    @Override
    public void addToSupportCase(String supportCaseId, SupportCaseHistory history) {
        if(history.minutesUsed <= 0) {
            history.minutesUsed = 1;
        }
        if(!isGetShop()) {
            history.minutesUsed = 0;
        }
        history.storeId = storeManager.getStoreId();
        history.userId = getSession().currentUser.id;
        history.date = new Date();
        history.fullName = getSession().currentUser.fullName;
        SupportCase scase = getSupportCase(supportCaseId);
        scase.history.add(history);
        if(getSession() != null && getSession().currentUser != null) {
            if(scase.byUserName.isEmpty()) {
                scase.byUserName = getSession().currentUser.fullName;
            }
            if(scase.usersEmail.isEmpty()) {
                scase.usersEmail = getSession().currentUser.emailAddress;
            }
        }
        if(!isGetShop()) {
            scase.state = SupportCaseState.SENT;
        }
        updateTimeCounter(history.minutesUsed);
        saveSupportCase(scase);
        
        SupportStore supportstore = getSupportStore(scase.byStoreId);
        if(isGetShop()) {
            if(supportstore != null && supportstore.mainEmailAdress != null && !supportstore.mainEmailAdress.isEmpty()) {
                String msg = "Your case has been replied to, log on to your support center to read it.";
                if(scase.usersEmail != null && scase.usersEmail.contains("@")) {
                    messageManager.sendMail(scase.usersEmail, scase.usersEmail, scase.title,msg,"noreply@getshop.com","noreply@getshop.com");
                } else {
                    messageManager.sendMail(supportstore.mainEmailAdress, supportstore.mainEmailAdress, scase.title,msg,"noreply@getshop.com","noreply@getshop.com");
                }
            }
        }
        
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
        if(storeId.equals(getshopStoreId)) {
            return true;
        }
        boolean isEqual = storeIdToCheck.equals(storeId);
        return isEqual;
    }

    private boolean isGetShop() {
        return storeId.equals(getshopStoreId);
    }
    
    @Override
    public void changeModuleForCase(String caseId, Integer module) {
        SupportCase toChange = getSupportCase(caseId);
        toChange.module = module;
        saveSupportCase(toChange);
    }

    @Override
    public SupportCase getSupportCase(String supportCaseId) {
        SupportCaseFilter filter = new SupportCaseFilter();
        filter.caseId = supportCaseId;
        List<SupportCase> allCases = getCases(filter);
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
            tmpCase.minutesSpent += hist.minutesUsed;
            SupportStore storeconfig = getSupportStore(tmpCase.byStoreId);
            if(storeconfig != null) {
                tmpCase.emailAdress = storeconfig.mainEmailAdress;
                tmpCase.webAddress = storeconfig.defaultWebAddress;
            } else {
                tmpCase.emailAdress = "unmapped store";
                tmpCase.webAddress = "unmapped store";
            }
        }
    }

    @Override
    public SupportStatistics getSupportStatistics() {
        SupportStatistics globalstats = getGlobalSupportStatitics();
        storeSupportStats.bugsTotal = globalstats.bugsTotal;
        storeSupportStats.featuresTotal = globalstats.featuresTotal;
        storeSupportStats.questionsTotal = globalstats.questionsTotal;
        storeSupportStats.timeSpentTotal = globalstats.timeSpentTotal;
        return storeSupportStats;
    }

    private void updateStatisticsCounter(SupportCase supportCase) {
        SupportStatistics globalStats = getGlobalSupportStatitics();
        if(supportCase.type.equals(SupportCaseType.BUG)) { globalStats.bugsTotal++; storeSupportStats.bugs++; }
        if(supportCase.type.equals(SupportCaseType.FEATURE)) { globalStats.featuresTotal++; storeSupportStats.features++;  }
        if(supportCase.type.equals(SupportCaseType.SUPPORT)) { globalStats.questionsTotal++; storeSupportStats.questions++;  }
        supportDatabase.save(globalStats);
        saveObject(storeSupportStats);
    }

    private void updateTimeCounter(Integer minutesUsed) {
        SupportStatistics globalStats = getGlobalSupportStatitics();
        globalStats.timeSpentTotal += minutesUsed;
        storeSupportStats.timeSpent += minutesUsed;
        supportDatabase.save(globalStats);
        saveObject(storeSupportStats);
    }

    @Override
    public void changeTitleOnCase(String caseId, String title) {
        if(!isGetShop()) {
            return;
        }
        SupportCase casetochange = getSupportCase(caseId);
        casetochange.title = title;
        saveSupportCase(casetochange);
    }

    private boolean isAllSupportStoresExpired() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);
        if(lastUpdatedAllStores == null) {
            return true;
        }
        Date toCheck = cal.getTime();
        return toCheck.after(lastUpdatedAllStores);
    }

    private void notifySupportCenter() {
        Store store = storeManager.getMyStore();
//        if(!mySupportStores.notifiedSupport) {
            mySupportStores.mainEmailAdress = store.configuration.emailAdress;
            mySupportStores.supportStoreId = store.id;
            mySupportStores.defaultWebAddress = store.getDefaultWebAddress();
            mySupportStores.notifiedSupport = true;
            supportDatabase.save(mySupportStores);
            saveObject(mySupportStores);
            lastUpdatedAllStores = null;
//        }
    }

    @Override
    public void saveFeatureThree(String moduleId, FeatureList list) {
        supportDatabase.save(list);
    }

    @Override
    public FeatureList getFeatureThree(String moduleId) {
        List<FeatureList> featurelists = getFeatureLists();
        for(FeatureList l : featurelists) {
            if(l.id.equals(moduleId)) {
                return l;
            }
        }
        FeatureList newlist = new FeatureList();
        newlist.id = moduleId;
        return newlist;
    }

    private boolean isBacklogProgressOrSolved(Integer state) {
        if(state.equals(SupportCaseState.MOVEDTOBACKLOG) || state.equals(SupportCaseState.SOLVEDBYDEV) || state.equals(SupportCaseState.INPROGRESS)) {
            return true;
        }
        return false;
    }

    
    
}
