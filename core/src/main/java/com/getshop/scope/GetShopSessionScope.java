/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.common.Session;
import com.thundashop.core.common.StoreComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    public <T> T getNamedSessionBean(String multiLevelName, Class className) {
        if (multiLevelName == null || multiLevelName.isEmpty()) {
            throw new RuntimeException("Come on!! name your multilevel beans properly!!!");
        }
        
        long threadId = Thread.currentThread().getId();
        
        String oldMultiLevelName = threadSessionBeanNames.get(threadId);
        
        if (oldMultiLevelName != null && !oldMultiLevelName.isEmpty() && !oldMultiLevelName.equals(multiLevelName)) {
            throw new RuntimeException("Its not allowed to use multiple multilevel names within one request. If you have a solution for this, please provide it :D");
        }
        
        threadSessionBeanNames.put(threadId, multiLevelName);
        String storeId = threadStoreIds.get(threadId);
        String name = className.getSimpleName();
        name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        String key = "scopedTarget." + name+"_"+storeId+"_"+multiLevelName;
        return (T)namedSessionObjects.get(key);
    }
    
    public Object get(String name, ObjectFactory<?> objectFactory) {
       
        long threadId = Thread.currentThread().getId();
        String storeId = threadStoreIds.get(threadId);
        String sessionBeanName = threadSessionBeanNames.get(threadId);
        
        if (storeId == null && name != null && name.equals("scopedTarget.database")) {
            storeId = "all";
        }
        
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
                
                if (object instanceof StoreComponent) {
                    ((StoreComponent)object).setStoreId(storeId);
                }
                
                if (object instanceof GetShopSessionObject) {
                    GetShopSessionObject obj = (GetShopSessionObject) object;
                    obj.storeId = storeId;
                }
                
                objectMap.put(nameWithStoreId, object);
            } catch (BeansException exception) {
                GetShopLogHandler.logPrintStatic("Got an bean exception ? ", storeId);
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
        long threadId = Thread.currentThread().getId();
        String storeId = threadStoreIds.get(threadId);
        return new ArrayList(
                namedSessionObjects.values()
                        .stream()
                        .filter(o -> ((GetShopSessionBeanNamed)o).getStoreId().equals(storeId))
                        .collect(Collectors.toList())
        );
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

    public void clearStore(String storeId) {
        List<String> keysToRemove = namedSessionObjects.keySet().stream().filter(o -> o.contains(storeId)).collect(Collectors.toList());
        for (String key : keysToRemove) {
            namedSessionObjects.remove(key);
        }
        
        List<String> keysToRemove2 = objectMap.keySet().stream().filter(o -> o.contains(storeId)).collect(Collectors.toList());
        for (String key : keysToRemove2) {
            objectMap.remove(key);
        }
    }

}
