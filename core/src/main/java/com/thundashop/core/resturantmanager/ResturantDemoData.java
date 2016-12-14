/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class ResturantDemoData  {
    
    Map<String, ResturantTable> tables = new HashMap();
    Map<String, ResturantRoom> rooms = new HashMap();
    
    public ResturantDemoData() {
        createRooms();
        createTables();
    }

    private void createRooms() {
        ResturantRoom room1 = new ResturantRoom();
        room1.name = "Room 1";
        room1.id = "room1";
        rooms.put(room1.id, room1);
        
        ResturantRoom room2 = new ResturantRoom();
        room2.name = "Room 2";
        room2.id = "room2";
        rooms.put(room2.id, room2);
    }
    
    public Map<String, ResturantTable> getTables() {
        return tables;
    }
    
    public Map<String, ResturantRoom> getRooms() {
        return rooms;
    }

    private void createTables() {
        ResturantTable table1_1 = createTable("Table 1", "table1");
        ResturantTable table1_2 = createTable("Table 2", "table2");
        ResturantTable table1_3 = createTable("Table 3", "table3");
        
        ResturantTable table2_1 = createTable("Table 1", "table4");
        ResturantTable table2_2 = createTable("Table 2", "table5");
        ResturantTable table2_3 = createTable("Table 3", "table6");
        
        rooms.get("room1").tableIds.add("table1");
        rooms.get("room1").tableIds.add("table2");
        rooms.get("room1").tableIds.add("table3");
        
        rooms.get("room2").tableIds.add("table4");
        rooms.get("room2").tableIds.add("table5");
        rooms.get("room2").tableIds.add("table6");
    }

    private ResturantTable createTable(String name, String id) {
        ResturantTable table = new ResturantTable();
        table.name = name;
        table.id = id;
        
        tables.put(table.id, table);
        
        return table;
    }
}