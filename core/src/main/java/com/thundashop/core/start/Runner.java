package com.thundashop.core.start;

import com.google.gson.Gson;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.StorePool;
import com.thundashop.core.databasemanager.DatabaseUpdater;
import com.thundashop.core.socket.WebInterface2;
import com.thundashop.core.socket.WebSocketServerImpl;
import com.thundashop.core.socket.WebSocketServerImplSSL;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.util.UUID;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author k
 */
public class Runner {
    public static boolean AllowedToSaveToRemoteDatabase = false;
    public static String OVERALLPASSWORD = UUID.randomUUID().toString();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, Exception {
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");

        setUnirestMapper();

        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        setDeveloperMode(context);

        invokeApiGenerator();

        PrintWriter out = new PrintWriter("../secret.txt");
        out.write(OVERALLPASSWORD + "\n");
        out.close();

        if (GetShopLogHandler.isDeveloper) {
            DatabaseUpdater updater = context.getBean(DatabaseUpdater.class);
            boolean updated = updater.check(context);
            if (updated) {
                context = new ClassPathXmlApplicationContext("All.xml");
            }
        }

        AppContext.appContext = context;
        Logger log = context.getBean(Logger.class);

        StorePool storePool = new StorePool();
        AppContext.storePool = storePool;
        int port = 25554;
        if (args.length > 0) {
            AppContext.devMode = false;
            if (args[0].equals("dev")) {
                AppContext.devMode = true;
            }
        }

        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }

        new WebInterface2(log, storePool, port); //Version 2.        

        WebSocketServerImpl webSocketServer = context.getBean(WebSocketServerImpl.class);
        webSocketServer.start();
        
//        WebSocketServerImplSSL sslWebSocketServer = context.getBean(WebSocketServerImplSSL.class);
//        sslWebSocketServer.useSSL();
//        sslWebSocketServer.start();


        startJettyServer();
    }

    private static void setDeveloperMode(ApplicationContext context) throws BeansException {
        FrameworkConfig frameWorkConfig = context.getBean(FrameworkConfig.class);
        GetShopLogHandler.isDeveloper = !frameWorkConfig.productionMode;
    }

    private static void startJettyServer() throws InterruptedException, Exception {

        Server server = new Server(8080);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{"index.html"});
        resource_handler.setResourceBase("html");
        server.setHandler(resource_handler);

        server.start();
        server.join();

    }

    private static void invokeApiGenerator() {
        try {
            if (java.lang.System.getProperty("gs_apigenerator") != null) {
                Class cls = Class.forName(java.lang.System.getProperty("gs_apigenerator"));
                System.out.println(cls);
                Method method = cls.getMethod("main", String[].class);
                String[] params = null; // init params accordingly
                method.invoke(null, (Object) params);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void setUnirestMapper() {
        Unirest.setObjectMapper(new ObjectMapper() {
            Gson gson = new Gson();

            public <T> T readValue(String value, Class<T> valueType) {
                return gson.fromJson(value, valueType);
            }

            public String writeValue(Object value) {
                return gson.toJson(value);
            }
        });

    }
}
