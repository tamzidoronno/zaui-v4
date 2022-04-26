/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.utils.NullSafeConcurrentHashMap;
import org.joda.time.DateTime;
import org.junit.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * @author Naim Murad
 * @since  25-Apr-2022
 */
public class ConcurrentMapTest {

    public ConcurrentMapTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    private ConcurrentHashMap<String, String> loadMap() {
        // Creating ConcurrentHashMap
        ConcurrentHashMap<String, String> mp = new ConcurrentHashMap<>();
        // using put() method
        mp.put("1", "1");
        mp.put("2", "2");
        mp.put("3", "3");
        return mp;
    }

    private NullSafeConcurrentHashMap<String, String> loadNullSafeMap() {
        // Creating ConcurrentHashMap
        NullSafeConcurrentHashMap<String, String> mp = new NullSafeConcurrentHashMap<>();
        // using put() method
        mp.put("1", "1");
        mp.put("2", "2");
        mp.put("3", "3");
        return mp;
    }

    @Test
    public void testGetByExistedKey() {
        // Creating ConcurrentHashMap
        ConcurrentHashMap<String, String> map = loadMap();
        // using put() method
        String val = map.get("2");
        System.out.println("Value is: " + val);
        assertEquals(val,"2");

        NullSafeConcurrentHashMap<String, String> nMap = loadNullSafeMap();
        val = nMap.get("2");
        System.out.println("Value is: " + val);
        assertEquals(val,"2");
    }

    @Test
    public void testGetByNonExistKey() {
        // Creating ConcurrentHashMap
        ConcurrentHashMap<String, String> mp = loadMap();
        // using put() method
        String val = mp.get("999");
        System.out.println("Value is: " + val);
        assertNull(val);

        NullSafeConcurrentHashMap<String, String> nMap = loadNullSafeMap();
        val = nMap.get("999");
        System.out.println("Value is: " + val);
        assertNull(val);
    }

    @Test
    public void testPutNullKey() {
        // Creating ConcurrentHashMap
        ConcurrentHashMap<String, String> mp = loadMap();
        // using put() method
        String val = mp.get("999");
        System.out.println("Value is: " + val);
        try {
            mp.put(null, "Null value");
        } catch(Exception e) {
            System.out.println("Can not set null key: ");
            e.printStackTrace();
        }

        // Null Safe Concurrent hashmap can put null
        NullSafeConcurrentHashMap<String, String> nmap = loadNullSafeMap();
        nmap.put(null, "Null value");
        System.out.println("Can put null key");
    }

    @Test
    public void testPutNullValue() {
        // Creating ConcurrentHashMap
        ConcurrentHashMap<String, String> mp = loadMap();
        // using put() method
        String val = mp.get("999");
        System.out.println("Value is: " + val);
        try {
            mp.put("777", null);
        } catch(Exception e) {
            System.out.println("Can not set null value: ");
            e.printStackTrace();
        }

        // Null Safe Concurrent hashmap can put null
        NullSafeConcurrentHashMap<String, String> nmap = loadNullSafeMap();
        nmap.put("777", null);
        System.out.println("Can put null value");
        String v2 = nmap.get("777");
        assertNull(v2);
    }

    @Test
    public void testRemoveNullValue() {
        // Creating ConcurrentHashMap
        ConcurrentHashMap<String, String> mp = loadMap();
        // using put() method
        String val = mp.get("999");
        System.out.println("Value is: " + val);
        try {
            mp.remove(null);
        } catch(Exception e) {
            System.out.println("Can not remove null key: ");
            e.printStackTrace();
        }

        // Null Safe Concurrent hashmap can put null
        NullSafeConcurrentHashMap<String, String> nmap = loadNullSafeMap();
        nmap.remove(null);
        System.out.println("Can remove null value");
    }

    @Test
    public void testRemoveNonExistedValue() {
        // Creating ConcurrentHashMap
        ConcurrentHashMap<String, String> mp = loadMap();
        // using put() method
        String val = mp.get("999");
        System.out.println("Value is: " + val);
        try {
            String v = mp.remove("null");
            System.out.println("Removed : " + v);
        } catch(Exception e) {
            System.out.println("Can not remove null key: ");
            e.printStackTrace();
        }
    }
}
