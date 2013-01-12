 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.thundashop.core.common.*;
import com.thundashop.core.loggermanager.LoggerManager;
import com.thundashop.core.reportingmanager.ReportingManager;
import com.thundashop.core.socket.JsonObject2;
import com.thundashop.core.usermanager.IUserManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author ktonder
 */
public class StoreHandler {

    public List<ManagerBase> messageHandler;
    private String storeId;
    private Session session;
    // remove this.
    private User testUser;

    @Deprecated
    public void setTestUser(User user) {
        testUser = user;
    }

    public StoreHandler(String storeId) {
        this.storeId = storeId;
        messageHandler = new ArrayList<ManagerBase>(AppContext.appContext.getBeansOfType(ManagerBase.class).values());
        init();
    }

    private void init() {
        for (ManagerBase base : messageHandler) {
            base.session = session;
            if (!base.isSingleton) {
                base.storeId = storeId;
                base.initialize();
            }
        }
    }

    public synchronized Object executeMethod(JsonObject2 inObject, Class[] types, Object[] argumentValues) throws ErrorException {
        setSessionObject(inObject.sessionId);
        Class aClass = loadClass(inObject.interfaceName);
        Method executeMethod = getMethodToExecute(aClass, inObject.method, types);
        authenticateUserLevel(executeMethod);
        Object result = invokeMethod(executeMethod, aClass, argumentValues);
        clearSessionObject();
        return result;
    }

    private Class loadClass(String objectName) throws ErrorException {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            return classLoader.loadClass("com.thundashop." + objectName);
        } catch (ClassNotFoundException ex) {
            ErrorException gex = new ErrorException(81);
            gex.additionalInformation = ex.getMessage();
            throw gex;
        }
    }

    private Class loadCacheClass(String objectName) throws ErrorException {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            return classLoader.loadClass("com.thundashop." + objectName + "Cache");
        } catch (ClassNotFoundException ex) {
        }
        return null;
    }

    private Method getMethodToExecute(Class aClass, String method, Class[] types) throws ErrorException {
        try {
            return aClass.getMethod(method, types);
        } catch (NoSuchMethodException ex) {
            throw new ErrorException(82);
        } catch (SecurityException ex) {
            throw new ErrorException(83);
        }
    }

    private Object invokeMethod(Method executeMethod, Class aClass, Object[] argObjects) throws ErrorException {
        try {
            ManagerBase manager = getManager(aClass);
            return executeMethod.invoke(manager, argObjects);
        } catch (IllegalAccessException ex) {
            throw new ErrorException(84);
        } catch (IllegalArgumentException ex) {
            throw new ErrorException(85);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof ErrorException) { throw (ErrorException) cause; }

            cause.printStackTrace();
            ErrorException aex = new ErrorException(86);
            aex.additionalInformation = cause.getLocalizedMessage() + " <br> " + stackTraceToString(cause);
            throw aex;
        }
    }
    

    public void invokeCache(JsonObject2 obj, Class[] types, Object[] argumentValues) throws ErrorException {
        setSessionObject(obj.sessionId);
        try {
            Class cacheClass = loadCacheClass(obj.interfaceName);
            if(cacheClass == null) {
                return;
            }
            
            Class aClass = loadClass(obj.interfaceName);
            
            ManagerBase manager = getManager(aClass);
            Method executeMethod = getMethodToExecute(cacheClass, obj.method, types);
            try {
                Object toExcetute = cacheClass.getConstructor(manager.getClass(), String.class).newInstance(manager, obj.addr);
                executeMethod.invoke(toExcetute, argumentValues);
            }catch(Exception e) {
//               e.printStackTrace();
            }
            
        } catch (IllegalArgumentException ex) {
            throw new ErrorException(85);
        }
        clearSessionObject();
    }

    private String stackTraceToString(Throwable e) {
        String stackTrace = "";
        for (StackTraceElement element : e.getStackTrace()) {
            stackTrace += element.toString() + "\n";
        }
        return stackTrace;
    }

    private void setSessionObject(String sessionId) {
        IUserManager userManager = getManager(IUserManager.class);

        session = new Session();
        session.storeId = storeId;
        session.id = sessionId;

        for (ManagerBase base : messageHandler) {
            base.session = session;
        }

        try {
            session.currentUser = userManager.getLoggedOnUser();
        } catch (ErrorException ex) {
            session.currentUser = null;
        }
    }

    private void clearSessionObject() {
        for (ManagerBase base : messageHandler) {
            base.session = null;
        }
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

    public void authenticateUserLevel(Method executeMethod) throws ErrorException {
        executeMethod = getCorrectMethod(executeMethod);

        if (executeMethod.getAnnotation(Internal.class) != null) {
            throw new ErrorException(90);
        }

        Annotation userLevel = executeMethod.getAnnotation(Administrator.class);
        if (userLevel == null) {
            userLevel = executeMethod.getAnnotation(Editor.class);
        }

        if (userLevel != null) {
            User user = findUser();
            if (user == null || userLevel instanceof Administrator && !user.isAdministrator()) {
                throw new ErrorException(26);
            }
            if (user == null || userLevel instanceof Editor && (!user.isEditor() && !user.isAdministrator())) {
                throw new ErrorException(26);
            }
        }
    }

    private User findUser() throws ErrorException {
        // REMOVE THIS
        if (testUser != null) {
            return testUser;
        }

        try {
            UserManager manager = getManager(UserManager.class);
            return manager.getLoggedOnUser();
        } catch (ErrorException e) {
            // Errorhiding is an antipattern! :P
            // http://en.wikipedia.org/wiki/Error_hiding
        }
        return new User();
    }

    public <T> T getManager(Class aClass) {
        for (ManagerBase handler : messageHandler) {
            if (aClass.isAssignableFrom(handler.getClass())) {
                return (T) handler;
            }
        }

        return null;
    }

    public void sendEvent(ManagerBase managerBase, String eventName, String eventReferance) {
        for (ManagerBase handler : messageHandler) {
            if (handler.equals(managerBase)) {
                continue;
            }

            ManagerBase mngBase = (ManagerBase) handler;
            mngBase.onEventPrivate(eventName, eventReferance);
        }
    }

    public void logApiCall(JsonObject2 object) throws ErrorException {
        setSessionObject(object.sessionId);
        
        //Save the data.
        LoggerManager manager = getManager(LoggerManager.class);
        manager.logApiCall(object);
        
        //Process it.
        ReportingManager reporting = getManager(ReportingManager.class);
        reporting.processApiCall(object);
    }
}