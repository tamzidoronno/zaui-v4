/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class SetCartPageToSystemPage {
    
    @Autowired
    public Database database;
    
    public void update() throws ErrorException {
        Credentials credentials = new Credentials(StoreManager.class);
        credentials.manangerName = "StoreManager";
        credentials.storeid = "all";
        credentials.password = "ADSFASDF";
        for (DataCommon data : database.retreiveData(credentials)) {
            if (data instanceof Store) {
                Store store = (Store)data;
                
                Credentials cred = new Credentials(PageManager.class);
                cred.storeid = store.id;
                cred.manangerName = PageManager.class.getSimpleName();
                cred.password = "ASDFASDF2";
                
                List<DataCommon> dataCommons = database.retreiveData(cred);
                for (DataCommon pagecommon : dataCommons) {
                    if (pagecommon instanceof Page) {
                        Page page = (Page)pagecommon;
                        if (page.id.equals("cart")) {
                            System.out.println(page.id);
                            page.type = Page.PageType.HeaderMiddleFooter;
                            database.save(page, cred);
                        }
                    }
                }
            }
        }
    }
    
    public static void main(String args[]) throws ErrorException {
        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        AppContext.appContext = context;
        
        SetCartPageToSystemPage update = context.getBean(SetCartPageToSystemPage.class);
        update.update();
    }
}
