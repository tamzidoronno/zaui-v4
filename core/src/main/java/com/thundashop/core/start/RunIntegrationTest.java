package com.thundashop.core.start;

import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 *
 * @author boggi
 */
public class RunIntegrationTest {
    private static BackendRunner starter;

    public static void main(String[] args) {
        RunIntegrationTest test = new RunIntegrationTest();
        starter = test.startJava();
        File jarFile = new File("dist/com.thundashop.core.jar");
        long lastBuild = jarFile.lastModified();
        
        GetShopLogHandler.logPrintStatic("Starting!", null);
        boolean doLoop = false;
        do {
            jarFile = new File("dist/com.thundashop.core.jar");
            if (lastBuild < jarFile.lastModified()) {
                GetShopLogHandler.logPrintStatic("Restarting java backend", null);
                starter.exit();
                do {
                    starter = test.startJava();
                } while (starter == null);
            }
            test.runPHPTests();
            
            if(doLoop)
                try { Thread.sleep(2000);} catch (Exception e) {}
            
        }while(doLoop);
        starter.exit();    
    }

    protected void finalize() throws Throwable {
        super.finalize(); //not necessary if extending Object.
    }

    private BackendRunner startJava() {
        BackendRunner starter = new BackendRunner();
        starter.start();

        for (int i = 0; i < 100; i++) {
            if (starter.started) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }

        if (!starter.started) {
            GetShopLogHandler.logPrintStatic("Failed to start the java backend.!", null);
            return null;
        }
        GetShopLogHandler.logPrintStatic("Java backend started", null);
        return starter;
    }

    private void runPHPTests() {

        //Run the php tests.
        try {
            ProcessBuilder procBuilder = new ProcessBuilder();
            procBuilder.directory(new File("../com.getshop.client/integrationtest"));
            procBuilder.command(new String[]{"/usr/bin/php", "-n", "/test/thundashop/com.getshop.client/integrationtest/runner.php", "alttest"});
            Process proc = procBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                GetShopLogHandler.logPrintStatic(line, null);
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class BackendRunner extends Thread {

        public boolean started = false;
        private Process p;

        public BackendRunner() {
        }

        public void run() {
            try {
                p = Runtime.getRuntime().exec(new String[]{"/usr/bin/java", "-jar", "dist/com.thundashop.core.jar", "20000"});
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                while (line != null) {
                    line = reader.readLine();
                    if (line.contains("Listening to port")) {
                        started = true;
                    }
                }
            } catch (Exception e) {
                //It just fails when killing this off.
                //e.printStackTrace();
            }
        }

        void exit() {
            p.destroy();
        }
    }
}
