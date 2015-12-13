/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class GetShopSessionScope implements Scope {
    
    private Map<Long, String> threadStoreIds = Collections.synchronizedMap(new HashMap<Long, String>());
    private Map<Long, String> threadSessionBeanNames = Collections.synchronizedMap(new HashMap<Long, String>());
    private Map<Long, Session> threadSessions = Collections.synchronizedMap(new HashMap<Long, Session>());
    private Map<String, Object> objectMap = Collections.synchronizedMap(new HashMap<String, Object>());
    private Map<String, Object> namedSessionObjects = Collections.synchronizedMap(new HashMap<String, Object>());

    public Object get(String name, ObjectFactory<?> objectFactory) {
       
        long threadId = Thread.currentThread().getId();
        String storeId = threadStoreIds.get(threadId);
        String sessionBeanName = threadSessionBeanNames.get(threadId);
        
        if (storeId == null) {
            throw new NullPointerException("There is scoped bean created without being in a context of a store, object: " + name);
        }
        
        String nameWithStoreId = name + "_" + storeId;
        
        
        if (!objectMap.containsKey(nameWithStoreId)) {
            try {
                Object object = objectFactory.getObject();
                if (object instanceof GetShopSessionBeanNamed) {
                    if (namedSessionObjects.containsKey(name+"_"+storeId+"_"+sessionBeanName)) {
                        return namedSessionObjects.get(name+"_"+storeId+"_"+sessionBeanName);
                    }
                    
                    GetShopSessionBeanNamed bean = (GetShopSessionBeanNamed)object;
                    bean.setStoreId(storeId);
                    bean.setName(sessionBeanName);
                    bean.initialize();
                    
                    if (threadSessions.get(threadId) != null) {
                        bean.setSession(threadSessions.get(threadId));
                    }
                    
                    namedSessionObjects.put(name+"_"+storeId+"_"+sessionBeanName, bean);
                    return bean;
                }
                
                if (object instanceof ManagerBase) {
                    ManagerBase managerBase = (ManagerBase) object;
                    managerBase.setStoreId(storeId);
                    managerBase.initialize();
                }
                
                if (object instanceof GetShopSessionObject) {
                    GetShopSessionObject obj = (GetShopSessionObject) object;
                    obj.storeId = storeId;
                }
                
                objectMap.put(nameWithStoreId, object);
            } catch (BeansException exception) {
                System.out.println("Got an bean exception ? ");
                exception.printStackTrace();
            }
        }

        return objectMap.get(nameWithStoreId);

    }
    
    /**
     * There must be a very good reason for using this function.
     * @param name
     * @param storeId
     * @return 
     */
    public Object getManagerBasedOnNameAndStoreId(String name, String storeId) {
        return objectMap.get(name + "_" + storeId);
    }
    
    public List<GetShopSessionBeanNamed> getSessionNamedObjects() {
        return new ArrayList(namedSessionObjects.values());
    }

    public Object remove(String name) {
        return objectMap.remove(name);
    }

    public void registerDestructionCallback(String name, Runnable callback) {
        // do nothing
    }

    public Object resolveContextualObject(String key) {
        return null;
    }

    public String getConversationId() {
        return "getshop";
    }

    public void vaporize() {
        objectMap.clear();
    }

    public void setStoreId(String storeId, String multiLevelName, Session session) {
        long threadId = Thread.currentThread().getId();
        threadStoreIds.put(threadId, storeId);
        threadSessions.put(threadId, session);
        threadSessionBeanNames.put(threadId, multiLevelName);
    }

}
