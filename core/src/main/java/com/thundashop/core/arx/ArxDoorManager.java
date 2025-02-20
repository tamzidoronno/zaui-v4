/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.arx;

import com.assaabloy.arxdata.persons.AccessCategoryList;
import com.assaabloy.arxdata.persons.AccessCategoryType;
import com.assaabloy.arxdata.persons.Arxdata;
import com.assaabloy.arxdata.persons.PersonListType;
import com.assaabloy.arxdata.persons.PersonType;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.getshop.data.GetShopLockMasterCodes;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingFilter;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

/**
 *
 * @author boggi
 */
public class ArxDoorManager implements IDoorManager {
    StoreManager storeManager;
    PmsManager pmsManager;
    DoorManager doorManager;
    BookingEngine bookingEngine;
    private boolean doneClosedForToday;
    private Date lastClosed;
    
    public void setManager(StoreManager store, PmsManager pms, DoorManager door, BookingEngine be) {
        storeManager = store;
        pmsManager = pms;
        doorManager = door;
        bookingEngine = be;
    }
    
    public String httpLoginRequest(String address, String content) throws Exception {
        String username = pmsManager.getConfigurationSecure().getDefaultLockServer().arxUsername;
        String arxHost = pmsManager.getConfigurationSecure().getDefaultLockServer().arxHostname;
        String password = pmsManager.getConfigurationSecure().getDefaultLockServer().arxPassword;
        
        if(!address.startsWith("http")) {
            address = "https://" + arxHost + address;
        }
        
        ArxConnection connection = new ArxConnection();
        if(!storeManager.isProductMode()) {
            GetShopLogHandler.logPrintStatic("Executing:" + address, null);
            return "";
        }
        
        if(arxHost.contains(":")) {
            address = address.replace(":5002", "");
        }
//        GetShopLogHandler.logPrintStatic("Executing:" + address, null);
//        GetShopLogHandler.logPrintStatic("Username:" + username, null);
//        GetShopLogHandler.logPrintStatic("Password:" + password, null);
        return connection.httpLoginRequest(address, username, password, content, pmsManager.getStoreId());
    }
    
    @Override
    public List<Door> getAllDoors() throws Exception {
        String hostName = ":5002/arx/export_accessarea";
        
        String result = httpLoginRequest(hostName, "");
        
        if(result.isEmpty()) {
            return new ArrayList();
        }
        
        InputStream is = new ByteArrayInputStream( result.getBytes() );
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        List<Door> doors = new ArrayList();
        try {
            Document document = builder.parse(is);
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            doors = recursiveFindDoors(nodeList, 0);
        }catch(SAXParseException e) {
//            if (pmsManager != null) {
//                GetShopLogHandler.logStack(e, pmsManager.getStoreId());
//            } else {
//                GetShopLogHandler.logStack(e, null);
//            }
        } finally {
            is.close();
        }
        
        return doors;
    }

    
    @Override
    public List<Person> getAllPersons() throws Exception {
        User currentUser = doorManager.getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/export?include_accesscategory_id=true&exclude_deleted=true";
        
        String result = httpLoginRequest(hostName, "");
        InputStream is = new ByteArrayInputStream( result.getBytes() );
        
        JAXBContext context = JAXBContext.newInstance("com.assaabloy.arxdata.persons");
        
        Unmarshaller unmarsh = context.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        //specify the schema definition for parsing
        Schema schema = sf.newSchema(new File("integration.xsd"));
        unmarsh.setSchema(schema);
        //unmarshall the xml file
        
        try {
            Arxdata obj = (Arxdata) unmarsh.unmarshal(is);
            PersonListType persons = obj.getPersons();
       
            List<Person> personlist = new ArrayList();
            List<Card> cards = createCardList(result);

            for(PersonType person : persons.getPerson()) {
                Person tmpPerson = createPerson(person);
                tmpPerson.cards.addAll(filterCards(cards, tmpPerson));
                personlist.add(tmpPerson);
            }
            return personlist;
        } finally {
            is.close();
        }
        
    }

