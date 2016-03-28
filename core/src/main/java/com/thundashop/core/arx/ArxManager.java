
package com.thundashop.core.arx;

import com.assaabloy.arxdata.persons.AccessCategoryList;
import com.assaabloy.arxdata.persons.AccessCategoryType;
import com.assaabloy.arxdata.persons.Arxdata;
import com.assaabloy.arxdata.persons.CardListType;
import com.assaabloy.arxdata.persons.CardType;
import com.assaabloy.arxdata.persons.PersonListType;
import com.assaabloy.arxdata.persons.PersonType;
import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.ibm.icu.util.Calendar;
import com.thundashop.app.logo.ILogoManager;
import com.thundashop.app.newsmanager.data.MailSubscription;
import com.thundashop.app.newsmanager.data.NewsEntry;
import com.thundashop.core.arx.Door;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.axis.encoding.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.xerces.dom.ElementNSImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component
@GetShopSession
public class ArxManager extends GetShopSessionBeanNamed implements IArxManager {

    public HashMap<String, String> userPasswords = new HashMap();
    
    @Autowired
    UserManager usermanager;
    
    @Autowired
    StoreManager storeManager;
    
    @Autowired
    PmsManager pmsManager;
    
    private List<Door> doorList = new ArrayList();
    private String arxHostname = null;
    private String arxUsername = null;
    private String arxPassword = null;
    private boolean doneClosedForToday = false;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon retData : data.data) {
            if (retData instanceof Door) {
                doorList.add((Door) retData);
            }
        }
    }
         
    public String httpLoginRequest(String address, String content) throws Exception {
        User currentUser = null;
        if(getSession() != null) {
            currentUser = getSession().currentUser;
        }
        
        String username = pmsManager.configuration.arxUsername;
        String arxHost = pmsManager.configuration.arxHostname;
        String password = pmsManager.configuration.arxPassword;
        
        if(!address.startsWith("http")) {
            address = "https://" + arxHost + address;
        }
        
        ArxConnection connection = new ArxConnection();
        return connection.httpLoginRequest(address, username, password, content);
    }
    
    
    public static DefaultHttpClient wrapClient(DefaultHttpClient base) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509ExtendedTrustManager tm = new X509ExtendedTrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String string, Socket socket) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String string, Socket socket) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException {
                }
            };
            
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = base.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            return new DefaultHttpClient(ccm, base.getParams());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Door> getAllDoors() throws Exception {
        List<Door> cachedDoors = getCachedDoors();
        if(!cachedDoors.isEmpty()) {
            return cachedDoors;
        }
        

        String hostName = ":5002/arx/export_accessarea";
        
        String result = httpLoginRequest(hostName, "");
        InputStream is = new ByteArrayInputStream( result.getBytes() );
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(is);
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        List<Door> doors = recursiveFindDoors(nodeList, 0);
        
        for(Door door : doors) {
            saveDoor(door);
        }
        
        return doors;
    }
    
    public List<AccessCategory> getAllAccessCategories() throws Exception {
        User currentUser = getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/export_accesscategory";
        String password = userPasswords.get(currentUser.id);
//        System.out.println("Looking at : " + hostName);
        
        String result = httpLoginRequest(hostName, "");
        InputStream is = new ByteArrayInputStream( result.getBytes() );
        
        JAXBContext context = JAXBContext.newInstance("com.assaabloy.arxdata.access");
        Unmarshaller unmarsh = context.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File("/home/boggi/accessschema.xml"));
        unmarsh.setSchema(schema);
        //unmarshall the xml file
        AccessCategoryList obj;
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
    }
    
    
    
    @Override
    public List<Person> getAllPersons() throws Exception {
        User currentUser = getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/export?include_accesscategory_id=true&exclude_deleted=true";
        String password = userPasswords.get(currentUser.id);
//        System.out.println("Looking at : " + hostName);
        
        String result = httpLoginRequest(hostName, "");
        InputStream is = new ByteArrayInputStream( result.getBytes() );
        
        JAXBContext context = JAXBContext.newInstance("com.assaabloy.arxdata.persons");
        
        Unmarshaller unmarsh = context.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        //specify the schema definition for parsing
        Schema schema = sf.newSchema(new File("integration.xsd"));
        unmarsh.setSchema(schema);
        //unmarshall the xml file
        Arxdata obj;
        obj = (Arxdata) unmarsh.unmarshal(is);
        PersonListType persons = obj.getPersons();
        
        List<Person> personlist = new ArrayList();
        List<Card> cards = createCardList(result);
        
        for(PersonType person : persons.getPerson()) {
            Person tmpPerson = createPerson(person);
            tmpPerson.cards.addAll(filterCards(cards, tmpPerson));
            personlist.add(tmpPerson);
        }
        return personlist;
    }

    @Override
    public boolean isLoggedOn() {
        if(usermanager.isLoggedIn()) {
            if(userPasswords.containsKey(getSession().currentUser.id)) {
                return true;
            } else {
                usermanager.logout();
            }
        }
        return false;
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

    public void doorAction(String externalId, String state, boolean setOn) throws Exception {
        String hostName = ":5002/arx/door_actions?externalid="+externalId+"&type="+state;
        if(setOn) {
            hostName += "&value=on";
        } else {
            hostName += "&value=off";
        }
        httpLoginRequest(hostName,"");
    }
    
    @Override
    public void doorAction(String externalId, String state) throws Exception {
        User currentUser = getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/door_actions?externalid="+externalId+"&type="+state;
        if(state.equals("forceOpen")) {
            Door door = getDoor(externalId);
            if(door.forcedOpen) {
                hostName += "&value=off";
                door.forcedOpen = false;
            } else {
                hostName += "&value=on";
                door.forcedOpen = true;
            }
            saveObject(door);
        }
        if(state.equals("forceClose")) {
            Door door = getDoor(externalId);
            if(door.forcedClose) {
                hostName += "&value=off";
                door.forcedClose = false;
            } else {
                hostName += "&value=on";
                door.forcedClose = true;
            }
            saveObject(door);
        }
        
        String password = userPasswords.get(currentUser.id);
//        System.out.println(hostName);
        httpLoginRequest(hostName,"");
    }

    @Override
    public HashMap<String, List<AccessLog>> getLogForAllDoor(long start, long end) throws Exception {
        String result = getDoorLog(start, end);
        return generateDoorLogForAllDoorsFromResult(result);
    }

    @Override
    public HashMap<String, List<AccessLog>> generateDoorLogForAllDoorsFromResult(String result) throws Exception {
        if(result.isEmpty()) {
            return new HashMap();
        }
        List<Door> allDoors = getAllDoors();
        HashMap<String, List<AccessLog>> returnResult = new HashMap();
        
        for(Door door : allDoors) {
            returnResult.put(door.externalId, generateDoorAccessLogFromResult(result, door.externalId));
        }
        return returnResult;
    }
    
    @Override
    public List<AccessLog> getLogForDoor(String externalId, long start, long end) throws Exception {
        String result = getDoorLog(start, end);
        return generateDoorAccessLogFromResult(result, externalId);
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
//                    System.out.println(log.dac_properties);
                }
            }
            
            Element element = (Element) node;
            log.timestamp = new Long(element.getAttribute("timestamp"));
            log.type = element.getAttribute("name");
        }
        return log;
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
                toPost += "<end_date>" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(category.endDate) + "</end_date>\n";
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
        
        User currentUser = getSession().currentUser;
        String hostName = ":5002/arx/import";
        String password = arxPassword;
        String username = arxUsername;
        if(currentUser != null) {
            password = userPasswords.get(currentUser.id);
            username = currentUser.username;
        }
        
        httpLoginRequest(hostName,toPost);
        
        return person;
    }

    @Override
    public Person getPerson(String id) throws Exception {
        User currentUser = getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/export?external_id=" + id + "&exclude_deleted=1";
        String password = userPasswords.get(currentUser.id);
//        System.out.println("Looking at : " + hostName);
        
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
        obj = (Arxdata) unmarsh.unmarshal(is);
        
        Person person = createPerson(obj.getPersons().getPerson().get(0));
        person.cards.addAll(createCardList(result));
        
        return person;
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

    private List<Card> createCardList(String xml) throws Exception {
        InputStream is = new ByteArrayInputStream( xml.getBytes() );
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(is);
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        return recursiveFindCards(nodeList, 0);
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
        
        User currentUser = getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/import";
        
        httpLoginRequest(hostName, toPost);
        
        return getPerson(personId);
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
    

    public String getDoorLog(long start, long end) {
        return getDoorLogForced(start, end);
    }

    private List<AccessLog> generateDoorAccessLogFromResult(String result, String externalId) throws Exception {
        InputStream is = new ByteArrayInputStream( result.getBytes() );
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(is);
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        List<AccessLog> retresult = recursiveFindDoorLogEntry(nodeList, externalId);
        return retresult;
    }

    private void saveDoor(Door door) {
        saveObject(door);
        doorList.add(door);
    }

    private List<Door> getCachedDoors() {
        return doorList;
    }

    @Override
    public void clearDoorCache() throws Exception {
        List<Door> cachedDoors = getAllDoors();
        for(Door door : cachedDoors) {
            deleteObject(door);
        }
        doorList.removeAll(cachedDoors);
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

    /**
     * A One time override of username and password.
     * @param arxHostname
     * @param arxUsername
     * @param arxPassword 
     */
    public void overrideCredentials(String arxHostname, String arxUsername, String arxPassword) {
        this.arxHostname = arxHostname;
        this.arxUsername = arxUsername;
        this.arxPassword = arxPassword;
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

    public void clearOverRideCredentials() {
        arxHostname = null;
        arxPassword = null;
        arxUsername = null;
    }

    public void closeAllForTheDay() throws Exception {
        if(doneClosedForToday) {
            return;
        }
        List<Door> doors = getAllDoors();
        for(Door door : doors) {
            doorAction(door.externalId, "forceOpen", false);
        }
        doneClosedForToday = true;
    }

    public void clearCloseForToday() {
        doneClosedForToday = false;
    }

    public String getDoorLogForced(long start, long end) {
        String hostName = ":5002/arx/eventexport?start_date="+start+"&end_date="+end;
        String result = "";
        try {
            result = httpLoginRequest(hostName, "");
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }

}
