
package com.thundashop.core.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ErrorMessage;
import com.thundashop.core.common.MessageBase;
import com.thundashop.core.common.StorePool;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
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
    
    private List<MessageBase> executeMessage(String message, String addr) {
        try {
            Object result = storePool.ExecuteMethod(message, addr);
            sendMessage(result);
        } catch (ErrorException d) {
            sendMessage(new ErrorMessage(d));
        }
   
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
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            String json = "";
            try {
                GsonBuilder builder = new GsonBuilder();
                builder.serializeNulls();
                builder.disableInnerClassSerialization();
                builder.registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {   

                    @Override
                    public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                        if(src.isNaN()) {
                            return new JsonPrimitive(0);
                        }
                        if(src.isInfinite()) {
                            return new JsonPrimitive(999999999);
                        }
                        return new JsonPrimitive(src);
                    }
                });
                
                Gson gson = builder.create();
                json = gson.toJson((Object) result);
            }catch(Exception e) {
                e.printStackTrace();
            }
            dos.write((json + "\n").getBytes("UTF8"));
            dos.flush();
        } catch (Exception d) {
            d.printStackTrace();
        }
    }
}