    @Override
    public List<AccessCategory> getAllAccessCategories() throws Exception {
        User currentUser = doorManager.getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/export_accesscategory";
        
        String result = httpLoginRequest(hostName, "");
        InputStream is = new ByteArrayInputStream( result.getBytes() );
        
        JAXBContext context = JAXBContext.newInstance("com.assaabloy.arxdata.access");
        Unmarshaller unmarsh = context.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File("/home/boggi/accessschema.xml"));
        unmarsh.setSchema(schema);
        //unmarshall the xml file
        AccessCategoryList obj;
        try {
            JAXBElement resultret = (JAXBElement) unmarsh.unmarshal(is);
            com.assaabloy.arxdata.access.AccessCategoryList types = (com.assaabloy.arxdata.access.AccessCategoryList) resultret.getValue();
            List<AccessCategory> returnresult = new ArrayList();
            for(com.assaabloy.arxdata.access.AccessCategoryType categories : types.getAccessCategory()) {
                AccessCategory cat = new AccessCategory();
                cat.accessId = categories.getId();
                cat.name = categories.getName();
                cat.description = categories.getDescription();
                returnresult.add(cat);
            }

            return returnresult;
        } finally {
            is.close();
        }
    }

    @Override
    public void doorAction(String externalId, String state) throws Exception {
        String hostName = ":5002/arx/door_actions?externalid="+externalId+"&type=";
        Door door = getDoor(externalId);
        if(state.equals("forceOpenOn")) {
            hostName += "forceOpen&value=on";
            door.forcedOpen = true;
        } else if(state.equals("forceOpenOff")) {
            hostName += "forceOpen&value=off";
            door.forcedOpen = false;
        } else if(state.equals("forceOpen")) {
            if(door.forcedOpen) {
                hostName += "forceOpen&value=off";
                door.forcedOpen = false;
            } else {
                hostName += "forceOpen&value=on";
                door.forcedOpen = true;
            }
        } else if(state.equals("forceClose")) {
            if(door.forcedClose) {
                hostName += "forceClose&value=off";
                door.forcedClose = false;
            } else {
                hostName += "forceClosev&alue=on";
                door.forcedClose = true;
            }
        } else {
            hostName += "pulseOpen";
        }
        
        if (pmsManager != null) {
            GetShopLogHandler.logPrintStatic(hostName, pmsManager.getStoreId());
        } else {
            GetShopLogHandler.logPrintStatic(hostName, null);
        }
        
        httpLoginRequest(hostName,"");
    }

    @Override
    public String pmsDoorAction(String code, String type) throws Exception {
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.filterType = PmsBookingFilter.PmsBookingFilterTypes.active;
        
        
        //This is a temp solution for bydel alna.. if it works... rewrite it.
        if(code.equals("432511")) {
            try {
                if(type.equalsIgnoreCase("open")) {
                    doorAction("Lindeberglokalet", "forceOpenOn");
                } else {
                    doorAction("Lindeberglokalet", "forceOpenOff");
                }
                return "success";
            }catch(Exception e) {
                e.printStackTrace();
                return "Unable to connect to server";
            }
        }
        
        //This is another solution for haugerud idrettsforening, if it alos works... rewrite it :P
        if(code.equals("543211")) {
            try {
                if(type.equalsIgnoreCase("open")) {
                    doorAction("H1001_HIF_1", "forceOpenOn");
                } else {
                    doorAction("H1001_HIF_1", "forceOpenOff");
                }
                return "success";
            }catch(Exception e) {
                e.printStackTrace();
                return "Unable to connect to server";
            }
        }
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        
        filter.startDate = cal.getTime();
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        filter.endDate = cal.getTime();
        
        List<PmsBooking> bookings = pmsManager.getAllBookings(filter);
        boolean codeFound = false;
        for(PmsBooking booking : bookings) {
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                if(room.code.equals(code)) {
                    codeFound = true;
                    BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
                    pmsManager.logEntry("Running action " + type + " on door", booking.id, room.bookingItemId);
                    try {
                        if(type.equalsIgnoreCase("open")) {
                            if(room.isStarted() && !room.isEnded()) {
                                doorActionForItem(item, "forceOpenOn");
                            } else {
                                return "You booking has expired";
                            }
                        } else {
                            doorActionForItem(item, "forceOpenOff");
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                        return "Unable to connect to server";
                    }
                }
            }
        }
        
        if(!codeFound) {
            return "Invalid code";
        }
        
        return "Success";
    }
    
    @Override
    public List<AccessLog> getLogForDoor(String externalId, long start, long end) throws Exception {
        String result = getDoorLog(start, end);
        if(result == null || result.isEmpty()) {
            return new ArrayList();
        }
        return generateDoorAccessLogFromResult(result, externalId);
    }

    @Override
    public HashMap<String, List<AccessLog>> getLogForAllDoor(long start, long end) throws Exception {
        String result = getDoorLog(start, end);
        return generateDoorLogForAllDoorsFromResult(result);
    }

    @Override
    public Person updatePerson(Person person) throws Exception {

        if(person.id == null) {
            person.id = UUID.randomUUID().toString();
        }
        
        String toPost = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
        String firstName = convertToIso(person.firstName);
        String lastName = convertToIso(person.lastName);
        
        toPost += "<arxdata timestamp=\"" + new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss").format(new Date()) + "\">\n";
        toPost += "<persons>\n";
        toPost += "<person>\n";
        toPost += "<id>" + person.id + "</id>\n";
        
        toPost += "<first_name>" + firstName + "</first_name>\n";
        toPost += "<last_name>" + lastName + "</last_name>\n";
        toPost += "<description></description>\n";
        toPost += "<pin_code></pin_code>\n";
        if(person.deleted) {
            toPost += "<deleted>true</deleted>\n";
        }
        toPost += "<extra_fields/>\n";
        toPost += "<access_categories>\n";
        
        
        for (AccessCategory category : person.accessCategories) {
            toPost += "<access_category>\n";
            toPost += "<name>" + category.name + "</name>\n";
            if(category.startDate != null) {
                toPost += "<start_date>" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(category.startDate) + "</start_date>\n";
            }
            if(category.endDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(category.endDate);
                cal.add(Calendar.MINUTE, 20);
                
                toPost += "<end_date>" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime()) + "</end_date>\n";
            }
            toPost += "</access_category>\n";
        }
        toPost += "</access_categories>\n";
        toPost += "</person>\n";
        toPost += "</persons>\n";
        toPost += "<cards>\n";
        for(Card card : person.cards) {
            toPost += "<card>\n";
            toPost += "<number>00" + card.cardid + "</number>\n";
            toPost += "<format_name>" + card.format + "</format_name>\n";
            toPost += "<description></description>\n";
            toPost += "<person_id>" + person.id + "</person_id>\n";
            toPost += "</card>\n";
        }
        toPost += "</cards>\n";
        toPost += "</arxdata>\n";
        
        String hostName = ":5002/arx/import";
        String response = httpLoginRequest(hostName,toPost);
        return person;
    }

    @Override
    public Person getPerson(String id) throws Exception {
    User currentUser = doorManager.getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/export?external_id=" + id + "&exclude_deleted=1";
        
        String result = httpLoginRequest(hostName,"");
        InputStream is = new ByteArrayInputStream( result.getBytes() );
        
        JAXBContext context = JAXBContext.newInstance("com.assaabloy.arxdata.persons");
        
        Unmarshaller unmarsh = context.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        //specify the schema definition for parsing
        Schema schema = sf.newSchema(new File("integration.xsd"));
        unmarsh.setSchema(schema);

        //unmarshall the xml file
        Arxdata obj;
        try {
            obj = (Arxdata) unmarsh.unmarshal(is);
        } finally {
            is.close();
        }
        
        Person person = createPerson(obj.getPersons().getPerson().get(0));
        person.cards.addAll(createCardList(result));
        
        return person;
    }

    @Override
    public Person addCard(String personId, Card card) throws Exception {
String toPost = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
        toPost += "<arxdata timestamp=\"" + new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss").format(new Date()) + "\">\n";
        toPost += "<cards>\n";
        toPost += "<card>\n";
        toPost += "<number>00" + card.cardid + "</number>\n";
        toPost += "<format_name>" + card.format + "</format_name>\n";
        toPost += "<description>"+card.description+"</description>\n";
        toPost += "<person_id>" + personId + "</person_id>\n";
        if(card.deleted) {
            toPost += "<deleted>1</deleted>\n";
        }
        toPost += "</card>\n";
        toPost += "</cards>\n";
        toPost += "</arxdata>\n";
        
        User currentUser = doorManager.getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/import";
        
        httpLoginRequest(hostName, toPost);
        
        return getPerson(personId);
    }

    @Override
    public void clearDoorCache() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HashMap<String, List<AccessLog>> generateDoorLogForAllDoorsFromResult(String result) throws Exception {
        if(result.isEmpty()) {
            return new HashMap();
        }
        List<Door> allDoors = getAllDoors();
        HashMap<String, List<AccessLog>> returnResult = new HashMap();
        
        for(Door door : allDoors) {
            List<AccessLog> accessLog = generateDoorAccessLogFromResult(result, door.externalId);
            if (accessLog != null) {
                returnResult.put(door.externalId, accessLog);
            } else {
                GetShopLogHandler.logPrintStatic("Warning, was not able to fetch accesslog from ARX: \n result: \n " + result, storeManager.getStoreId());
            }
        }
        return returnResult;
    }
 
    private String convertToIso(String firstName) throws UnsupportedEncodingException {
        
        byte[] utf8bytes = firstName.getBytes();
        Charset utf8charset = Charset.forName("UTF-8");
        Charset iso88591charset = Charset.forName("ISO-8859-1");

        String string = new String ( utf8bytes, utf8charset );
        
        // "When I do a getbytes(encoding) and "
        byte[] iso88591bytes = string.getBytes(iso88591charset);

        // "then create a new string with the bytes in ISO-8859-1 encoding"
        String string2 = new String ( iso88591bytes, iso88591charset );

        return string2;
    }

    public String getDoorLogForced(long start, long end) {
        
        String filter = "<filter><name><mask>controller.access.card.valid.standard</mask></name></filter>";
        try {
            filter = URLEncoder.encode(filter, "UTF-8");
        }catch(Exception e) {
            e.printStackTrace();
        }
//        String hostName = ":5002/arx/eventexport?start_date="+start+"&end_date="+end + "&filter="+filter;
        String hostName = ":5002/arx/eventexport?start_date="+start+"&end_date="+end;
        String result = "";
        try {
            result = httpLoginRequest(hostName, "");
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    public String getDoorLog(long start, long end) {
        return getDoorLogForced(start, end);
    }

    private Door getDoor(String externalId) throws Exception {
        List<Door> allDoors = getAllDoors();
        for(Door door : allDoors) {
            if(door.externalId.equals(externalId)) {
                return door;
            }
        }
        return null;
    }
    
    private Person createPerson(PersonType person) throws UnsupportedEncodingException, ParseException {
        Person tmpPerson = new Person();

        tmpPerson.firstName = new String(person.getFirstName().getBytes("ISO-8859-1"),"UTF-8");
        tmpPerson.lastName = new String(person.getLastName().getBytes("ISO-8859-1"),"UTF-8");
        tmpPerson.id = person.getId();

        AccessCategoryList cat = person.getAccessCategories();
        List<Serializable> list = cat.getContent();
        String pattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        for(Object test : list) {
            if(test instanceof JAXBElement) {
                JAXBElement jaxelement = (JAXBElement) test;
                if(jaxelement.getValue() instanceof AccessCategoryType) {
                    AccessCategoryType type = (AccessCategoryType) jaxelement.getValue();
                    try {
                        AccessCategory category = new AccessCategory();
                        category.id = type.getId();
                        category.accessId = type.getId();
                        category.name = new String(type.getName().getBytes("ISO-8859-1"),"UTF-8");
                        if(type.getStartDate() != null && !type.getStartDate().isEmpty()) {
                            category.startDate = format.parse(type.getStartDate());
                        }
                        if(type.getEndDate() != null && !type.getEndDate().isEmpty()) {
                            category.endDate = format.parse(type.getEndDate());
                            if(tmpPerson.endDate == null || category.endDate.after(tmpPerson.endDate)) {
                                tmpPerson.endDate = category.endDate;
                            }
                        }
                        tmpPerson.accessCategories.add(category);
                    } catch (ParseException e) {
                        throw e;
                    }
                }
            }
        }

        return tmpPerson; 
    }

    private Door handleDoorNode(Node node) throws UnsupportedEncodingException {
        Door door = new Door();
        for(int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node innernode = node.getChildNodes().item(i);
            if(innernode.getNodeName().equals("name")) {
                door.name = new String(innernode.getTextContent().getBytes("ISO-8859-1"),"UTF-8");
            }
            if(innernode.getNodeName().equals("external_id")) {
                door.externalId = innernode.getTextContent();
            }
        }
        return door;
    }
    
    private List<Door> recursiveFindDoors(NodeList nodeList, int depth) throws UnsupportedEncodingException {
        List<Door> doors = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
             Node node = nodeList.item(i);
             if(node.getNodeName().equals("door")) {
                 doors.add(handleDoorNode(node));
             } else if(node.getChildNodes().getLength() > 0) {
                 doors.addAll(recursiveFindDoors(node.getChildNodes(), depth+1));
             }
        }
        return doors;
    }
 
    private List<Card> createCardList(String xml) throws Exception {
        InputStream is = new ByteArrayInputStream( xml.getBytes() );
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        try {
            Document document = builder.parse(is);
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            return recursiveFindCards(nodeList, 0);
        } finally {
            is.close();
        }
        
        
    }
    
    private List<Card> recursiveFindCards(NodeList nodeList, int depth) throws UnsupportedEncodingException {
        List<Card> cards = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
             Node node = nodeList.item(i);
             if(node.getNodeName().equals("card")) {
                 cards.add(handleCardNode(node));
             } else if(node.getChildNodes().getLength() > 0) {
                 cards.addAll(recursiveFindCards(node.getChildNodes(), depth+1));
             }
        }
        return cards;
    }

    private Card handleCardNode(Node node) {
        Card card = new Card();
        NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(child.getNodeName().equals("number")) {
                card.cardid = child.getTextContent();
            }
            if(child.getNodeName().equals("description")) {
                card.description = child.getTextContent();
            }
            if(child.getNodeName().equals("person_id")) {
                card.personId = child.getTextContent();
            }
            if(child.getNodeName().equals("format_name")) {
                card.format = child.getTextContent();
            }
        }
        return card;
    }

    private void doorActionForItem(BookingItem item, String type) throws Exception {
        List<Door> doors = getAllDoors();
        for (Door door : doors) {
            if (door.name.equals(item.bookingItemName) || door.name.equals(item.bookingItemAlias) || door.name.equals(item.doorId) || door.externalId.equals(item.doorId) || door.externalId.equals(item.bookingItemAlias) || door.externalId.equals(item.bookingItemName)) {
                doorAction(door.externalId, type);
            }
        }
    }
    
    private AccessLog buildAccessLogEntry(Node node, String externalId) {
        AccessLog log = new AccessLog();
        boolean found = false;
        Element eElement = (Element) node;
        NodeList nodeList = eElement.getElementsByTagName("argument");
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nodeinner = nodeList.item(i);
            Element element = (Element) nodeinner;
            if(element.getAttribute("externalId").equals(externalId)) {
                found = true;
            }
        }
        
        
        if(!found) {
            return null;
        } else {
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node nodeinner = nodeList.item(i);
                Element element = (Element) nodeinner;
                if(element == null || element.getAttribute("type") == null) {
                    continue;
                }
                if(element.getAttribute("type").equals("person")) {
                    log.personName = element.getAttribute("value");
                }
                if(element.getAttribute("type").equals("door")) {
                    log.door = element.getAttribute("value");
                }
                if(element.getAttribute("type").equals("card")) {
                    log.card = element.getAttribute("value");
                }
                if(element.getAttribute("type").equals("dac_properties")) {
                    log.dac_properties = element.getTextContent();
                }
            }
            
            Element element = (Element) node;
            log.timestamp = new Long(element.getAttribute("timestamp"));
            log.type = element.getAttribute("name");
        }
        return log;
    }
    
    private List<AccessLog> recursiveFindDoorLogEntry(NodeList nodeList, String externalid) {
        List<AccessLog> entries = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node.getNodeName().equals("event")) {
                AccessLog item = buildAccessLogEntry(node, externalid);
                if(item != null) {
                    entries.add(item);
                }
            } else if(node.getChildNodes().getLength() > 0) {
                entries.addAll(recursiveFindDoorLogEntry(node.getChildNodes(), externalid));
            }
        }
        return entries;
    }
    
    private List<AccessLog> generateDoorAccessLogFromResult(String result, String externalId) throws Exception {
        InputStream is = new ByteArrayInputStream( result.getBytes() );
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(is);
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            List<AccessLog> retresult = recursiveFindDoorLogEntry(nodeList, externalId);

            Collections.sort(retresult, new Comparator<AccessLog>(){
                public int compare(AccessLog o1, AccessLog o2){
                    return o1.timestamp > o2.timestamp ? -1 : 1;
                }
           });


            return retresult;
        } catch (SAXParseException sa) {
            return null;
        } finally {
            is.close();
        }
    }

    private List<Card> filterCards(List<Card> cards, Person tmpPerson) {
        List<Card> result = new ArrayList();
        for(Card card : cards) {
            if(card.personId.equals(tmpPerson.id)) {
                result.add(card);
            }
        }
        return result;
    }

    @Override
    public void closeAllForTheDay() throws Exception {
        List<Door> doors = getAllDoors();
        for(Door door : doors) {
            doorAction(door.externalId, "forceOpenOff");
        }
    }

    @Override
    public GetShopLockMasterCodes getMasterCodes() {
        return null;
    }

    @Override
    public void saveMasterCodes(GetShopLockMasterCodes masterCodes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
