/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.usermanager.UserManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class AcculogixVirtualRoutesImporter {
    private final String rawData;
    private final List<String[]> datas = new ArrayList();
    private final TrackAndTraceManager trackAndTraceManager;
    
    public AcculogixVirtualRoutesImporter(TrackAndTraceManager trackAndTraceManager, String data) {
        this.trackAndTraceManager = trackAndTraceManager;
        this.rawData = data;
        loadLines();
        importRoutes();
    }
    
    private void loadLines() {
        for (String line : rawData.split("\n")) {
            if (line.contains("RD$ID"))
                continue;
            
            String[] values = line.replaceAll("\r", "").split("\t", -1);
            datas.add(values);
        }
    }

    private void importRoutes() {
        
         SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        
        // This is set because it should automatically be deleted after two days
        
        for (String[] data : datas) {
            Route route = new Route();
            route.completedInfo.completedTimeStamp = new Date();
            route.id = data[0]+" "+data[4].replace("\"", "");
            route.name = data[0]+" - "+data[2].replace("\"", "")+ " "+data[4].replace("\"", "");
            route.depotId = data[1].replace("\"", "");
            route.userIds.add(data[3].replace("\n", ""));
            route.isVritual = false;
            route.originalId = data[0];
            
            try {
                route.deliveryServiceDate = sdf.parse(data[4].replace("\"", "").split(" ")[1]);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
            
            trackAndTraceManager.saveRoute(route);
        }
        
    }
    
}
