/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ISedoxProductManager  {
    public SedoxProductSearchPage search(String searchString);
    
    @Administrator
    public void sync() throws ErrorException;
    
    @Customer
    public SedoxUser getSedoxUserAccount() throws ErrorException;
    
    @Customer
    public List<SedoxProduct> getProductsFirstUploadedByCurrentUser() throws ErrorException;
    
    @Administrator
    public List<SedoxUser> getAllUsersWithNegativeCreditLimit() throws ErrorException;
    
    public List<SedoxUser> getFileDevelopers() throws ErrorException;
    
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
    public void createSedoxProduct(SedoxProduct sedoxProduct, String base64encodedOriginalFile, String originalFileName) throws ErrorException;
    
    @Customer
    public SedoxProduct getSedoxProductByMd5Sum(String md5sum) throws ErrorException;
    
    @Customer
    public void requestSpecialFile(String productId, String comment) throws ErrorException;
}
