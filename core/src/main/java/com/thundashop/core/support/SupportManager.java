package com.thundashop.core.support;


import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.SupportDatabase;
import static com.thundashop.core.support.SupportCaseType.BUG;
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
    
    @Override
    public void helloWorld() {
        saveSupportCaseTest();
        showSavedCases();
    }

    private void saveSupportCaseTest() throws ErrorException {
        SupportCase supportCase = new SupportCase();
        supportCase.type = BUG;
        supportDatabase.save(supportCase);
    }

    private void showSavedCases() {
        BasicDBObject query = new BasicDBObject();
        
        supportDatabase.query(query).stream()
                .map(o -> (SupportCase)o)
                .forEach(o -> System.out.println("Created: " + o.rowCreatedDate + ", type: " + o.type));
    }
    
}
