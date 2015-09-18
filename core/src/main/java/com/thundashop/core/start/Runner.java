package com.thundashop.core.start;


import com.thundashop.core.apigenerator.GenerateApiLocal;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.StorePool;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.socket.WebInterface2;
import com.thundashop.core.socket.WebSocketServerImpl;
import java.io.PrintWriter;
import java.util.UUID;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author k
 */
public class Runner {
    public static String OVERALLPASSWORD = UUID.randomUUID().toString();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, Exception {  
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");

        PrintWriter out = new PrintWriter("../secret.txt") ;
        out.write(OVERALLPASSWORD+"\n");
        out.close();

        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        AppContext.appContext = context;
        Logger log = context.getBean(Logger.class);
        
        StorePool storePool = new StorePool();
        AppContext.storePool = storePool;
        int port = 25554;
        if(args.length > 0) {
            AppContext.devMode = false;
            if(args[0].equals("dev")) {
                AppContext.devMode = true;
            }
        }
        
        if(args.length > 1) {
            port = Integer.parseInt(args[1]);
            context.getBean(Database.class).activateSandBox();
        }

        new WebInterface2(log, storePool, port); //Version 2.        
        
        context.getBean(WebSocketServerImpl.class).start();
        startJettyServer();
    }

    private static void startJettyServer() throws InterruptedException, Exception {
      
        Server server = new Server(8080);
 
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
        resource_handler.setResourceBase("html");
        server.setHandler(resource_handler);
 
        server.start();
        server.join();

    }
}