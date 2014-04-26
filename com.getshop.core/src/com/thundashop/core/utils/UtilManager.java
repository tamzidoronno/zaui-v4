/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.utils;

import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.usermanager.data.Company;
import java.util.HashMap;
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

    @Autowired
    public BrRegEngine brRegEngine;
    
    @Autowired
    public UtilManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @Override
    public Company getCompanyFromBrReg(String companyVatNumber) throws ErrorException {
        return brRegEngine.getCompany(companyVatNumber);
    }

    @Override
    public HashMap<String, String> getCompaniesFromBrReg(String search) throws ErrorException {
        return brRegEngine.search(search);
    }
}
