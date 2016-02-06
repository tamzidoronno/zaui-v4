/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.bookingengine;

import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.common.Session;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.Before;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author ktonder
 */
public abstract class TestCommon implements ApplicationContextAware {
    
    @Mock
    DatabaseSaver databaseSaver;
    
    @Mock
    PageManager pageManager;
    
    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        GetShopSessionScope scope = ac.getBean(GetShopSessionScope.class);
        scope.setStoreId("junit_test_store", "level1", new Session());
    }
    
    @Before
    public void setupFirst() {
        MockitoAnnotations.initMocks(this);
        Mockito.doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Object ret = invocation.getMock();
                
                DataCommon data = (DataCommon) args[0];
                if (data.id == null || data.id.isEmpty()) {
                    data.id = UUID.randomUUID().toString();
                }
                
                return invocation;
            }}).when(databaseSaver).saveObject(any(DataCommon.class), any(Credentials.class));
        
        Page page = new Page();
        page.id = "new_page_id";
        when(pageManager.createPageFromTemplatePage(any(String.class))).thenReturn(page);
    }
    
    public void feedDataFromDatabase(ManagerSubBase bookingEngine, DataCommon dataObject) {
        DataRetreived dataRetreived = new DataRetreived();
        dataRetreived.data = new ArrayList();
        dataRetreived.data.add(dataObject);
        
        bookingEngine.dataFromDatabase(dataRetreived);
    }

    void setUserLoggedIn(ManagerSubBase bookingEngine) {
        Session session = new Session();
        
        session.currentUser = new User();
        session.currentUser.fullName = "Test User";
        session.currentUser.id = "TestUserId";
        
        bookingEngine.setSession(session);
    }
}
