/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Session;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author ktonder
 */
public abstract class UpdateScriptBase implements ApplicationContextAware {

    private static String sessionId = "";

    public UpdateScriptBase() {
        
        if (UpdateScriptBase.sessionId.equals("")) {
            System.out.println("asdfasdf");
            sessionId = UUID.randomUUID().toString();

        }
    }
    
    
    private ApplicationContext appContext;
    
    abstract public Date getAddedDate();
    
    @Autowired
    public StorePool storePool;
    
    @Autowired
    public Database database;
    
    protected Date getDate(String sdate) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM-yyyy");
        Date date = null;
        
        try {
            date = dateFormat.parse(sdate);
            String output = dateFormat.format(date); 
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return date;
    }
    
    public void runSingle() {
        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        UpdateScript script = (UpdateScript)context.getBean(this.getClass());
        script.run();
        System.exit(1);
    }
    
    /**
     * Returns a map with all stores and Data objects for a manager
     * 
     * @param managerName
     * @return 
     */
    public Map<Store, List<DataCommon>> getAllData(Class managerName) {
        List<Store> stores = storePool.getAllStores();
        String dbName = managerName.getSimpleName();
        
        Map<Store, List<DataCommon>> retData = new HashMap();
        
        for (Store store : stores) {
            Stream<DataCommon> dataStream = database.getAll(dbName, store.id);
            List<DataCommon> dataObjects = dataStream.collect(Collectors.toList());
            
            retData.put(store, dataObjects);
        }
        
        return retData;
    }
    
    
    public <V> V getManager(Class managerClass, Store store, String multilevelName) {
        Session session = getSession(store);
        GetShopSessionScope scope = appContext.getBean(GetShopSessionScope.class);
        scope.setStoreId(store.id, multilevelName, session);
        ManagerBase base = (ManagerBase)appContext.getBean(managerClass);
        base.setSession(session);
        return (V)base;
    }

    private Session getSession(Store store) {
        Session session = new Session();
        session.storeId = store.id;
        session.id = sessionId;
        return session;
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.appContext = ac;
    }
    
    private User getAdminUser() {
        User user = new User();
        user.type = 100;
        user.referenceKey = "TempUser";
        return user;
    }

    public void logOnStore(Store store) {
        UserManager userManger = getManager(UserManager.class, store, "");
        userManger.addTempUserForcedLogon(getAdminUser());
    }
    
        
}
