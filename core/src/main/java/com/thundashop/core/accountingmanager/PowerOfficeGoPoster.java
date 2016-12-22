package com.thundashop.core.accountingmanager;

import com.google.gson.Gson;
import com.powerofficego.data.CanBePostedResponse;
import com.powerofficego.data.PostObject;
import com.thundashop.core.webmanager.WebManager;

public class PowerOfficeGoPoster extends Thread {

    private final String transferId;
    private final String token;
    
    PowerOfficeGoPoster(String transferId, String token) {
        this.transferId = transferId;
        this.token = token;
    }
    
    public void run() {
        WebManager webMgr = new WebManager();
        for(int i = 0; i < 300; i++) {
        try { 
                String address = "http://api.poweroffice.net/import/"+transferId+"/status";
                System.out.println(address);
                String result = webMgr.htmlPostBasicAuth(address, "", false, "ISO-8859-1", token, "Bearer", false, "GET");
                Gson gson = new Gson();
                CanBePostedResponse resp = gson.fromJson(result, CanBePostedResponse.class);
                if(resp.data.canBePosted) {
                    PostObject obj = new PostObject();
                    obj.id = transferId;
                    String postedResult = webMgr.htmlPostBasicAuth("http://api.poweroffice.net/import/post/", gson.toJson(obj), true, "ISO-8859-1", token, "Bearer", false, "GET");
                    System.out.println(postedResult);
                    return;
                }
                System.out.println(i + " : " + result);
                Thread.sleep(10000); 
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
