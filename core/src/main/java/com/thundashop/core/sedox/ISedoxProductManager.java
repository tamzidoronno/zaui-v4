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
import com.thundashop.core.usermanager.data.User;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ISedoxProductManager  {
    public SedoxProductSearchPage search(SedoxSearch search);
    
    @Administrator
    public void sync(String option) throws ErrorException;
    
    @Customer
    public SedoxUser getSedoxUserAccount() throws ErrorException;
    
    @Administrator
    public SedoxUser getSedoxUserAccountById(String userid) throws ErrorException;
    
    @Customer
    public List<SedoxProduct> getProductsFirstUploadedByCurrentUser(FilterData filterData);
    
    @Customer
    public int getProductsFirstUploadedByCurrentUserTotalPages(FilterData filterData);
    
    @Administrator
    public List<SedoxUser> getAllUsersWithNegativeCreditLimit() throws ErrorException;
    
    /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     * 
     * @param day
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<SedoxProduct> getProductsByDaysBack(int day) throws ErrorException;
    
    public SedoxProduct getProductById(String id) throws ErrorException;
    
    public SedoxSharedProduct getSharedProductById(String id) throws ErrorException;
    
    @Customer
    public SedoxProduct createSedoxProduct(SedoxSharedProduct sedoxProduct, String base64encodedOriginalFile, String originalFileName, String forSlaveId, String origin, String comment, String useCredit) throws ErrorException;
    
    @Customer
    public SedoxSharedProduct getSedoxProductByMd5Sum(String md5sum) throws ErrorException;
    
    @Customer
    public void requestSpecialFile(String productId, String comment) throws ErrorException;
    
    @Administrator
    public void addFileToProduct(String base64EncodedFile, String fileName, String fileType, String productId) throws ErrorException;
    
    @Customer
    public String purchaseProduct(String productId, List<Integer> files) throws ErrorException;
    
    @Administrator
    public SedoxOrder purchaseOnlyForCustomer(String productId, List<Integer> files) throws ErrorException;
    
    @Administrator
    public void notifyForCustomer(String productId, String extraText) throws ErrorException;
    
    @Administrator
    public void sendProductByMail(String productId, String extraText, List<Integer> files) throws ErrorException;
    
    @Administrator
    public List<User> searchForUsers(String searchString) throws ErrorException;
    
    @Administrator
    public void addUserCredit(String id, String description, int amount) throws ErrorException;
    
    public User login(String emailAddress, String password) throws ErrorException;
    
    @Administrator
    public void setChecksum(String productId, String checksum) throws ErrorException;
    
    /**
     * This will disable/enable the developer. Useful if a developer goes on vacation
     * or needs an hour sleep.
     */
    @Administrator
    public void changeDeveloperStatus(String userId, boolean disabled) throws ErrorException;
    
    /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     * 
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<SedoxUser> getDevelopers() throws ErrorException;
    
    @Administrator
    public void removeBinaryFileFromProduct(String productId, int fileId) throws ErrorException;
    
    @Administrator
    public void toggleAllowNegativeCredit(String userId, boolean allow) throws ErrorException;

    @Administrator
    public void toggleAllowWindowsApp(String userId, boolean allow) throws ErrorException;
    
    @Administrator
    public void addSlaveToUser(String masterUserId, String slaveUserId) throws ErrorException;
    
    @Administrator
    public void addCreditToSlave(String slaveId, double amount) throws ErrorException;
    
    public List<SedoxUser> getSlaves(String masterUserId);
    
    @Administrator
    public void togglePassiveSlaveMode(String userId, boolean toggle) throws ErrorException;
    
    @Administrator
    public void toggleStartStop(String productId, boolean toggle) throws ErrorException;
    
    @Administrator
    public String getExtraInformationForFile(String productId, int fileId) throws ErrorException;
    
    @Administrator
    public void setExtraInformationForFile(String productId, int fileId, String text) throws ErrorException;
 
    @Administrator
    public void toggleSaleableProduct(String productId, boolean saleable) throws ErrorException;
    
    @Administrator
    public void toggleIsNorwegian(String userId, boolean isNorwegian) throws ErrorException;
	
    @Administrator
    public void toggleBadCustomer(String userId, boolean badCustomer) throws ErrorException;
	
    public List<SedoxProduct> getLatestProductsList(int count) throws ErrorException;

    @Customer
    public void addReference(String productId, String reference) throws ErrorException;

    @Customer
    public void transferCreditToSlave(String slaveId, double amount) throws ErrorException;
    
    @Administrator
    public List<SedoxProductStatistic> getStatistic();
    
    @Administrator
    public void setFixedPrice(String userId, String price) throws ErrorException;
    
    public List<String> getProductIds() throws ErrorException;
    
    @Administrator
    public void syncFromMagento(String userId) throws ErrorException;
    
    public Double getPriceForProduct(String productId, List<Integer> files) throws ErrorException;
    
    @Editor
    public int getFileNotProcessedToDayCount() throws ErrorException;
    
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
}