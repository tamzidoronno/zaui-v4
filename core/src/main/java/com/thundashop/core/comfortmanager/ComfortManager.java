/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.comfortmanager;

import com.getshop.scope.GetShopSession;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author pal
 */
@Component
@GetShopSession
public class ComfortManager extends ManagerBase implements IComfortManager {
    HashMap<String, ComfortState> states = new HashMap();
    HashMap<String, ComfortRoom> comfortRooms = new HashMap();
    List<ComfortLog> logEntries = new ArrayList();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon com : data.data) {
            if(com instanceof ComfortState) {
                states.put(com.id, (ComfortState) com);
            }
            if(com instanceof ComfortRoom) {
                comfortRooms.put(com.id, (ComfortRoom) com);
            }
            if(com instanceof ComfortLog) {
                logEntries.add((ComfortLog) com);
            }
        }
    }

    @Override
    public void createState(String name) {
        ComfortState state = new ComfortState();
        state.name = name;
        saveState(state);
    }

    public void pmsEvent(String event, String roomId) {
        clearOldLogEntries();
        ComfortLog logentry = new ComfortLog();
        logentry.pmsEvent = event;
        logentry.roomId = roomId;
        logentry.text = "";
        
        ComfortRoom room = getComfortRoom(logentry.roomId);
        ComfortState state = getStateByEvent(logentry.pmsEvent);
        if(room != null && state != null) {
            logentry.state = state;
            signalGetShopUnitsAboutChange(state, room);
        }
        
        saveObject(logentry);
        logEntries.add(logentry);
    }
    
    @Override
    public void deleteState(String stateId) {
        ComfortState state = getState(stateId);
        states.remove(stateId);
        deleteObject(state);
    }

    @Override
    public ComfortState getState(String stateId) {
        return states.get(stateId);
    }

    @Override
    public List<ComfortState> getAllStates() {
        return new ArrayList(states.values());
    }

    @Override
    public void saveState(ComfortState state) {
        saveObject(state);
        states.put(state.id, state);
    }

    @Override
    public void test() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ComfortRoom getComfortRoom(String bookingItemId) {
        for(ComfortRoom r : comfortRooms.values()) {
            if(r.bookingItemId.equals(bookingItemId)) {
                return r;
            }
        }
        ComfortRoom room = new ComfortRoom();
        room.bookingItemId = bookingItemId;
        return room;
    }

    @Override
    public void saveComfortRoom(ComfortRoom room) {
        saveObject(room);
        comfortRooms.put(room.id, room);
    }

    private void clearOldLogEntries() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -3);
        Date old = cal.getTime();
        List<ComfortLog> entiresToRemove = new ArrayList();
        for(ComfortLog logEntry : logEntries) {
            if(logEntry.rowCreatedDate.before(old)) {
                entiresToRemove.add(logEntry);
                deleteObject(logEntry);
            }
        }
        logEntries.removeAll(entiresToRemove);
    }

    @Override
    public List<ComfortLog> getAllLogEntries() {
        Collections.sort(logEntries, new Comparator<ComfortLog>() {
        public int compare(ComfortLog o1, ComfortLog o2) {
            return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
        }
      });
        
        return logEntries;
    }

    private ComfortState getStateByEvent(String pmsEvent) {
        for(ComfortState state : getAllStates()) {
            if(state.event.equals(pmsEvent)) {
                return state;
            }
        }
        return null;
    }

    private void signalGetShopUnitsAboutChange(ComfortState state, ComfortRoom room) {
        //This is where the magic happends.
        
        /**
         * What is left to do:
         * 1. Create a unit system for fetching all units like radiators etc (should be done in the gdsmanager)
         * 2. send signals using the gds manager to adjust temperature to begin with.
         */
        
        
//        for(String getshopUnitId : room.getshopDeviceUnitsConnected) {
//            sendSignalToUnitAboutTemperatureChange(state.temperature);
//        }
    }

    

}