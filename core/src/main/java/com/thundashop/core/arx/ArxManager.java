
package com.thundashop.core.arx;

import com.assaabloy.arxdata.persons.AccessCategoryList;
import com.assaabloy.arxdata.persons.AccessCategoryType;
import com.assaabloy.arxdata.persons.Arxdata;
import com.assaabloy.arxdata.persons.CardListType;
import com.assaabloy.arxdata.persons.CardType;
import com.assaabloy.arxdata.persons.PersonListType;
import com.assaabloy.arxdata.persons.PersonType;
import com.getshop.scope.GetShopSession;
import com.ibm.icu.util.Calendar;
import com.thundashop.app.logo.ILogoManager;
import com.thundashop.core.arx.Door;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
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
import javax.net.ssl.TrustManager;
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
public class ArxManager extends ManagerBase implements IArxManager {

    public HashMap<String, String> userPasswords = new HashMap();
    
    @Autowired
    UserManager usermanager;
            
    @Override
    public boolean logonToArx(String hostname, String username, String password) {
        String result = httpLoginRequest("https://" + hostname + ":5002/arx/export", username, password, "");
        if(result.equals("401")) {
            return false;
        }
        
        String reference = hostname + "@" + username;
        
        User user = usermanager.getUserByReference(reference);
        if(user == null) {
            user = new User();
            user.referenceKey = reference;
            user.emailAddress = reference;
            user.username = username;
            user.password = password;
            user.fullName = hostname;
            user.storeId = storeId;
            usermanager.createUser(user);
        }
        user.storeId = storeId;
        
        usermanager.forceLogon(user);
        
        userPasswords.put(user.id, password);
        
        return true;
    }

    
    public String httpLoginRequest(String address, String username, String password, String content) {
        String loginToken = null;
        String loginUrl = address;
        //
        DefaultHttpClient client = new DefaultHttpClient();
        client = wrapClient(client);
        HttpResponse httpResponse;
        

        HttpEntity entity;
        HttpPost request = new HttpPost(loginUrl);
        byte[] bytes = (username + ":" + password).getBytes();
        String encoding = Base64.encode(bytes);

        request.addHeader("Authorization", "Basic " + encoding);

        StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

        StringBody body = new StringBody(content, ContentType.TEXT_PLAIN);

        HttpEntity reqEntity = MultipartEntityBuilder.create()
                .addPart("upfile", body)
                .addPart("comment", comment)
                .build();

        request.setEntity(reqEntity);

        try {
            System.out.println("Now sending to arx");
            httpResponse = client.execute(request);
            
            Integer statusCode = httpResponse.getStatusLine().getStatusCode();
            if(statusCode == 401) {
                return "401";
            }

            
            entity = httpResponse.getEntity();
            
            
            
            System.out.println("Done sending to arx");

            if (entity != null) {
                InputStream instream = entity.getContent();
                int ch;
                StringBuilder sb = new StringBuilder();
                while ((ch = instream.read()) != -1) {
                    sb.append((char) ch);
                }
                String result = sb.toString();
                return result.trim();
            }
        } catch (ClientProtocolException e) {
            client.getConnectionManager().shutdown();
            return "401";
        } catch (UnknownHostException e) {
            client.getConnectionManager().shutdown();
            return "401";
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
        return "failed";
    }
    
    
    public static DefaultHttpClient wrapClient(DefaultHttpClient base) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
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
        User currentUser = getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;

        String hostName = arxHost + ":5002/arx/export_accessarea";
        String password = userPasswords.get(currentUser.id);
        System.out.println("Looking at : " + hostName);
        
        String result = httpLoginRequest(hostName, currentUser.username, password, "");
        InputStream is = new ByteArrayInputStream( result.getBytes() );
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(is);
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        List<Door> doors = recursiveFindDoors(nodeList, 0);
        
        return doors;
    }
    
