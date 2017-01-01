/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printmanager;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.printmanager.PrintJob;
import com.thundashop.core.usermanager.data.User;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class PrintClient {

    private String printerId = "utsikten_hotell_printer_1";

    private String serverAddress = "utsiktenhotell.getshop.com";
    private String sessionId = UUID.randomUUID().toString();

    private int backendPortLocal = 25554;
    private int backendPortOnline = 4224;
    
    public PrintClient() throws Exception {
        GetShopApi api = new GetShopApi(backendPortOnline, serverAddress, sessionId, serverAddress);
        User user = api.getUserManager().logOn("post@getshop.com", "gakkgakk");

        while (true) {
            List<PrintJob> printerList = api.getPrintManager().getPrintJobs(printerId);
            handlePrintJobs(printerList);
            Thread.sleep(250);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        new PrintClient();
    }

    private void handlePrintJobs(List<PrintJob> jobList) throws IOException {
        for (PrintJob job : jobList) {
            byte[] decoded = Base64.getDecoder().decode(job.content);
            String fileName = "/tmp/" + UUID.randomUUID().toString();
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(decoded);
            fos.close();
            print(fileName);
        }
    }

    private void print(String fileName) {
        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec("lpr " + fileName );
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
