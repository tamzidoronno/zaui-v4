
package com.thundashop.core.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.thundashop.core.common.AnnotationExclusionStrategy;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ErrorMessage;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.GetShopLogging;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.MessageBase;
import com.thundashop.core.common.StorePool;
import com.thundashop.core.common.WebSocketReturnMessage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
            Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();
        
            JsonObject2 inObject = gson.fromJson(message, JsonObject2.class);
            if (inObject.version != null && inObject.version.equals("2")) {
                WebSocketReturnMessage msg = new WebSocketReturnMessage();
                msg.messageId = inObject.messageId;
                msg.object = new ErrorMessage(d);
                sendMessage(msg);
            } else {
                sendMessage(new ErrorMessage(d));
            }
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
            if (!(ex instanceof ErrorException)) {
                ex.printStackTrace();
            }
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
                builder.serializeSpecialFloatingPointValues();
                builder.setExclusionStrategies(new AnnotationExclusionStrategy(null));
                builder.registerTypeAdapter(Date.class, new GsonUTCDateAdapter()).create();
                Gson gson = builder.create();
                json = gson.toJson((Object) result);
            }catch(Exception e) {
                GetShopLogHandler.logPrintStatic(result, null);
                e.printStackTrace();
            }
            dos.write((json + "\n").getBytes("UTF8"));
            dos.flush();
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private boolean isV2(String message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
