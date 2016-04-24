
package com.thundashop.core.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ErrorMessage;
import com.thundashop.core.common.MessageBase;
import com.thundashop.core.common.StorePool;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    
    private List<MessageBase> executeMessage(String message, String addr) throws IOException {
        String cachedResult = storePool.getCachedResult(message, addr);
        
        if (cachedResult != null) {
            sendContent(cachedResult);
        } else {
            try {
                Object result = storePool.ExecuteMethod(message, addr);
                sendMessage(result, message, addr);
            } catch (ErrorException d) {
                sendMessage(new ErrorMessage(d), message, addr);
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
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void sendMessage(Object result, String message, String addr) {
        try {
            Gson gson = new GsonBuilder().serializeNulls().disableInnerClassSerialization().create();
            String json = gson.toJson((Object) result);
            sendContent(json);
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void sendContent(String json) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.write((json + "\n").getBytes("UTF8"));
        dos.flush();
    }
}
