/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mekonomen;

import com.thundashop.mekonomen.MekonomenEvent;
import com.thundashop.mekonomen.MekonomenEventParticipant;
import com.thundashop.mekonomen.MekonomenUser;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ktonder
 */
public class MekonomenDatabase {
    public HashMap<String, MekonomenEvent> events = new HashMap();
    public List<MekonomenUser> users = new ArrayList();
    public List<MekonomenEventParticipant> participants = new ArrayList();
    
    public static MekonomenDatabase getDatabase() {
        MekonomenDatabase db = new MekonomenDatabase();
        db.load();
        return db;
    }

    private void load() {
        try {
            loadUsers();
            loadEvents();
            loadConnections();
        } catch (IOException ex) {
            Logger.getLogger(MekonomenDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(MekonomenDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        MekonomenDatabase db = MekonomenDatabase.getDatabase();
        List<MekonomenUser> users = db.searchForUser("Bo Svensson");
        
        for (MekonomenUser user : users) {
            System.out.println(user.firstName + " " + user.sureName);
            for (MekonomenEventParticipant participated : user.events) {
                System.out.println("Username: " + user.username);
                System.out.println("Group: " + user.groupName);
                System.out.println("Befattning: " + user.befattning);
                System.out.println("Name: " + participated.event != null ? participated.event.eventName : "N/A");
                System.out.println("Start: " + participated.startDate);
                System.out.println("End: " + participated.endDate);
            }
        }
    }

    private void loadUsers() throws FileNotFoundException, IOException {
        FileInputStream fileInputStream = new FileInputStream("/datafiles/promeister/users.xlsx");
        
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet worksheet = workbook.getSheet("Blad1");
        
        for (Row myrow : worksheet) {
            if (myrow == null || myrow.getCell(0) == null || myrow.getCell(0).getStringCellValue().equals("Förnamn"))
                continue;
            
            MekonomenUser user = new MekonomenUser();
            user.firstName = getStringValue(myrow, 0);
            user.sureName = getStringValue(myrow, 1);
            user.groupName = getStringValue(myrow, 2);
            user.befattning = getStringValue(myrow, 3);
            user.managerLogin = getStringValue(myrow, 4);
            user.username = getStringValue(myrow, 5);
            user.email = getStringValue(myrow, 6);
            
            users.add(user);
        }
                     
    }

    private String getStringValue(Row myrow, int i) {
        if (myrow.getCell(i) == null)
            return "";
        
        myrow.getCell(i).setCellType(Cell.CELL_TYPE_STRING);
        if (myrow.getCell(i) != null) {
            return myrow.getCell(i).getStringCellValue();
        }
        
        return "";
    }

    private void loadEvents() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("/datafiles/promeister/events.xlsx");
        
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet worksheet = workbook.getSheet("Blad1");
        
        int i  = 0;
        for (Row myrow : worksheet) {
            i++;
            
            if (myrow == null || myrow.getCell(0) == null || i == 1)
                continue;
            
            
            
            MekonomenEvent event = new MekonomenEvent();
            event.eventName = getStringValue(myrow, 1);
            event.nodeId = getStringValue(myrow, 0).trim().toLowerCase();
            
            events.put(event.nodeId, event);
        }
    }

    private void loadConnections() throws FileNotFoundException, IOException, ParseException {
        FileInputStream fileInputStream = new FileInputStream("/datafiles/promeister/users_events.xlsx");
        
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet worksheet = workbook.getSheet("Blad1");
        
        int i = 0;
        for (Row myrow : worksheet) {
            i++;
            
            if (i == 1)
                continue;
            
            
            MekonomenEventParticipant participant = new MekonomenEventParticipant();
            participant.username = getStringValue(myrow, 0);
            participant.nodeId = getStringValue(myrow, 1);
            participant.startDate = getDateValue(myrow, 3);
            participant.endDate = getDateValue(myrow, 4);
            participant.status = getStringValue(myrow, 5);
            
            participants.add(participant);
        }
    }

    private Date getDateValue(Row myrow, int i) throws ParseException {
        if (myrow.getCell(i) == null)
            return null;
        
        String text = myrow.getCell(i).getStringCellValue();
        if (text.equals("NULL"))
            return null;
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date date = format.parse(text);
        
        return date;
    }

    public List<MekonomenUser> searchForUser(String name) {
        List<MekonomenUser> foundUsers = users.stream().filter(o -> o.getFullName().toLowerCase().contains(name.toLowerCase()))
                .filter(distinctByKey(p -> p.username.trim().toLowerCase()))
                .collect(Collectors.toList());
        
        foundUsers.stream().forEach(o -> addEvents(o));
        
        return foundUsers;
    }
    
    public <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
    
    private void addEvents(MekonomenUser user) {
        participants.stream().filter(p -> p.username.trim().toLowerCase().equals(user.username.trim().toLowerCase()))
                .forEach(participant -> {
                    MekonomenEvent event = getEvent(participant.nodeId);
                    user.add(event, participant);
                });
    }
    
    private MekonomenEvent getEvent(String nodeId) {
        return events.get(nodeId);
        
    }
}
