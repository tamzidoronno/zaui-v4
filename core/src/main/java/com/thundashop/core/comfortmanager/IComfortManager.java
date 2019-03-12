/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.comfortmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;


/**
 *
 * @author pal
 */
@GetShopApi
public interface IComfortManager {
    
    @Administrator
    public void createState(String name);
    
    @Administrator
    public void deleteState(String stateId);
    
    @Administrator
    public ComfortState getState(String stateId);
    
    @Administrator
    public List<ComfortState> getAllStates();
    
    @Administrator
    public void saveState(ComfortState state); 
    
    @Administrator
    public void test();
    
    @Administrator
    public ComfortRoom getComfortRoom(String bookingItemId);
    
    @Administrator
    public void saveComfortRoom(ComfortRoom room);
    
    @Administrator
    public List<ComfortLog> getAllLogEntries();
}