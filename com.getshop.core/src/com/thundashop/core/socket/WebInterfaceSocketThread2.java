
package com.thundashop.core.socket;

import com.thundashop.core.common.JsonObject2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ErrorMessage;
import com.thundashop.core.common.MessageBase;
import com.thundashop.core.common.StorePool;
import java.io.BufferedReader;
import java.io.DataOutputStream;
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
                System.out.print(json);
                executeMessage(json, socket.getInetAddress().getHostAddress());
                System.out.println(" - done");
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
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.write((json + "\n").getBytes("UTF8"));
            dos.flush();
        } catch (Exception d) {
            d.printStackTrace();
        }
    }
}
