/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.utils;

import com.thundashop.core.utilmanager.data.FileObject;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.usermanager.data.Company;
import java.util.HashMap;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IUtilManager {
    public Company getCompanyFromBrReg(String companyVatNumber) throws ErrorException;
    
    public HashMap<String, String> getCompaniesFromBrReg(String search) throws ErrorException;
    
    public String saveFile(FileObject file) throws ErrorException;
    
    public FileObject getFile(String id) throws ErrorException;
    
    public String getBase64EncodedPDFWebPage(String urlToPage);
}
