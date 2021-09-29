/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.storemanager.data.Store;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class StorePool {
    private final HashMap<String, StoreHandler> storeHandlers = new HashMap<>();
    private com.thundashop.core.storemanager.StorePool storePool;
    private Date lastTimePrintedTimeStampToLog = null;

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
  
    public Object ExecuteMethod(String message, String addr, String sessionId) throws ErrorException {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();
        
        Gson gsonWithVirusScanner = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(String.class, new VirusScanner())
                .create();
                
        
        Type type = new TypeToken<JsonObject2>() {
        }.getType();

        message = message.replace("\"args\":[]", "\"args\":{}");

        JsonObject2 object = null;
        
        
        if(lastTimePrintedTimeStampToLog == null) {
            System.out.println("####################################################################################################################################");
            System.out.println("####################################################  " + new Date() + "  ################################################");
            System.out.println("####################################################################################################################################");
            lastTimePrintedTimeStampToLog = new Date();
        } else {
            long diff = System.currentTimeMillis() - lastTimePrintedTimeStampToLog.getTime();
            if(diff > (1000*60*5)) {
                System.out.println("####################################################################################################################################");
                System.out.println("####################################################  " + new Date() + "  ################################################");
                System.out.println("####################################################################################################################################");
                lastTimePrintedTimeStampToLog = new Date();
            }
        }
        
        try {
            object = gson.fromJson(message, type);
            object.addr = addr;
        } catch (JsonSyntaxException ex) {
            GetShopLogHandler.logPrintStatic("Could not decode: " + message, null);
            ex.printStackTrace();
            return null;
        }
        try {
            object.multiLevelName = gson.fromJson(object.multiLevelName, String.class);
        }catch(Exception e) {
            //invalid mulitlevelname.
        }
       
        int i = 0;
        Object[] executeArgs = new Object[object.args.size()];
        Class[] types = getArguments(object);
        Type[] casttypes = getArgumentsTypes(object);
        for (String parameter : object.args.keySet()) {
            try {
                Class classLoaded = getClass(types[i].getCanonicalName());
            }catch(Exception e) {
                GetShopLogHandler.logPrintStatic("test", null);
            }
            try {
                Gson useGson = isAdministrator(object) || whiteLabeledForVirusScans(object) ? gson : gsonWithVirusScanner;
                Object argument = useGson.fromJson(object.args.get(parameter), casttypes[i]);
                executeArgs[i] = argument;
            } catch (Exception e) {
                GetShopLogHandler.logPrintStatic("Cast type: " + casttypes[i], null);
                GetShopLogHandler.logPrintStatic("From json param: " + object.args.get(parameter), null);
                GetShopLogHandler.logPrintStatic("From json paramValue: " + object.args.get(parameter), null);
                GetShopLogHandler.logPrintStatic("From json message: " + message, null);
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

        Object result = ExecuteMethod(object, types, executeArgs);
        
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
        if (object.interfaceName.equals("core.usermanager.UserManager") && object.method.equals("getPingoutTime")) {
            return handleSpecialTimeoutCheck(object, types, argumentValues);
        }
        
        
        Object res;
        if (object.interfaceName.equals("core.storemanager.StoreManager") && object.method.equals("initializeStore")) {
            res = storePool.initialize((String) argumentValues[0], (String) argumentValues[1]);
        } else if (object.interfaceName.equals("core.storemanager.StoreManager") && object.method.equals("initializeStoreWithModuleId")) {
            res = storePool.initialize((String) argumentValues[0], (String) argumentValues[1]);
            StoreHandler handler = getStoreHandler(object.sessionId);
            if(handler != null) {
                handler.setGetShopModule((String) argumentValues[1], (String) argumentValues[2]);
            }
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
            
            object.storeId = handler.getStoreId();
            Class aClass = loadClass(object.interfaceName);
            Method method = getMethodToExecute(aClass, object.method, types, argumentValues);
            method = getCorrectMethod(method);

            try {
                try {
                    if ((aClass != null && method != null) && (method.getAnnotation(GetShopNotSynchronized.class) != null || method.getAnnotation(ForceAsync.class) != null)) {
                        if (method.getAnnotation(GetShopNotSynchronized.class) != null && aClass.getAnnotation(GetShopSession.class) != null) {
                            throw new RuntimeException("@GetShopNotSynchronized can not be used on components that is scoped with @GetShopSession");
                        }
                        res = handler.executeMethod(object, types, argumentValues, false);
                    } else {
                        res = handler.executeMethodSync(object, types, argumentValues);
                    }
                    
                    try {
                        logToTimerLoggedToFile(object);
                    }catch(Exception e) {
                        GetShopLogHandler.logPrintStatic(e, object.storeId);
                    }
                }catch(Exception x) {
                    
                    if (!(x instanceof ErrorException)) {
                        GetShopLogHandler.logPrintStatic("Exception: " + x.getMessage(), handler.getStoreId());
                        x.printStackTrace();
                    }
                    throw x;
                }
            }catch(ErrorException x) {
                throw x;
            }
        }
        
        return res;
    }
    
    
    private Method getCorrectMethod(Method executeMethod) throws ErrorException {
        Class<?>[] interfaces = executeMethod.getDeclaringClass().getInterfaces();

        ErrorException retex = new ErrorException(86);
        for (Class iface : interfaces) {
            if (iface.isAnnotationPresent(GetShopApi.class)) {
                try {
                    return iface.getDeclaredMethod(executeMethod.getName(), executeMethod.getParameterTypes());
                } catch (Exception ex) {
                    retex.additionalInformation = ex.getMessage();
                    throw retex;
                }
            }
        }

        retex.additionalInformation = "Did not find the interface method for the called method";
        throw retex;
    }

    
    private Method getMethodToExecute(Class aClass, String method, Class[] types, Object[] argumentValues) throws ErrorException {
        try {
            for (Method emethod : aClass.getMethods()) {
                if (emethod != null && emethod.getName().equals(method) && emethod.getParameterTypes().length == argumentValues.length) {
                    return emethod;
                }
            }

            return aClass.getMethod(method, types);
        } catch (NoSuchMethodException ex) {
            throw new ErrorException(82);
        } catch (SecurityException ex) {
            throw new ErrorException(83);
        }
    }
    
    private Class loadClass(String objectName) throws ErrorException {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            return classLoader.loadClass("com.thundashop." + objectName);
        } catch (ClassNotFoundException ex) {
            ErrorException gex = new ErrorException(81);
            gex.additionalInformation = ex.getMessage();
            ex.printStackTrace();
            throw gex;
        }
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
            GetShopLogHandler.logPrintStatic("Failed on interface: " + object.interfaceName, null);
            GetShopLogHandler.logPrintStatic("Failed on method: " + object.method, null);
            GetShopLogHandler.logPrintStatic("Failed on size: " + object.args.size(), null);
        }
        return method;
    }

    public void stop(Store store) {
        storeHandlers.remove(store.id);
    }

    private Object handleSpecialTimeoutCheck(JsonObject2 object, Class[] types, Object[] argumentValues) {
        Store store = storePool.getStoreBySessionIdDontUpdateSession(object.sessionId);
        if (store == null) {
            return "notinitted";
        }
        
        StoreHandler storeHandler = get(store.id);
        return storeHandler.executeMethodSync(object, types, argumentValues);
   }
    
    public void executeGetShopThread(GetShopThread thread) {
        String sessionId = UUID.randomUUID().toString();
        storePool.initStoreByStoreId(thread.getStoreId(), sessionId);
        StoreHandler handler = getStoreHandler(sessionId);
        handler.executeMethodGetShopThread(thread, sessionId);
    }

    public void sessionRemoved(String sessionId) {
        storeHandlers.values().stream()
                .forEach(handler -> {
                    handler.sessionRemoved(sessionId);
                });
    }

    private boolean isAdministrator(JsonObject2 object) {
        if (object.sessionId == null || object.sessionId.isEmpty())
            return false;
        
        StoreHandler storeHandler = getStoreHandler(object.sessionId);
        if (storeHandler == null) {
            return false;
        }
        
        boolean isAdmin = storeHandler.isAdministrator(object.sessionId, object);
        return isAdmin;
    }

    private boolean whiteLabeledForVirusScans(JsonObject2 object) {
      
        
        if (object.interfaceName != null && object.interfaceName.equals("core.getshop.IGetShop")) {
            if (object.method != null && object.method.equals("insertNewStore")) {
                return true;
            }
        }
        if (object.interfaceName != null && object.interfaceName.equals("core.ticket.ITicketManager")) {
            if (object.method != null && object.method.equals("uploadAttachment")) {
                return true;
            }
        }
        
        return false;
    }

    private void logToTimerLoggedToFile(JsonObject2 obj) throws Exception {
        long timer = (System.currentTimeMillis() - obj.started.getTime())/1000;
        if(timer > 2) {
            if(obj.interfaceName.equals("core.gsd.GdsManager")) {
                return;
            }
            if(obj.interfaceName.equals("core.applications.StoreApplicationPool")) {
                return;
            }
            
           File yourFile = new File("timerLogged.txt");
           yourFile.createNewFile(); 

           String result = new Date()+ ";" + obj.id + ";" + obj.storeId + ";" + obj.interfaceName + ";" + obj.method + ";" + timer + "\n";
           Path path = Paths.get("timerLogged.txt");
           Files.write(path, result.getBytes(), StandardOpenOption.APPEND);  //Append mode
        }
    }
}
