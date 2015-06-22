
package com.thundashop.core.arx;

import com.assaabloy.arxdata.persons.AccessCategoryList;
import com.assaabloy.arxdata.persons.AccessCategoryType;
import com.assaabloy.arxdata.persons.Arxdata;
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
import java.util.Collections;
import java.util.Comparator;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
        return recursiveFindDoors(nodeList, 0);
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
        File intfile = new File("integration.xsd");
        if(!intfile.exists()) {
            System.out.println("Integration.xds does not exist at path: " + intfile.getAbsolutePath());
            return new ArrayList();
        }
        Schema schema = sf.newSchema(intfile);
        unmarsh.setSchema(schema);
        //unmarshall the xml file
        Arxdata obj;
        obj = (Arxdata) unmarsh.unmarshal(is);
        PersonListType persons = obj.getPersons();
        
        List<Person> personlist = new ArrayList();
        
        for(PersonType person : persons.getPerson()) {
            Person tmpPerson = new Person();

            tmpPerson.firstName = new String(person.getFirstName().getBytes("ISO-8859-1"),"UTF-8");
            tmpPerson.lastName = new String(person.getLastName().getBytes("ISO-8859-1"),"UTF-8");
            personlist.add(tmpPerson);
            
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
        
        String password = userPasswords.get(currentUser.id);
        httpLoginRequest(hostName, currentUser.username, password, "");
    }

    @Override
    public List<AccessLog> getLogForDoor(String externalId, long start, long end) throws Exception {
        
        User currentUser = getSession().currentUser;
        String arxHost = "https://" + currentUser.fullName;
        String hostName = arxHost + ":5002/arx/eventexport?start_date="+start+"&end_date="+end+"&filter=" + URLEncoder.encode("<filter><name><mask>controller.door.forcedUnlock</mask><mask>controller.door.requestToExit</mask></name></filter>");
        System.out.println("Looking up: " + hostName);
        String password = userPasswords.get(currentUser.id);
        String result = httpLoginRequest(hostName, currentUser.username, password, "");
        
        InputStream is = new ByteArrayInputStream( result.getBytes() );
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(is);
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        List<AccessLog> retresult = recursiveFindDoorLogEntry(nodeList, externalId);
        Collections.sort(retresult, new Comparator<AccessLog>() {
            public int compare(AccessLog o1, AccessLog o2) {
                if(o1.timestamp < o2.timestamp) {
                    return 1;
                } else if(o1.timestamp == o2.timestamp) {
                    return 0;
                }
                return -1;
            }
          });
        
        
        return retresult;
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
            }
            
            Element element = (Element) node;
            log.timestamp = new Long(element.getAttribute("timestamp"));
            log.type = element.getAttribute("name");
        }
        return log;
    }
    
}
