/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.arx;

import com.thundashop.core.arx.Door;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 * communication with the arx server.
 * 
 * @author boggi
 */

@GetShopApi
interface IArxManager {
    public boolean logonToArx(String hostname, String username, String password);
    public List<Door> getAllDoors() throws Exception;
    public List<Person> getAllPersons() throws Exception;
    public boolean isLoggedOn();
    public List<AccessCategory> getAllAccessCategories() throws Exception;
    public void doorAction(String externalId, String state) throws Exception;
    public List<AccessLog> getLogForDoor(String externalId, long start, long end) throws Exception;
    public Person updatePerson(Person person) throws Exception;
    public Person getPerson(String id) throws Exception;
    public Person addCard(String personId, Card card) throws Exception;
}
