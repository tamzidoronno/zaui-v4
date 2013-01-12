/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.socket.JsonObject2;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import java.util.HashMap;

/**
 *
 * @author ktonder
 */
public class StorePool {
    private HashMap<String, StoreHandler> storeHandlers = new HashMap();
    private StoreManager storeManager;

    public StorePool() {
        if(AppContext.appContext != null) {
            this.storeManager = AppContext.appContext.getBean(StoreManager.class);
        }
    }
    
    public Object ExecuteMethod(JsonObject2 object, Class[] types, Object[] argumentValues) throws ErrorException {
        Object res;
        if (object.interfaceName.equals("core.storemanager.StoreManager") && object.method.equals("initializeStore")) {
            res = storeManager.initializeStore((String)argumentValues[0], (String)argumentValues[1]);
        } else if (object.interfaceName.equals("core.storemanager.StoreManager") && object.method.equals("createStore")) {
            res = storeManager.createStore((String)argumentValues[0], (String)argumentValues[1], (String)argumentValues[2]);
        } else {
            StoreHandler handler = getStoreHandler(object.sessionId);
            if(handler == null) {
                throw new ErrorException(1000010);
            }
            res = handler.executeMethod(object, types, argumentValues);
        }
        
        StoreHandler handler = getStoreHandler(object.sessionId);
        if(handler != null) {
            handler.invokeCache(object, types, argumentValues);
            handler.logApiCall(object);
        }
        return res;
    }

    public StoreHandler getStoreHandler(String sessionId) throws ErrorException {
        Store store = storeManager.getStoreBySessionId(sessionId);
        if(store == null) {
            return null;
        }
        return get(store.id);
    }
    
    private StoreHandler get(String storeId) {
        StoreHandler handler = storeHandlers.get(storeId);
        if (handler == null) {
            handler = new StoreHandler(storeId);
            storeHandlers.put(storeId, handler);
        }
        return handler;
    }

    public StoreHandler getStorePool(String storeId) {
        return storeHandlers.get(storeId);
    }
}
