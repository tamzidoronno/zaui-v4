/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import org.owasp.validator.html.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.storemanager.data.Store;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ktonder
 */
public class StorePool {

    private HashMap<String, StoreHandler> storeHandlers = new HashMap();
    private com.thundashop.core.storemanager.StorePool storePool;
    private final AntiSamy antiySamy;
    private Policy policy;

    public StorePool() {
        if (AppContext.appContext != null) {
            this.storePool = AppContext.appContext.getBean(com.thundashop.core.storemanager.StorePool.class);
        }
        
        try {
             policy = Policy.getInstance(getClass().getResource("/antisamy-myspace-1.4.4.xml"));
        } catch (Exception ex) {
            System.out.println("Could not find the antisamy policy file, can not continue unsecurly");
            ex.printStackTrace();
            System.exit(1);
        }
        
        antiySamy = new AntiSamy();
        
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
        } catch (Exception ex) {
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
                case "long":
                    classLoaded = long.class;
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
    
    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    public static boolean isWrapperType(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }

    public Object ExecuteMethod(String message, String addr) throws ErrorException {
        return ExecuteMethod(message, addr, null);
    }

    private Object scan(Object objectToScan) throws IllegalArgumentException, IllegalAccessException, ScanException, PolicyException {
        if (objectToScan == null) {
            return objectToScan;
        }
        
        if (objectToScan instanceof String) {
            CleanResults result2 = antiySamy.scan(objectToScan.toString(), policy);
            return result2.getCleanHTML();
        } else if( objectToScan.getClass().isEnum()) {
            return objectToScan;
        } else {
            for (Field field : objectToScan.getClass().getFields()) {
                if (field.getType().equals(String.class)) {
                    String text = (String) field.get(objectToScan);
                    if (text == null) {
                        field.set(objectToScan, text);
                    } else {
                        CleanResults result2 = antiySamy.scan(text, policy);
                        field.set(objectToScan, result2.getCleanHTML());
                    }
                } else if(isWrapperType(objectToScan.getClass())) {
                    return objectToScan;
                } else {
                    field.set(objectToScan, scan(field.get(objectToScan)));
                }
            }
        }
        
        return objectToScan;
    }
    
    public Object ExecuteMethod(String message, String addr, String sessionId) throws ErrorException {
        Gson gson = new GsonBuilder().serializeNulls().create();

        Type type = new TypeToken<JsonObject2>() {
        }.getType();

        message = message.replace("\"args\":[]", "\"args\":{}");

        JsonObject2 object = null;
        
        try {
            object = gson.fromJson(message, type);
            object.addr = addr;
        } catch (JsonSyntaxException ex) {
            System.out.println("Could not decode: " + message);
            ex.printStackTrace();
            return null;
        }
        
        object.multiLevelName = gson.fromJson(object.multiLevelName, String.class);

        int i = 0;
        Object[] executeArgs = new Object[object.args.size()];
        Class[] types = getArguments(object);
        Type[] casttypes = getArgumentsTypes(object);
        for (String parameter : object.args.keySet()) {
            try {
                Class classLoaded = getClass(types[i].getCanonicalName());
            }catch(Exception e) {
                System.out.println("test");
            }
            try {
                Object argument = gson.fromJson(object.args.get(parameter), casttypes[i]);
                executeArgs[i] = argument;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Cast type: " + casttypes[i]);
                System.out.println("From json param: " + object.args.get(parameter));
                System.out.println("From json paramValue: " + object.args.get(parameter));
                System.out.println("From json message: " + message);
                ErrorException ex = new ErrorException(100);
                ex.additionalInformation = e.getMessage();
                throw ex;
            }
            i++;
        }

        object.realInterfaceName = object.interfaceName;
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
    
    private Object[] runTroughAntiSamy(JsonObject2 object, Object[] argumentValues) throws ErrorException {
        StoreHandler storeHandler = getStoreHandler(object.sessionId);
        
        if (storeHandler != null && (storeHandler.isAdministrator(object.sessionId, object) || storeHandler.isEditor(object.sessionId, object))) {
            return argumentValues;
        }
        
        Object[] cleanOject = new Object[argumentValues.length];
        int i = 0;
        for (Object arg : argumentValues) {
            try { 
                cleanOject[i] = scan(arg); 
            } catch (Exception ex) { ex.printStackTrace(); }
            i++;
        }
        return cleanOject;
    }

    private Object ExecuteMethod(JsonObject2 object, Class[] types, Object[] argumentValues) throws ErrorException {
        argumentValues = runTroughAntiSamy(object, argumentValues);
        
        Object res;
        if (object.interfaceName.equals("core.storemanager.StoreManager") && object.method.equals("initializeStore")) {
            res = storePool.initialize((String) argumentValues[0], (String) argumentValues[1]);
        } else if (object.interfaceName.equals("core.storemanager.StoreManager") && object.method.equals("createStore")) {
            res = storePool.createStoreObject((String) argumentValues[0], (String) argumentValues[1], (String) argumentValues[2], (boolean) argumentValues[3]);
        } else if (object.interfaceName.equals("core.storemanager.StoreManager") && object.method.equals("autoCreateStore")) {
            res = storePool.autoCreateStoreObject((String) argumentValues[0]);
            res = storePool.initialize((String) argumentValues[0], object.sessionId);
        } else {
            StoreHandler handler = getStoreHandler(object.sessionId);
            if (handler == null) {
                throw new ErrorException(1000010);
            }
            res = handler.executeMethod(object, types, argumentValues);
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

    private Method getMethod(JsonObject2 object) throws ClassNotFoundException, SecurityException {
        Class aClass = getClass().getClassLoader().loadClass("com.thundashop." + object.interfaceName);
        
        Method[] methods = aClass.getMethods();
        Method method = null;
        for (Method tmpMethod : methods) {
            if (tmpMethod.getName().equals(object.method) && tmpMethod.getGenericParameterTypes().length == object.args.size()) {
                method = tmpMethod;
            }
        }
        if (method == null) {
            System.out.println("Failed on interface: " + object.interfaceName);
            System.out.println("Failed on method: " + object.method);
            System.out.println("Failed on size: " + object.args.size());
        }
        return method;
    }

    public void stop(Store store) {
        storeHandlers.remove(store.id);
    }
}
