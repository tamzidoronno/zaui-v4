
package com.thundashop.core.common;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetShopLogHandler {
    public static boolean isDeveloper = false;
    
    public static List<String> started = new ArrayList();
    public static List<String> authenticationError = new ArrayList();
    
    public static void logPrintStatic(Object key, String storeId) {
        if (key == null)
            return;
        
        if (isDeveloper) {
            System.out.println(key);
            return;
        }
        
        try {
            createLogPath();
            String storeIdToUse = storeId == null || storeId.isEmpty() ? "all" : storeId;           
            String fileName = "log/"+storeIdToUse+".log";
            
            File logFileName = new File(fileName);
            if (!logFileName.exists()) {
                logFileName.createNewFile(); 
            }

            String textToLog = new Date().toString() + "\t" + key + "\n";
            Files.write(Paths.get(fileName), textToLog.getBytes(), StandardOpenOption.APPEND);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Cant log... check log access etc");
        }
        
    }

    private static void createLogPath() throws IOException {
        Path logPath = Paths.get("log");
        if (Files.notExists(logPath)) {
            Files.createDirectories(logPath);
        }
    }

    public static void logPrintStaticSingle(Object key, String storeId) {
        logPrintStatic(key, storeId);
    }

    public static void logStack(Exception ex, String storeId) {
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        String stack = errors.toString();
        logPrintStatic(stack, storeId);
    }

 

}