    public List<AccessCategory> getAllAccessCategories() throws Exception {
        User currentUser = getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/export_accesscategory";
        String password = userPasswords.get(currentUser.id);
        System.out.println("Looking at : " + hostName);
        
        String result = httpLoginRequest(hostName, currentUser.username, password, "");
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
        System.out.println("Looking at : " + hostName);
        
        String result = httpLoginRequest(hostName, currentUser.username, password, "");
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

    @Override
    public void doorAction(String externalId, String state) throws Exception {
        User currentUser = getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/door_actions?externalid="+externalId+"&type="+state;
        if(state.equals("forceOpen") || state.equals("forceClose")) {
            hostName += "&&value=on";
        }
        
        String password = userPasswords.get(currentUser.id);
        System.out.println(hostName);
        httpLoginRequest(hostName, currentUser.username, password, "");
    }

    @Override
    public HashMap<String, List<AccessLog>> getLogForAllDoor(long start, long end) throws Exception {
        String result = getDoorLog(start, end);
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
                    System.out.println(log.dac_properties);
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
        String firstName = new String(person.firstName.getBytes("UTF-8"), "ISO-8859-1");
        String lastName = new String(person.lastName.getBytes("UTF-8"), "ISO-8859-1");
        
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
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/import";
        String password = userPasswords.get(currentUser.id);
        
        httpLoginRequest(hostName, currentUser.username, password, toPost);
        
        return person;
    }

    @Override
    public Person getPerson(String id) throws Exception {
        User currentUser = getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/export?external_id=" + id + "&exclude_deleted=1";
        String password = userPasswords.get(currentUser.id);
        System.out.println("Looking at : " + hostName);
        
        String result = httpLoginRequest(hostName, currentUser.username, password, "");
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
                card.format = child.getNodeValue();
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
        toPost += "<description></description>\n";
        toPost += "<person_id>" + personId + "</person_id>\n";
        if(card.deleted) {
            toPost += "<deleted>1</person_id>\n";
        }
        toPost += "</card>\n";
        toPost += "</cards>\n";
        toPost += "</arxdata>\n";
        
        User currentUser = getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/import";
        String password = userPasswords.get(currentUser.id);
        
        httpLoginRequest(hostName, currentUser.username, password, toPost);
        
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

    private void findDoorStatus(List<Door> doors) {
        User currentUser = getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;

        String hostName = arxHost + ":5002/arx/statusexport";
        String password = userPasswords.get(currentUser.id);
        System.out.println("Looking at : " + hostName);
        
        
        String result = httpLoginRequest(hostName, currentUser.username, password, "");
        String[] dacs = result.split("<dac>");
        for(String dac : dacs) {
            for(Door door : doors) {
                System.out.println(door.externalId);
                if(dac.contains(door.externalId) && dac.contains("motor_lock_state")) {
                    String[] lines = dac.split("\n");
                    for(String line : lines) {
                        if(line.contains("motor_lock_state")) {
                            String state = line;
                            state.replace("<motor_lock_state>", "");
                            state.replace("</motor_lock_state>", "");
                            door.state = state;
                        }
                    }
                }
            }
        }
    }

    private String getDoorLog(long start, long end) {
         User currentUser = getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/eventexport?start_date="+start+"&end_date="+end+"&filter=" + URLEncoder.encode("<filter><name>"
                + "<mask>controller.door.forcedUnlock</mask>"
                + "<mask>controller.door.requestToExit</mask>"
                + "<mask>controller.door.mode.unlocked</mask>"
                + "<mask>controller.door.mode.locked</mask>"
                + "<mask>acs.dac.update</mask>"
                + "<mask>controller.door.pulseOpenRequest</mask>"
                + "</name>"
                + "</filter>");
        System.out.println("Looking up: " + hostName);
        String password = userPasswords.get(currentUser.id);
        String result = httpLoginRequest(hostName, currentUser.username, password, "");
        return result;
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

}
