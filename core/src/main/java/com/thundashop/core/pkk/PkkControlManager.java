/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pkk;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.pkkcontrol.PkkControlData;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class PkkControlManager extends ManagerBase implements IPkkControlManager {
    private List<PkkControlData> pkkControls = new ArrayList();
    
    @Autowired
    private MessageManager messageManager;
    
    @Autowired
    private StoreManager storeManager;
            
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof PkkControlData) {
                pkkControls.add((PkkControlData)dataCommon);
            }
        }
    }

    private PkkControlData getDataCarInformation(String licensePlateNumber) throws MalformedURLException, IOException, SAXException, ParserConfigurationException {
        URL url = new URL("http://www.vegvesen.no/Kjoretoy/Kjop+og+salg/Kjøretøyopplysninger?registreringsnummer="+licensePlateNumber);
        InputStream is = url.openStream();  // throws an IOException
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = "";
        String table = "";
        
        String all = "";
        while ((line = br.readLine()) != null) {
            all += line;
        }
//        
        if (all.indexOf("<table") < 0) {
            return null;
        }
        
        if (all.indexOf("</table") < 0) {
            return null;
        }
        
        table = all.substring(all.indexOf("<table"));
        table = table.substring(0, table.indexOf("</table")+8);

        if (table.equals("")) {
            return null;
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource xmlis = new InputSource(new StringReader(table));
        Document xmlTable = builder.parse(xmlis);
        
        NodeList list = xmlTable.getElementsByTagName("td");
        PkkControlData controlData = new PkkControlData();
        
        if (list.getLength() > 12) {
            controlData.modelAndBrand = list.item(1).getTextContent();
            controlData.licensePlate = list.item(0).getTextContent();
            controlData.vineNumber = list.item(7).getTextContent();
            controlData.registedYear = list.item(3).getTextContent();
            controlData.lastControl = list.item(11).getTextContent();
            controlData.nextControl = list.item(12).getTextContent();
            return controlData;
        } else {
            return null;
        }
    }
    
    private PkkControlData getDataEuKontroll(String licensePlateNumber) throws MalformedURLException, IOException, SAXException, ParserConfigurationException {
        URL url = new URL("http://www.vegvesen.no/Kjoretoy/Kjop+og+salg/Periodisk+kjoretoykontroll/Kontrollfrist?ticket=CD9CA2A309394CE02484145274C05303&registreringsnummer="+licensePlateNumber);
        InputStream is = url.openStream();  // throws an IOException
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = "";
        String table = "";
        
        while ((line = br.readLine()) != null) {
            if (line.toLowerCase().contains("merke og modell")) {
                table = line;
                table = table.substring(table.indexOf("<table"));
                table = table.substring(0, table.indexOf("</table")+8);
                break;
            }
        }
        
        if (table.equals("")) {
            return null;
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource xmlis = new InputSource(new StringReader(table));
        Document xmlTable = builder.parse(xmlis);
        
        NodeList list = xmlTable.getElementsByTagName("td");
        PkkControlData controlData = new PkkControlData();
        
        
        if (list.getLength() == 6) {
            controlData.modelAndBrand = list.item(0).getTextContent();
            controlData.licensePlate = list.item(1).getTextContent();
            controlData.vineNumber = list.item(2).getTextContent();
            controlData.registedYear = list.item(3).getTextContent();
            controlData.lastControl = list.item(4).getTextContent();
            controlData.nextControl = list.item(5).getTextContent();
            return controlData;
        } else {
            return null;
        }
    }

    @Override
    public PkkControlData getPkkControlData(String licensePlate) throws ErrorException {
        PkkControlData data = null;
        
        try {
            data = getDataCarInformation(licensePlate);
            if (data == null) {
                data = new PkkControlData();
                data.vineNumber = "sn1235781982XX";
                data.modelAndBrand = "Mitsubitsu Colt (Demo Data)";
                data.registedYear = "1993";
                data.lastControl = "10/03-2013";
                data.nextControl = "10/03-2015";
                data.licensePlate = licensePlate;
            }
        } catch (IOException ex) {
            Logger.getLogger(PkkControlManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(PkkControlManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PkkControlManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return data;
    }

    @Override
    public void registerPkkControl(PkkControlData data) throws ErrorException {
        saveObject(data);
        pkkControls.add(data);
        sendEmail(data);
    }

    @Override
    public List<PkkControlData> getPkkControls() {
        return pkkControls;
    }

    @Override
    public void removePkkControl(String id) throws ErrorException {
        for (PkkControlData data : pkkControls) {
            if (data.id.equals(id)) {
                pkkControls.remove(data);
                deleteObject(data);
                return;
            }
        }
    }

    private void sendEmail(PkkControlData data) throws ErrorException {
        Store store = storeManager.getMyStore();
        String content = "<b>Ønsker oppfølging  for PKK Kontroll.</b>"; 
        content += "<br/> "; 
        content += "<br/> Bil: "+data.modelAndBrand; 
        content += "<br/> Årsmodell: "+data.registedYear; 
        content += "<br/> Regnr: "+data.licensePlate; 
        content += "<br/> Understellsnummer: "+data.vineNumber; 
        content += "<br/> Site godkjent: "+data.lastControl; 
        content += "<br/> Neste godkjennelse: "+data.nextControl; 
        content += "<br/> "; 
        content += "<br/> <b>Kontakt info</b>"; 
        content += "<br/> Navn: " + data.name; 
        content += "<br/> Epost: " + data.email; 
        content += "<br/> Mobilnr: " + data.cellphone; 
        
        messageManager.sendMail(store.configuration.emailAdress, "Pkk Control Manager", "Pkk kontroll", content, "post@getshop.com", "Meca webpakke");
    }
}