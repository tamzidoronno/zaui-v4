/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.utils;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.utilmanager.data.FileObject;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.data.Company;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class UtilManager extends ManagerBase implements IUtilManager {

    public HashMap<String, FileObject> files = new HashMap();
    
    @Autowired
    public BrRegEngine brRegEngine;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon tmpData : data.data) {
            if(tmpData instanceof FileObject) {
                files.put(tmpData.id, (FileObject)tmpData);
            }
        }
    }
    
    @Override
    public Company getCompanyFromBrReg(String companyVatNumber) throws ErrorException {
        return brRegEngine.getCompany(companyVatNumber);
    }

    @Override
    public HashMap<String, String> getCompaniesFromBrReg(String search) throws ErrorException {
        return brRegEngine.search(search);
    }

    @Override
    public String saveFile(FileObject file) throws ErrorException {
        file.storeId = storeId;
        databaseSaver.saveObject(file, credentials);
        files.put(file.id, file);
        return file.id;
    }

    @Override
    public FileObject getFile(String id) throws ErrorException {
        return files.get(id);
    }
}
