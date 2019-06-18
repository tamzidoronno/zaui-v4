package getshopiotserver;

import getshopiotserver.processors.ProcessPrinterMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.gsd.DevicePrintMessage;
import com.thundashop.core.gsd.DirectPrintMessage;
import com.thundashop.core.gsd.GdsAccessDenied;
import com.thundashop.core.gsd.GdsPaymentAction;
import com.thundashop.core.gsd.GetShopDeviceMessage;
import getshop.nets.GetShopNetsApp;
import getshopiotserver.processors.ProcessAccessDenied;
import getshopiotserver.processors.ProcessPaymentMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GetShopIOTOperator extends GetShopIOTCommon {
    private String configFile = "getshopiotserverconfig.txt";

    private List<String> configsSent = new ArrayList();
            
    public GetShopNetsApp nets = null;
    
    private boolean isProductionMode = true;
    private String debugConnectionAddr ="http://www.3.0.local.getshop.com/";
    private String debugLongPullAddr ="http://lomcamping.3.0.local.getshop.com/";
    
    public void run() {
        logPrint("Ready to recieve instructions from " +getAddress() +  ", with token: " + getToken());
        setStartupConfigs();
        while(true) {
            doLongPull();
        }
    }
    
    private void doLongPull() {
        
        Gson gson = new Gson();
        try {
            String msg = readFromPullService();
            if(msg != null && !msg.isEmpty()) {
                List<GetShopDeviceMessage> result = new ArrayList();
                Type listType = new TypeToken<List<GetShopDeviceMessage>>(){}.getType();
                
                result = gson.fromJson(msg, listType);
                ArrayList result2 = gson.fromJson(msg, ArrayList.class);
                if(result!=null) {
                    int idx = 0;
                    for(GetShopDeviceMessage gsdmsg : result) {
                        Class classLoaded = this.getClass().getClassLoader().loadClass(gsdmsg.className);
                        Object res = gson.fromJson(gson.toJson(result2.get(idx)), classLoaded);
                        idx++;
                        processMessage(res);
                    }
                }
            }
        }catch(Exception e) {
            logPrintException(e);
            try { Thread.sleep(10000); }catch(Exception d) {}
        }
    }

    boolean setup() {
        String fileName = configFile;
        List<String> list = new ArrayList<>();

        Path path = Paths.get(fileName);
        
        try {
            if(!Files.exists(path)) {
                Files.createFile(path);
            }
            Stream<String> stream = Files.lines(path);
                //1. filter line 3
                //2. convert all content to upper case
                //3. convert it into a List
                list = stream
                                .filter(line -> !line.startsWith("line3"))
                                .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = "";
        for(String l : list) {
            result += l;
        }
        Gson gson = new Gson();
        SetupMessage msg = gson.fromJson(result, SetupMessage.class);
        this.setConfiguration(msg);
        writeConfig(msg);
        return (!getAddress().isEmpty() && !getToken().isEmpty());
    }

    void readConfiguration() {
        long identificator = (long)(Math.random() * 1000000000);
        logPrint("Im not identified yet, waiting for initial instruction from the GetShop hive, my identification is : " + identificator);
        int i = 0;
        logPrintWithoutLine(new Date()+ " : ");
        while(true) {
            logPrintWithoutLine(".");
            if(i % 10 == 0) {
                try {
                    String content = getReplyFromGetShop(identificator);
                    if(!content.isEmpty()) {
                        logPrintWithoutLine("\r\n");
                        Gson gson = new Gson(); 
                        SetupMessage msg = new SetupMessage();
                        msg = gson.fromJson(content, SetupMessage.class);
                        writeConfig(msg);
                        logPrint("Got response from the GetShop hive, instructions recieved.");
                        logPrint("   my token is   " + msg.token);
                        logPrint("   My address is " + msg.address);
                        break;
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
            i++;
            try { Thread.sleep(1000); }catch(Exception e) {
                
            }
        }
    }

    private String getReplyFromGetShop(long id) throws MalformedURLException, IOException {
        String addr = "https://system.getshop.com";
        if(!isProductionMode) {
            addr = debugConnectionAddr;
        }
        URL oracle = new URL(addr+"/scripts/iotserver.php?identification=" + id);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(oracle.openStream()));

        String result = "";
        String inputLine = "";
        while ((inputLine = in.readLine()) != null) {
            result += inputLine + "\r\n";
        }
        in.close();
        return result;
    }

    private void writeConfig(SetupMessage msg) {
        try {
            Path path = Paths.get(configFile);
            if(!Files.exists(path)) {
                Files.createFile(path);
            }
            Gson gson = new Gson();
            String config = gson.toJson(msg);
            Files.write(path, config.getBytes());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void setStartupConfigs() {
        sendConfig("supportDirectPrint", "true");
        SetupMessage msg = getSetupMessage();
        Field[] fields = msg.getClass().getDeclaredFields();
        for(Field f : fields) {
            try {
                sendConfig(f.getName(), f.get(msg).toString());
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private String readFromPullService() throws MalformedURLException, IOException {
        String addr = getAddress();
        if(!isProductionMode) {
            addr = debugLongPullAddr;
        }
        
        addr += "/scripts/longpullgsd.php";
        URL url = new URL(addr+"?token=" + getToken());
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(20000);
        
        BufferedReader in = new BufferedReader(
        new InputStreamReader(connection.getInputStream()));
        
        String result = "";
        String inputLine = "";
        while ((inputLine = in.readLine()) != null) {
            result += inputLine + "\r\n";
        }
        in.close();
        return result;
    }

    private void processMessage(Object res) {
        boolean isInstanceOfMessage = (res instanceof  GetShopDeviceMessage);
        
        if (res == null || !isInstanceOfMessage) {
            return;
        }
        
        List<MessageProcessorInterface> processors = new ArrayList();
        processors.add(new ProcessPrinterMessage());
        processors.add(new ProcessPaymentMessage());
        processors.add(new ProcessAccessDenied());
        
        processors.stream().forEach(o -> {
            
            try {
                o.setIOTOperator(this);
                o.processMessage((GetShopDeviceMessage)res);
            } catch (Exception ex) {
                logPrintException(ex);
            }
        });
    }

    private void sendConfig(String key, String value) {
        if (configsSent.contains(key))
            return;
        
        try {
            String addr = getAddress() + "/scripts/setGdsConfig.php?token=" + getToken() + "&key="+key+"&value="+value;
            URL url = new URL(addr);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(20000);
            
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            
            String result = "";
            String inputLine = "";
            while ((inputLine = in.readLine()) != null) {
                result += inputLine + "\r\n";
            }
            in.close();
            configsSent.add(key);
        } catch (IOException ex) {
            logPrintException(ex);
        }
    }

}
