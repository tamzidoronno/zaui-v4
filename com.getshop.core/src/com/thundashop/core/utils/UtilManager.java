/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.utils;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.utilmanager.data.FileObject;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.data.Company;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class UtilManager extends ManagerBase implements IUtilManager {

    public HashMap<String, FileObject> files = new HashMap();

    @Autowired
    private CompanySearchEngineHolder searchEngineHolder;

    @Autowired
    public UtilManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon tmpData : data.data) {
            if (tmpData instanceof FileObject) {
                files.put(tmpData.id, (FileObject) tmpData);
            }
        }
    }

    @Override
    public Company getCompanyFromBrReg(String companyVatNumber) throws ErrorException {
        return searchEngineHolder.getSearchEngine(storeId).getCompany(companyVatNumber, true);
    }

    @Override
    public List<Company> getCompaniesFromBrReg(String search) throws ErrorException {
        return searchEngineHolder.getSearchEngine(storeId).search(search);
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

    @Override
    public Company getCompanyFree(String companyVatNumber) throws ErrorException {
        return searchEngineHolder.getSearchEngine(storeId).getCompany(companyVatNumber, false);
    }
}
