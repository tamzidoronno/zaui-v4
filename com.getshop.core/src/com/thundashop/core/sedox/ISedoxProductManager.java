/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
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
    public List<SedoxProduct> getProductsFirstUploadedByCurrentUser() throws ErrorException;
    
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
    
    @Customer
    public void createSedoxProduct(SedoxProduct sedoxProduct, String base64encodedOriginalFile, String originalFileName, String forSlaveId, String origin) throws ErrorException;
    
    @Customer
    public SedoxProduct getSedoxProductByMd5Sum(String md5sum) throws ErrorException;
    
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
    
}