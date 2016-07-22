
package com.thundashop.core.arx;

import com.assaabloy.arxdata.persons.AccessCategoryList;
import com.assaabloy.arxdata.persons.AccessCategoryType;
import com.assaabloy.arxdata.persons.Arxdata;
import com.assaabloy.arxdata.persons.PersonListType;
import com.assaabloy.arxdata.persons.PersonType;
import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.doormanager.DoorManagerConfiguration;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingFilter;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
@GetShopSession
public class DoorManager extends GetShopSessionBeanNamed implements IDoorManager {
    @Autowired
    UserManager usermanager;
    
    @Autowired
    StoreManager storeManager;
    
    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    BookingEngine bookingEngine;
    
    private List<Door> doorList = new ArrayList();
    
    @Override
    public List<Door> getAllDoors() throws Exception {
        List<Door> cachedDoors = getCachedDoors();
        if(!cachedDoors.isEmpty()) {
            return cachedDoors;
        }
        
        
        IDoorManager mgr = getDoorManager();
        
        List<Door> res = mgr.getAllDoors();
        doorList.clear();
        doorList.addAll(res);
        return res;
    }
    
    @Override
    public List<AccessCategory> getAllAccessCategories() throws Exception {
        return getDoorManager().getAllAccessCategories();
    }
    
    @Override
    public List<Person> getAllPersons() throws Exception {
        return getDoorManager().getAllPersons();
    }

    @Override
    public void doorAction(String externalId, String state) throws Exception {
        getDoorManager().doorAction(externalId, state);
    }

    @Override
    public HashMap<String, List<AccessLog>> getLogForAllDoor(long start, long end) throws Exception {
        return getDoorManager().getLogForAllDoor(start, end);
    }

    @Override
    public HashMap<String, List<AccessLog>> generateDoorLogForAllDoorsFromResult(String result) throws Exception {
        return getDoorManager().generateDoorLogForAllDoorsFromResult(result);
    }
    
    @Override
    public List<AccessLog> getLogForDoor(String externalId, long start, long end) throws Exception {
        return getDoorManager().getLogForDoor(externalId, start, end);
    }

    @Override
    public Person updatePerson(Person person) throws Exception {
        return getDoorManager().updatePerson(person);
    }

    @Override
    public Person getPerson(String id) throws Exception {
        return getDoorManager().getPerson(id);
    }

    @Override
    public Person addCard(String personId, Card card) throws Exception {
         return getDoorManager().addCard(personId, card);
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
    
    @Override
    public String pmsDoorAction(String code, String type) throws Exception {
        return getDoorManager().pmsDoorAction(code, type);
    }
    
    private IDoorManager getDoorManager() {
        ArxDoorManager test = new ArxDoorManager();
        return test;
    }

    List<Door> getDoorList() {
        return doorList;
    }

}
