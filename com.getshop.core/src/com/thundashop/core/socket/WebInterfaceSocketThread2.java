
package com.thundashop.core.socket;

import com.thundashop.core.common.JsonObject2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ErrorMessage;
import com.thundashop.core.common.MessageBase;
import com.thundashop.core.common.StorePool;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class WebInterfaceSocketThread2 implements Runnable {
    private StorePool storePool;
    private Socket socket;

    public WebInterfaceSocketThread2(Socket socket, StorePool storePool) {
        this.storePool = storePool;
        this.socket = socket;
    }

    private Class getClass(String className) throws ErrorException {
        try {
            Class classLoaded = null;
            switch(className) {
                case "int": classLoaded = int.class; break;
                case "boolean": classLoaded = boolean.class; break;
                case "double": classLoaded = double.class; break;
                case "float": classLoaded = float.class; break;
                default: classLoaded = this.getClass().getClassLoader().loadClass(className); break;
            }

            return classLoaded;
        } catch (ClassNotFoundException ex) {
            throw new ErrorException(81);
        }
    }
    
    private Class<?>[] getArguments(JsonObject2 object) throws ErrorException {
        try {
            Class aClass = getClass().getClassLoader().loadClass("com.thundashop." + object.interfaceName);
        
            Method[] methods = aClass.getMethods();
            Method method = null;

            for (Method tmpMethod : methods) {
                if (tmpMethod.getName().equals(object.method)) {
                    method = tmpMethod;
                }
            }
            if(method == null) {
                System.out.println("Failed on obj: " + object.interfaceName);
                System.out.println("Failed on obj: " + object.method);
            }
            Class<?>[] parameters = method.getParameterTypes();
            return parameters;
        } catch (ClassNotFoundException ex) {
            throw new ErrorException(81);
        }
    }
    
    private List<MessageBase> executeMessage(String message, String addr) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        
        Type type = new TypeToken<JsonObject2>() {}.getType();
        
        message = message.replace("\"args\":[]", "\"args\":{}");
        
        JsonObject2 object = gson.fromJson(message, type);
        object.addr = addr;

        int i = 0;
        Object[] executeArgs = new Object[object.args.size()];
        try {
            Class[] types = getArguments(object);
            for (String parameter : object.args.keySet()) {
                Class classLoaded = getClass(types[i].getCanonicalName());
                try {
                    Object argument = gson.fromJson(object.args.get(parameter), classLoaded);
                    executeArgs[i] = argument;
                }catch(Exception e) {
                    e.printStackTrace();
                    System.out.println("From json param: " + object.args.get(parameter));
                    System.out.println("From json message: " + message);
                }
                i++;
            }

            object.interfaceName = object.interfaceName.replace(".I", ".");
            long start = System.currentTimeMillis();
            Object result = storePool.ExecuteMethod(object, types, executeArgs);
            long end = System.currentTimeMillis();
            long diff = end-start;
            if(diff > 40) {
                System.out.println("" + diff + " : " + object.interfaceName + " method: " + object.method);
            }
            result = (result == null) ? new ArrayList() : result;
            sendMessage(result);
        } catch (ErrorException d) {
            sendMessage(new ErrorMessage(d));
        }
        
        AppContext.cacheManager.executeOKSignal(object.addr);
        
        List<MessageBase> messages = new ArrayList();
        return messages;
    }

    @Override
    public void run() {
        String json = "";
        try {
            while(true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                json = br.readLine();
                if (json == null) {
                    return;
                }
                executeMessage(json, socket.getInetAddress().getHostAddress());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void sendMessage(Object result) {
        try {
            Gson gson = new GsonBuilder().serializeNulls().disableInnerClassSerialization().create();
            String json = gson.toJson((Object) result);
            PrintWriter bw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            bw.println(json);
            bw.flush();
        } catch (Exception d) {
            d.printStackTrace();
        }
    }
}
