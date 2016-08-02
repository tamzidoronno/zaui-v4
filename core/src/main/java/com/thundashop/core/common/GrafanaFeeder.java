/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

/**
 *
 * @author boggi
 */
public class GrafanaFeeder extends Thread {
    public String dbName;
    public String point;
    public HashMap<String, Object> values;
    
    public void run() {
        InfluxDB influxDB = InfluxDBFactory.connect("http://10.0.3.95:8086", "root", "root");
        influxDB.createDatabase(dbName);

        Point.Builder pointToAdd = Point.measurement(point)
                            .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        
        for(String key : values.keySet()) {
            Object res = values.get(key);
            if(res instanceof Boolean) {
                pointToAdd.addField(key, (Boolean) values.get(key));
            }
            if(res instanceof Number) {
                pointToAdd.addField(key, (Number) values.get(key));
            }
            if(res instanceof Long) {
                pointToAdd.addField(key, (Long) values.get(key));
            }
            if(res instanceof String) {
                pointToAdd.addField(key, (String) values.get(key));
            }
            if(res instanceof Double) {
                pointToAdd.addField(key, (Double) values.get(key));
            }
        }
        
        Point buildedPoint = pointToAdd.build();
        influxDB.write(dbName, "default", buildedPoint);
        
    }
}
