/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.listmanager.data.TreeNode;
import com.thundashop.core.usermanager.data.User;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ISedoxProductManager  {
    public SedoxProductSearchPage search(SedoxSearch search);
    
    @Editor
    public SedoxProductSearchPage searchUserFiles(SedoxSearch search);
    
    @Editor
    public void sync(String option) throws ErrorException;
    
    @Customer
    public SedoxUser getSedoxUserAccount() throws ErrorException;
    
    @Customer
    public SedoxUser getSedoxUserAccountById(String userid) throws ErrorException;
    
    @Customer
    public List<SedoxProduct> getProductsFirstUploadedByCurrentUser(FilterData filterData);
    
    @Customer
    public int getProductsFirstUploadedByCurrentUserTotalPages(FilterData filterData);
    
    @Editor
    public List<SedoxUser> getAllUsersWithNegativeCreditLimit() throws ErrorException;
    
    @Editor
    public List<TreeNode> getAllUsersAsTreeNodes() throws ErrorException;
    
    /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     * 
     * @param day
     * @return
     * @throws ErrorException 
     */
    @Editor
    public List<SedoxProduct> getProductsByDaysBack(int day) throws ErrorException;
    
    public SedoxProduct getProductById(String id) throws ErrorException;
    
    public SedoxSharedProduct getSharedProductById(String id) throws ErrorException;
    
    @Customer
    public void createSedoxProduct(SedoxSharedProduct sedoxProduct, String base64encodedOriginalFile, String originalFileName, String forSlaveId, String origin, String comment, String useCredit, SedoxBinaryFileOptions options, String reference);
    
    @Administrator
    public void finishUpload(String forSlaveId, SedoxSharedProduct sharedProduct, String useCredit, String comment, SedoxBinaryFile originalFile, SedoxBinaryFile cmdEncryptedFile, SedoxBinaryFileOptions options, String base64EncodeString, String originalFileName, String origin, String fromUserId, String referenceId);
    
    @Administrator
    public void addFileToProductAsync(SedoxBinaryFile sedoxBinaryFile, String fileType, String fileName, String productId) ;
    
    @Administrator
    public int getNextFileId();
    
    @Customer
    public SedoxSharedProduct getSedoxProductByMd5Sum(String md5sum) throws ErrorException;
    
    @Customer
    public void requestSpecialFile(String productId, String comment) throws ErrorException;
    
    @Editor
    public void addFileToProduct(String base64EncodedFile, String fileName, String fileType, String productId, SedoxBinaryFileOptions options) throws ErrorException;
    
    @Customer
    public String purchaseProduct(String productId, List<Integer> files) throws ErrorException;
    
    @Editor
    public SedoxOrder purchaseOnlyForCustomer(String productId, List<Integer> files) throws ErrorException;
    
    @Editor
    public void notifyForCustomer(String productId, String extraText) throws ErrorException;
    
    @Editor
    public void sendProductByMail(String productId, String extraText, List<Integer> files) throws ErrorException;
    
    @Editor
    public List<User> searchForUsers(String searchString) throws ErrorException;
    
    @Editor
    public void addUserCredit(String id, String description, int amount) throws ErrorException;
    
    public User login(String emailAddress, String password) ;
    
    @Editor
    public void setChecksum(String productId, String checksum) throws ErrorException;
    
    /**
     * This will disable/enable the developer. Useful if a developer goes on vacation
     * or needs an hour sleep.
     */
    @Editor
    public void changeDeveloperStatus(String userId, boolean disabled) throws ErrorException;
    
    /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     * 
     * @return
     * @throws ErrorException 
     */
    @Editor
    public List<SedoxUser> getDevelopers() throws ErrorException;
    
    @Editor
    public void removeBinaryFileFromProduct(String productId, int fileId) throws ErrorException;
    
    @Editor
    public void toggleAllowNegativeCredit(String userId, boolean allow) throws ErrorException;

    @Editor
    public void toggleAllowWindowsApp(String userId, boolean allow) throws ErrorException;
    
    @Editor
    public void addSlaveToUser(String masterUserId, String slaveUserId) throws ErrorException;
    
    @Editor
    public void addCreditToSlave(String slaveId, double amount) throws ErrorException;
    
    @Editor
    public void addCommentToUser(String userId, String comment) throws ErrorException;
    
    public List<SedoxUser> getSlaves(String masterUserId);
    
    @Editor
    public List<User> getAllUsers() throws ErrorException;
    
    @Editor
    public void togglePassiveSlaveMode(String userId, boolean toggle) throws ErrorException;
    
    @Editor
    public void toggleStartStop(String productId, boolean toggle) throws ErrorException;
    
    @Editor
    public String getExtraInformationForFile(String productId, int fileId) throws ErrorException;
    
    @Editor
    public void setExtraInformationForFile(String productId, int fileId, String text) throws ErrorException;
    
    @Editor
    public void setSpecialRequestsForFile(String productId, int fileId, boolean dpf, boolean egr, boolean decat, boolean vmax, boolean adblue, boolean dtc) throws ErrorException;
 
    @Editor
    public void toggleSaleableProduct(String productId, boolean saleable) throws ErrorException;
    
    @Editor
    public void toggleIsNorwegian(String userId, boolean isNorwegian) throws ErrorException;
	
    @Editor
    public void toggleBadCustomer(String userId, boolean badCustomer) throws ErrorException;
	
    public List<SedoxProduct> getLatestProductsList(int count) throws ErrorException;

    @Customer
    public void addReference(String productId, String reference) throws ErrorException;

    @Customer
    public void transferCreditToSlave(String slaveId, double amount) throws ErrorException;
    
    @Editor
    public List<SedoxProductStatistic> getStatistic();
    
    @Editor
    public void setFixedPrice(String userId, String price) throws ErrorException;
    
    public List<String> getProductIds() throws ErrorException;
    
    @Editor
    public void syncFromMagento(String userId) throws ErrorException;
    
    public Double getPriceForProduct(String productId, List<Integer> files) throws ErrorException;
    
    @Editor
    public int getFileNotProcessedToDayCount(int daysBack) throws ErrorException;
    
    @Editor
    public void markAsFinished(String productId, boolean finished) throws ErrorException;
    
    public void updateEvcCreditAccounts() throws ErrorException;
    
    @Customer
    public List<SedoxOrder> getOrders(FilterData filterData);
    
    @Customer
    public int getOrdersPageCount(FilterData filterData);
    
    @Customer
    public List<SedoxCreditHistory> getCurrentUserCreditHistory(FilterData filterData);
    
    @Customer
    public int getCurrentUserCreditHistoryCount(FilterData filterData);
    
    @Customer
    public Long getUserFileUploadCount();
    
    @Customer
    public Long getUserFileDownloadCount();
    
    @Customer
    public List<SedoxFileHistory> getUploadHistory();
    
    @Customer
    public void setPushoverId(String pushover);
    
    @Editor
    public void setPushoverIdForUser(String pushover, String userId);
    
    @Customer
    public void refreshEvcCredit();
    
    @Editor
    public void removeSlaveFromMaster(String slaveId);
    
    @Editor
    public void setType(String productId, String type);
    
    @Administrator
    public void clearManager();
    
    @Administrator
    public void invokeCreditUpdate();
    
    @Administrator
    public void sendProductToDifferentEmail(String productId, String emailAddress, List<Integer> files);
}