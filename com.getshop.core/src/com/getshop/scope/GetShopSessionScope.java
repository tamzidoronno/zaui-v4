/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.thundashop.core.common.ManagerBase;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class GetShopSessionScope implements Scope, ApplicationContextAware {
    private Map<Long, String> threadStoreIds = Collections.synchronizedMap(new HashMap<Long, String>());
    private Map<String, Object> objectMap = Collections.synchronizedMap(new HashMap<String, Object>());
    private ApplicationContext ac;
    
    public Object get(String name, ObjectFactory<?> objectFactory) {
        String storeId = threadStoreIds.get(Thread.currentThread().getId());
        if (storeId == null) {
            throw new NullPointerException("There is scoped bean created without being in a context of a store, object: " + name);
        }
        String nameWithStoreId = name + "_" + storeId;
        if (!objectMap.containsKey(nameWithStoreId)) {
			try {
				Object object = objectFactory.getObject();
				if (object instanceof ManagerBase) {
					ManagerBase managerBase = (ManagerBase)object;
					managerBase.storeId = storeId;
					managerBase.initialize();
				}
				objectMap.put(nameWithStoreId, object);
			} catch (BeansException exception) {
				System.out.println("Got an bean exception ? ");
			}
        }
        
        return objectMap.get(nameWithStoreId);

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
    
    public void setStoreId(String storeId) {
        long threadId = Thread.currentThread().getId();
        threadStoreIds.put(threadId, storeId);
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.ac = ac;
    }
}
