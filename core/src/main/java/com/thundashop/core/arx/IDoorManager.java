/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.arx;

import com.thundashop.core.arx.Door;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.getshop.data.GetShopLockCode;
import com.thundashop.core.getshop.data.GetShopLockMasterCodes;
import java.util.HashMap;
import java.util.List;

/**
 * communication with the arx server.
 * 
 * @author boggi
 */

@GetShopApi
@GetShopMultiLayerSession
interface IDoorManager {
    @Administrator
    public List<Door> getAllDoors() throws Exception;
    @Administrator
    public List<Person> getAllPersons() throws Exception;
    @Administrator
    public List<AccessCategory> getAllAccessCategories() throws Exception;
    @Administrator
    public void doorAction(String externalId, String state) throws Exception;

    public String pmsDoorAction(String code, String type) throws Exception;
    @Administrator
    public List<AccessLog> getLogForDoor(String externalId, long start, long end) throws Exception;
    @Administrator
    public HashMap<String, List<AccessLog>> getLogForAllDoor(long start, long end) throws Exception;
    @Administrator
    public Person updatePerson(Person person) throws Exception;
    @Administrator
    public Person getPerson(String id) throws Exception;
    @Administrator
    public Person addCard(String personId, Card card) throws Exception;
    @Administrator
    public void clearDoorCache() throws Exception;
    @Administrator
    public void closeAllForTheDay() throws Exception;
    @Administrator
    public HashMap<String, List<AccessLog>> generateDoorLogForAllDoorsFromResult(String resultFromArx) throws Exception;
    
    @Administrator
    public GetShopLockMasterCodes getMasterCodes();
    
    @Administrator
    public void saveMasterCodes(GetShopLockMasterCodes masterCodes);
}
