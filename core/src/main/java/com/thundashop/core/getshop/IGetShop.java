package com.thundashop.core.getshop;

import com.getshop.scope.CronThreadStartLog;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopNotSynchronized;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.getshop.data.CreatedStoreData;
import com.thundashop.core.getshop.data.DibsAutoCollectData;
import com.thundashop.core.getshop.data.GetshopStore;
import com.thundashop.core.getshop.data.Lead;
import com.thundashop.core.getshop.data.LeadHistory;
import com.thundashop.core.getshop.data.PartnerData;
import com.thundashop.core.getshop.data.SmsResponse;
import com.thundashop.core.getshop.data.StartData;
import com.thundashop.core.getshop.data.StoreCreatedData;
import com.thundashop.core.getshop.data.WebPageData;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.data.User;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IGetShop {
    
    /**
     * 
     * @param code
     * @return 
     */
    public List<GetshopStore> getStores(String code);
    
    @Administrator
    public List<DibsAutoCollectData> getOrdersToAutoPayFromDibs();
    
    public void addToDibsAutoCollect(String orderId, String storeId);
    
    /**Current leads
     * 
     * @param ids
     * @throws ErrorException 
     */
    public void setApplicationList(List<String> ids, String partnerId, String password) throws ErrorException;
    
    /**
     * Get partner data for this user.
     * @return
     * @throws ErrorException 
     */
    public PartnerData getPartnerData(String partnerId, String password) throws ErrorException;

    @Administrator
    public Lead createLead(String name);

    @Administrator
    public void markLeadHistoryCompleted(String leadHistoryId);

    @Administrator
    public void saveLead(Lead lead);
    
    @Administrator
    public List<Lead> getLeads();
    
    @Administrator
    public Lead getLead(String leadId);
    
    @Administrator
    public void addLeadHistory(String leadId, String comment, Date start, Date end, String userId);
    
    
    @Administrator
    public void changeLeadState(String leadId, Integer state);
    
    /**
     * 
     * @param userId
     * @param partner
     * @param password
     * @throws ErrorException 
     */
    public void addUserToPartner(String userId, String partner, String password) throws ErrorException;
    
        
    /**
     * Need to figure out what address is connected to a specific uuid?
     * Remember this is query is quite slow. so cache the result.
     * @param uuid
     * @return
     * @throws ErrorException 
     */
    public String findAddressForUUID(String uuid) throws ErrorException;

    /**
     * Find the store address for a given application.
     * @param uuid The appid.
     * @return
     * @throws ErrorException 
     */
    public String findAddressForApplication(String uuid) throws ErrorException;
    
    
    /**
     * Create a new webpage
     * @return 
     */
    public Store createWebPage(WebPageData webpageData) throws ErrorException;
    
    public void saveSmsCallback(SmsResponse smsResponses);
    
    public String startStoreFromStore(StartData startData);
   
    public void insertNewStore(String password, String newAddress, List<StoreData> storeDatas, String newStoreId, StartData startData);
    
    @GetShopNotSynchronized
    public String getBase64EncodedPDFWebPage(String urlToPage);
    
    @GetShopNotSynchronized
    public String getBase64EncodedPDFWebPageFromHtml(String html);
    
    @GetShopNotSynchronized
    public void triggerPullRequest(String storeId);
    
    @GetShopNotSynchronized
    public CreatedStoreData createNewStore(StartData startData);
    
    @Administrator
    public void toggleRemoteEditing();
    
    @Administrator
    @GetShopNotSynchronized
    public void loadEhfCompanies();
    
    @Administrator
    public boolean canInvoiceOverEhf(String vatNumber);
    
    public void startRecoveryForUnit(String id, String ip, String password);
    
    public void setRecoveryStatusForUnit(String id, String status);
    
    public String getRecoveryStatusForUnit(String id);
    
    public String getIpForUnitId(String id);
    
    public String canStartRestoringUnit(String id);
    
    @Administrator
    public HashMap<Long, String> getUnitsAskedForUpdate();
    
    @Administrator
    public void recoveryCompleted(String id);
    
    @Administrator
    public List<String> unitsTryingToRecover();
}
