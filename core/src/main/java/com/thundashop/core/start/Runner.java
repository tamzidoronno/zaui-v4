package com.thundashop.core.start;

import com.getshop.scope.GetShopSessionScopeCleaner;
import com.google.gson.Gson;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.DatabaseRemoteCache;
import com.thundashop.core.databasemanager.DatabaseUpdater;
import com.thundashop.core.socket.WebInterface2;
import com.thundashop.core.socket.WebSocketServerImpl;
import com.thundashop.services.config.FrameworkConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 *
 * @author k
 */
public class Runner {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Runner.class);

    public static boolean AllowedToSaveToRemoteDatabase = false;
    public static String OVERALLPASSWORD = UUID.randomUUID().toString();

    public static DatabaseRemoteCache cached = new DatabaseRemoteCache();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, Exception {

        log.info("Starting application...");

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

        try {
            WebSocketServerImpl webSocketServer = context.getBean(WebSocketServerImpl.class);
            webSocketServer.start();
        } catch (BeansException e) {
            e.printStackTrace();
        }

//        WebSocketServerImplSSL sslWebSocketServer = context.getBean(WebSocketServerImplSSL.class);
//        sslWebSocketServer.useSSL();
//        sslWebSocketServer.start();


        Thread cleaner = new Thread(new GetShopSessionScopeCleaner());
        cleaner.start();
        
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
                log.debug("invokeApiGenerator class `{}`", cls);
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
