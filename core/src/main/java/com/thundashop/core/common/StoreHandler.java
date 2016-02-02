 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.getshop.scope.GetShopSessionBeanNamed;
import com.getshop.scope.GetShopSessionObject;
import com.getshop.scope.GetShopSessionScope;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thundashop.core.usermanager.IUserManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserPrivilege;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.BeansException;

/**
 *
 * @author ktonder
 */
public class StoreHandler {

    private List<ManagerBase> messageHandler;
    private String storeId;
    private HashMap<String, Session> sessions = new HashMap();
    private GetShopSessionScope scope;
    private ArrayList<GetShopSessionObject> sessionScopedBeans;

    public StoreHandler(String storeId) {
        this.storeId = storeId;
        try {
            scope = AppContext.appContext.getBean(GetShopSessionScope.class);
        } catch (BeansException ex) {
            System.out.println("Throws bean exception?");
        }
    }
        
    public synchronized Object executeMethod(JsonObject2 inObject, Class[] types, Object[] argumentValues) throws ErrorException {
        scope.setStoreId(storeId, inObject.multiLevelName, getSession(inObject.sessionId));
        Class getShopInterfaceClass = loadClass(inObject.realInterfaceName);
        setSessionObject(inObject.sessionId, getShopInterfaceClass, inObject);

        Class aClass = loadClass(inObject.interfaceName);
        Method executeMethod = getMethodToExecute(aClass, inObject.method, types, argumentValues);

        try {
            Annotation userLevel = authenticateUserLevel(executeMethod, aClass, getShopInterfaceClass, inObject);
            Object result = invokeMethod(executeMethod, aClass, argumentValues, getShopInterfaceClass, inObject);
            clearSessionObject();
            result = cloneResult(result);
            return result;
        } catch (ErrorException ex) {
            if (ex.code == 26) {
                User user = findUser(getShopInterfaceClass, inObject);
                String userInfo = "";
                if (user != null) {
                    userInfo += " id: " + user.id;
                    userInfo += " name: " + user.fullName;
                    userInfo += " email: " + user.emailAddress;
                }

                System.out.println("Access denied, store: " + storeId + " , user={" + userInfo + "} method={" + aClass.getSimpleName() + "." + inObject.method + "}");
            }
            throw ex;
        }
    }

