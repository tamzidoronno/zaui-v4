/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.thundashop.core.common.*;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
@Component
public class GetShopSessionScope implements Scope {
    private static final Logger log = LoggerFactory.getLogger(GetShopSessionScope.class);
    private StorePool storePool = null;
    private final Map<Long, String> threadStoreIds = new ConcurrentHashMap<>();
    private final Map<Long, String> threadSessionBeanNames = new ConcurrentHashMap<>();
    private final Map<Long, String> originalSessionBeanName = new ConcurrentHashMap<>();
    private final Map<Long, Session> threadSessions = new ConcurrentHashMap<>();
    private final Map<String, Object> objectMap = new ConcurrentHashMap<>();
    private final Map<String, Object> namedSessionObjects = new ConcurrentHashMap<>();

    public <T> T getNamedSessionBean(String multiLevelName, Class className) {
        if (multiLevelName == null || multiLevelName.isEmpty()) {
            throw new RuntimeException("Come on!! name your multilevel beans properly!!!");
        }
        
        long threadId = Thread.currentThread().getId();
        
        String oldMultiLevelName = threadSessionBeanNames.get(threadId);
        
        if (oldMultiLevelName != null && !oldMultiLevelName.isEmpty() && !oldMultiLevelName.equals(multiLevelName) && originalSessionBeanName.get(threadId) != null) {
            throw new GetShopBeanException("Its not possible to get multilevel names beans when you are already executing from one");
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
        
        if (AppContext.appContext != null && storePool == null) {
            storePool = AppContext.appContext.getBean(StorePool.class);
        }
        
        if (storePool != null) {
            if (name != null && name.equals("scopedTarget.userManager")) {
                Store slaveStore = storePool.getStore(storeId);
                if(slaveStore!= null) {
                    Store masterStore = storePool.getStore(slaveStore.masterStoreId);
                    if (slaveStore.masterStoreId != null && !slaveStore.masterStoreId.isEmpty() && masterStore.acceptedSlaveIds.contains(storeId)) {
                        storeId = slaveStore.masterStoreId;
                    }
                }
            }    
        }
        
        String nameWithStoreId = name + "_" + storeId;
        
        if (namedSessionObjects.containsKey(name+"_"+storeId+"_"+sessionBeanName)) {
            return namedSessionObjects.get(name+"_"+storeId+"_"+sessionBeanName);
        }

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

                    if (threadSessions.get(threadId) != null) {
                        managerBase.setSession(threadSessions.get(threadId));
                    }
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
                log.error("Got an bean exception ? storeId `{}`", storeId, exception);
            }
        }

        return objectMap.get(nameWithStoreId);
    }
    
    public List<GetShopSessionBeanNamed> getSessionNamedObjects() {
        long threadId = Thread.currentThread().getId();
        String storeId = threadStoreIds.get(threadId);
        return namedSessionObjects.values()
                .stream()
                .map(i -> (GetShopSessionBeanNamed) i)
                .filter(o -> o.getStoreId().equals(storeId))
                .collect(Collectors.toList());
    }

    public Session getSessionFromThreadSessions(long threadId){
        return threadSessions.get(threadId);
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
        
        if (storeId != null) {
            threadStoreIds.put(threadId, storeId);
        }
        
        if (session != null) {
            threadSessions.put(threadId, session);
        }
        
        if (multiLevelName != null) {
            threadSessionBeanNames.put(threadId, multiLevelName);
        }
        
        if (multiLevelName == null) {
            originalSessionBeanName.remove(threadId);
        } else {
            originalSessionBeanName.put(threadId, multiLevelName);
        }
    }
    public void removethreadStoreId(String storeId){
        long threadId = Thread.currentThread().getId();
        threadStoreIds.remove(threadId, storeId);
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

    public void cleanupThreadSessions() {
        log.debug("Thread sessions before removal : `{}`", threadSessions.size());
        List<Long> toRemove = new ArrayList<>();

        for (Long threadId : threadSessions.keySet()) {
            Session session = threadSessions.get(threadId);
            if (session.hasExpired()) {
                toRemove.add(threadId);
            }
        }

        toRemove.forEach(threadSessions::remove);

        log.debug("Thread sessions after removal : `{}`", threadSessions.size());
    }

    public void shareThreadContext(long threadId1, long threadId2){
        this.threadStoreIds.put(threadId2, this.threadStoreIds.get(threadId1));
        this.threadSessionBeanNames.put(threadId2, this.threadSessionBeanNames.get(threadId1));
        this.threadSessions.put(threadId2, this.threadSessions.get(threadId1));
        this.originalSessionBeanName.put(threadId2, this.originalSessionBeanName.get(threadId1));
    }
}