package com.thundashop.core.start;


import com.thundashop.core.socket.WebInterface2;
import com.thundashop.core.common.StorePool;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.socket.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author k
 */
public class Runner {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, Exception {  
        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        AppContext.appContext = context;
        
        
        
        StorePool storePool = new StorePool();
        AppContext.storePool = storePool;
        int port = 25554;
        if(args.length > 0) {
            port = Integer.parseInt(args[0]);
            context.getBean(Database.class).activateSandBox();
        }
        
        new WebInterface2(storePool, port); //Version 2.
        CacheManager cache = new CacheManager();
        cache.start();
        
        AppContext.cacheManager = cache;
    }
}