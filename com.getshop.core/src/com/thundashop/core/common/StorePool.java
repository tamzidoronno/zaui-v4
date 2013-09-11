/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.storemanager.data.Store;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ktonder
 */
public class StorePool {

    private HashMap<String, StoreHandler> storeHandlers = new HashMap();
    private com.thundashop.core.storemanager.StorePool storePool;

    public StorePool() {
        if (AppContext.appContext != null) {
            this.storePool = AppContext.appContext.getBean(com.thundashop.core.storemanager.StorePool.class);
        }
    }

    private Type[] getArgumentsTypes(JsonObject2 object) throws ErrorException {
        try {
            Method method = getMethod(object);
            return method.getGenericParameterTypes();
        } catch (ClassNotFoundException ex) {
            throw new ErrorException(81);
        }

    }

    private Class<?>[] getArguments(JsonObject2 object) throws ErrorException {
        try {
            Method method = getMethod(object);
            return (Class<?>[]) method.getParameterTypes();
        } catch (ClassNotFoundException ex) {
            throw new ErrorException(81);
        }
    }

    private Class getClass(String className) throws ErrorException {
        try {
            Class classLoaded = null;
            switch (className) {
                case "int":
                    classLoaded = int.class;
                    break;
                case "boolean":
                    classLoaded = boolean.class;
                    break;
                case "double":
                    classLoaded = double.class;
                    break;
                case "float":
                    classLoaded = float.class;
                    break;
                default:
                    classLoaded = this.getClass().getClassLoader().loadClass(className);
                    break;
            }

            return classLoaded;
        } catch (ClassNotFoundException ex) {
            throw new ErrorException(81);
        }
    }

    public Object ExecuteMethod(String message, String addr) throws ErrorException {
        return ExecuteMethod(message, addr, null);
    }

    public Object ExecuteMethod(String message, String addr, String sessionId) throws ErrorException {
        Gson gson = new GsonBuilder().serializeNulls().create();

        Type type = new TypeToken<JsonObject2>() {
        }.getType();

        message = message.replace("\"args\":[]", "\"args\":{}");

        JsonObject2 object = gson.fromJson(message, type);
        object.addr = addr;

        int i = 0;
        Object[] executeArgs = new Object[object.args.size()];
        Class[] types = getArguments(object);
        Type[] casttypes = getArgumentsTypes(object);
        for (String parameter : object.args.keySet()) {
            Class classLoaded = getClass(types[i].getCanonicalName());
            try {
                Object argument = gson.fromJson(object.args.get(parameter), casttypes[i]);
                executeArgs[i] = argument;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("From json param: " + object.args.get(parameter));
                System.out.println("From json message: " + message);
            }
            i++;
        }

        object.interfaceName = object.interfaceName.replace(".I", ".");

        if (sessionId != null) {
            object.sessionId = sessionId;
        }

        long start = System.currentTimeMillis();
        Object result = ExecuteMethod(object, types, executeArgs);

        long end = System.currentTimeMillis();
        long diff = end - start;
        if (diff > 40) {
            System.out.println("" + diff + " : " + object.interfaceName + " method: " + object.method);
        }
        result = (result == null) ? new ArrayList() : result;

        if (!object.messageId.equals("")) {
            WebSocketReturnMessage returnmessage = new WebSocketReturnMessage();
            returnmessage.messageId = object.messageId;
            returnmessage.object = result;
            return returnmessage;
        }

        return result;
    }

    private Object ExecuteMethod(JsonObject2 object, Class[] types, Object[] argumentValues) throws ErrorException {
        Object res;
        if (object.interfaceName.equals("core.storemanager.StoreManager") && object.method.equals("initializeStore")) {
            res = storePool.initialize((String) argumentValues[0], (String) argumentValues[1]);
        } else if (object.interfaceName.equals("core.storemanager.StoreManager") && object.method.equals("createStore")) {
            res = storePool.createStoreObject((String) argumentValues[0], (String) argumentValues[1], (String) argumentValues[2], (boolean) argumentValues[3]);
        } else {
            StoreHandler handler = getStoreHandler(object.sessionId);
            if (handler == null) {
                throw new ErrorException(1000010);
            }
            res = handler.executeMethod(object, types, argumentValues);
        }

        StoreHandler handler = getStoreHandler(object.sessionId);
        if (handler != null) {
            handler.logApiCall(object);
        }
        return res;
    }

    private StoreHandler getStoreHandler(String sessionId) throws ErrorException {
        Store store = storePool.getStoreBySessionId(sessionId);
        if (store == null) {
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
        return get(storeId);
    }

    private Method getMethod(JsonObject2 object) throws ClassNotFoundException, SecurityException {
        Class aClass = getClass().getClassLoader().loadClass("com.thundashop." + object.interfaceName);
        Method[] methods = aClass.getMethods();
        Method method = null;
        for (Method tmpMethod : methods) {
            if (tmpMethod.getName().equals(object.method)) {
                method = tmpMethod;
            }
        }
        if (method == null) {
            System.out.println("Failed on obj: " + object.interfaceName);
            System.out.println("Failed on obj: " + object.method);
        }
        return method;
    }

    public void stop(Store store) {
        storeHandlers.remove(store.id);
    }
}