    public synchronized boolean isAdministrator(String sessionId, JsonObject2 object) throws ErrorException {
        scope.setStoreId(storeId, object.multiLevelName, getSession(sessionId));

        UserManager manager = getManager(UserManager.class, null, null);
        User user = manager.getUserBySessionId(sessionId);
        if (user != null) {
            return user.isAdministrator();
        }

        return false;
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

    private Class loadCacheClass(String objectName) throws ErrorException {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            return classLoader.loadClass("com.thundashop." + objectName + "Cache");
        } catch (ClassNotFoundException ex) {
        }
        return null;
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

    private Object invokeMethod(Method executeMethod, Class aClass, Object[] argObjects, Class getShopInterfaceClass, JsonObject2 inObject) throws ErrorException {
        try {
            ManagerSubBase manager = getManager(aClass, getShopInterfaceClass, inObject);
            Object result = executeMethod.invoke(manager, argObjects);
            result = manager.preProcessMessage(result, executeMethod);
            return result;
        } catch (IllegalAccessException ex) {
            throw new ErrorException(84);
        } catch (IllegalArgumentException ex) {
            throw new ErrorException(85);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            
            if (cause instanceof ErrorException) {
                throw (ErrorException) cause;
            } else {
                ex.printStackTrace();
            }

            ErrorException aex = new ErrorException(86);
            aex.additionalInformation = cause.getLocalizedMessage() + " <br> " + stackTraceToString(cause);
            throw aex;
        }
    }

    private String stackTraceToString(Throwable e) {
        String stackTrace = "";
        for (StackTraceElement element : e.getStackTrace()) {
            stackTrace += element.toString() + "\n";
        }
        return stackTrace;
    }
    
    private Session getSession(String sessionId) {
        if (!sessions.containsKey(sessionId)) {
            Session session = new Session();
            session.storeId = storeId;
            session.id = sessionId;
            sessions.put(sessionId, session);
            return session;
        } else {
            return sessions.get(sessionId);
        }
    }

    private void setSessionObject(String sessionId, Class getShopInterfaceClass, JsonObject2 inObject) {
        IUserManager userManager = getManager(IUserManager.class, getShopInterfaceClass, inObject);

        Session session = getSession(sessionId);
                
        if (!sessions.containsKey(sessionId)) {
            session = new Session();
            session.storeId = storeId;
            session.id = sessionId;
            sessions.put(sessionId, session);
        } else {
            session = sessions.get(sessionId);
        }

        session.lastActive = new Date();

        try {
            messageHandler = new ArrayList<ManagerBase>(AppContext.appContext.getBeansOfType(ManagerBase.class).values());
        } catch (BeansException ex) {
            System.out.println("Throws bean exception?");
        }
        for (ManagerBase base : messageHandler) {
            base.setSession(session);
        }
        
        // Set sessions for maps
        try {
            sessionScopedBeans = new ArrayList<>(AppContext.appContext.getBeansOfType(GetShopSessionObject.class).values());
            for (GetShopSessionObject base : sessionScopedBeans) {
                base.setSession(session);
            }
        } catch (BeansException ex) {
            System.out.println("Throws bean exception?");
        }
        
        for (GetShopSessionBeanNamed bean : scope.getSessionNamedObjects()) {
            bean.setSession(session);
        }
        
        try {
            session.currentUser = userManager.getLoggedOnUser();
        } catch (ErrorException ex) {
            session.currentUser = null;
        }
    }

    private void clearSessionObject() {
        for (ManagerBase base : messageHandler) {
            base.setSession(null);
        }
        for (GetShopSessionObject base : sessionScopedBeans) {
            base.clearSession();
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

    private synchronized Annotation authenticateUserLevel(Method executeMethod, Class aClass, Class getShopInterfaceClass, JsonObject2 inObject) throws ErrorException {
        executeMethod = getCorrectMethod(executeMethod);

        if (executeMethod.getAnnotation(Internal.class) != null) {
            throw new ErrorException(90);
        }

        Annotation userLevel = executeMethod.getAnnotation(GetShopAdministrator.class);

        if (userLevel == null) {
            userLevel = executeMethod.getAnnotation(Administrator.class);
        }

        if (userLevel == null) {
            userLevel = executeMethod.getAnnotation(Editor.class);
        }

        if (userLevel != null) {
            User user = findUser(getShopInterfaceClass, inObject);

            if (user == null || userLevel instanceof GetShopAdministrator && !user.isGetShopAdministrator()) {
                throw new ErrorException(26);
            }

            if (user != null && user.isGetShopAdministrator()) {
                return userLevel;
            }
            
            if (user != null && user.isAdministrator()) {
                return userLevel;
            }

            if (user != null && user.applicationAccessList.size() > 0) {
                if (checkApplicationsAccessByApp(user, executeMethod, aClass)) {
                    return userLevel;
                } else {
                    throw new ErrorException(26);
                }
            }

            if (user != null && (userLevel instanceof Administrator || userLevel instanceof Editor)) {
                checkUserPrivileges(user, executeMethod, aClass, getShopInterfaceClass, inObject);
            }

            if (user == null || userLevel instanceof Administrator && !user.isAdministrator()) {
                throw new ErrorException(26);
            }
            if (userLevel instanceof Editor && (!user.isEditor() && !user.isAdministrator())) {
                throw new ErrorException(26);
            }
        }
        return userLevel;
    }

    private void checkUserPrivileges(User user, Method executeMethod, Class aClass, Class getShopInterfaceClass, JsonObject2 inObject) throws ErrorException {
        if (user.privileges.isEmpty()) {
            return;
        }

        ManagerBase manager = getManager(aClass, getShopInterfaceClass, inObject);
        String managerName = manager.getClass().getSimpleName();

        for (UserPrivilege priv : user.privileges) {
            if (executeMethod.getName().equals(priv.managerFunction) && managerName.equals(priv.managerName)) {
                return;
            }
        }

        throw new ErrorException(26);
    }

    private User findUser(Class getShopInterfaceClass, JsonObject2 inObject) throws ErrorException {

        try {
            UserManager manager = getManager(UserManager.class, getShopInterfaceClass, inObject);
            return manager.getLoggedOnUser();
        } catch (ErrorException e) {
            // Errorhiding is an antipattern! :P
            // http://en.wikipedia.org/wiki/Error_hiding
        }
        return new User();
    }

    private <T> T getManager(Class aClass, Class getShopInterfaceClass, JsonObject2 inObject) {
        try {
//            if (getShopInterfaceClass != null && getShopInterfaceClass.getAnnotation(GetShopMultiLayerSession.class) != null) {
//                return (T) AppContext.appContext.getBean(aClass, inObject.multiLevelName);
//            } else {
                return (T) AppContext.appContext.getBean(aClass);
//            }
            
        } catch (BeansException ex) {
            System.out.println("Throws bean exception?");
        }

        return null;
    }

    private boolean checkApplicationsAccessByApp(User user, Method executeMethod, Class aClass) {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

//        for(ApplicationSettings setting : pool.applications.values()) {
//            for(String id : user.applicationAccessList.keySet()) {
//                if(setting.id.equals(id)) {
//                    int writeType = user.applicationAccessList.get(id);
//                    for(ApiCallsInUse inuse : setting.apiCallsInUse) {
//                        boolean isWriting = false;
//                        for(Annotation anno : executeMethod.getAnnotations()) {
//                            if(anno instanceof Writing) {
//                                isWriting = true;
//                            }
//                        }
//                        
//                        //Write access only
//                        if((writeType == 2) && !isWriting) {
//                            continue;
//                        }
//                        //Read access only
//                        if((writeType == 1) && isWriting) {
//                            continue;
//                        }
//                        
//                        if(inuse.manager.equals(aClass.getCanonicalName()) && inuse.method.equals(executeMethod.getName())) {
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
    }

    /**
     * We need to clone the data before leaving this class. This class is considered threadsafe, and leaking
     * live objects here is bad because they might be modified.
     * 
     * Ideal would have been to return a String instead of a cloned object.
     * 
     * @param <V>
     * @param result
     * @return 
     */
    private <V> V cloneResult(V result) {
        if (result == null) {
            return result;
        }
        
        Gson gson = new GsonBuilder().serializeNulls().disableInnerClassSerialization().create();
        String json = gson.toJson((Object) result);
        V retObject = (V)gson.fromJson(json, result.getClass());
        
        return retObject;
    }
}
