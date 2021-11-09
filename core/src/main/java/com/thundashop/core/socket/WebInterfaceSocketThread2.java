
package com.thundashop.core.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thundashop.core.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

/**
 * @author ktonder
 */
public class WebInterfaceSocketThread2 implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(WebInterfaceSocketThread2.class);

    private final StorePool storePool;
    private final Socket socket;

    public WebInterfaceSocketThread2(Socket socket, StorePool storePool) {
        this.storePool = storePool;
        this.socket = socket;
    }

    private void executeMessage(String message, String addr) {
        try {
            Object result = storePool.ExecuteMethod(message, addr);
            sendMessage(result);
        } catch (ErrorException d) {
            log.error("Error while executing, message `{}`, addr `{}`", message, addr, d);

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

    }

    @Override
    public void run() {
        try {
            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String json = br.readLine();
                if (json == null) {
                    return;
                }
                executeMessage(json, socket.getInetAddress().getHostAddress());
            }
        } catch (Exception ex) {
            if (!(ex instanceof ErrorException)) {
                log.error("", ex);
            }
        } finally {
            try {
                socket.close();
            } catch (Exception ex) {
                log.error("", ex);
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
                json = gson.toJson(result);
            } catch (Exception e) {
                log.error("", e);
            }
            dos.write((json + "\n").getBytes("UTF8"));
            dos.flush();
        } catch (Exception d) {
            log.error("", d);
        }
    }

}
