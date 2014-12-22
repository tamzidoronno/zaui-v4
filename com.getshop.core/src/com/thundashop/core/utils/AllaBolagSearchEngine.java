/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.utils;

import com.thundashop.core.usermanager.data.Company;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author ktonder
 */
@Component
public class AllaBolagSearchEngine implements CompanySearchEngine {

    @Override
    public String getName() {
        return "allabolag";
    }

    @Override
    public Company getCompany(String organisationNumber, boolean fetch) {
        try {
            String urlAddition = fetch ? "fetch" : "find";
            String url = "http://www.allabolag.se/ws/BIWS/service.php?key=BIWSfc1a0c54b394d513754c6223537b&type="+urlAddition+"&query=nummer:"+organisationNumber;
            Map<String, Company> companies = new HashMap();
            companies.putAll(getCompaniesFromContent(getContent(url)));
            return companies.values().iterator().next();
        } catch (SAXException ex) {
            Logger.getLogger(AllaBolagSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(AllaBolagSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AllaBolagSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public List<Company> search(String search) {
        try {
            search = URLEncoder.encode(search, "UTF-8");
            String url = "http://www.allabolag.se/ws/BIWS/service.php?key=BIWSfc1a0c54b394d513754c6223537b&type=find&query=jurnamn:"+search;
            String url2 = "http://www.allabolag.se/ws/BIWS/service.php?key=BIWSfc1a0c54b394d513754c6223537b&type=find&query=nummer:"+search+"%20OR%20orgnr:"+search;

            Map<String, Company> companies = new HashMap();
            companies.putAll(getCompaniesFromContent(getContent(url)));
            companies.putAll(getCompaniesFromContent(getContent(url2)));
            
            String content = getContent(url);
            return new ArrayList<Company>(companies.values());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AllaBolagSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AllaBolagSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(AllaBolagSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(AllaBolagSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ArrayList<Company>();
    }
 
    private Map<String, Company> getCompaniesFromContent(String content) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new ByteArrayInputStream(content.getBytes("utf-8"))));
        
        NodeList list = document.getElementsByTagName("record");
        HashMap<String, Company> retCompanies = new HashMap();
        
        for (int i= 0; i<list.getLength() ; i++) {
            Node node = list.item(i);
            NodeList dataList = node.getChildNodes();
            Company company = new Company();
            for (int j = 0; j<dataList.getLength(); j++) {
                Node dataNode = dataList.item(j);
                
                if (dataNode.getNodeName().equals("nummer"))
                    company.vatNumber = URLDecoder.decode(dataNode.getTextContent().trim(), "UTF-8");
                
                if (dataNode.getNodeName().equals("orgnr") && company.vatNumber.equals("")) 
                    company.vatNumber = URLDecoder.decode(dataNode.getTextContent().trim(), "UTF-8");
                
                if (dataNode.getNodeName().equals("jurnamn")) 
                    company.name = URLDecoder.decode(dataNode.getTextContent().trim(), "UTF-8");
                
                if (dataNode.getNodeName().equals("ba_gatuadress")) 
                    company.streetAddress = URLDecoder.decode(dataNode.getTextContent().trim(), "UTF-8");
                
                if (dataNode.getNodeName().equals("riktnrtelnr")) 
                    company.phoneNumber = URLDecoder.decode(dataNode.getTextContent().trim(), "UTF-8");
                
                if (dataNode.getNodeName().equals("ba_postort")) 
                    company.city = URLDecoder.decode(dataNode.getTextContent().trim(), "UTF-8");
                
                if (dataNode.getNodeName().equals("ba_postnr")) 
                    company.postnumber = URLDecoder.decode(dataNode.getTextContent().trim(), "UTF-8");
                
            }
            
            if (company.streetAddress.equals("")) {
                company.streetAddress = "N/A";
            }
            
            if (company.postnumber.equals("")) {
                company.postnumber = "N/A";
            }
            
            retCompanies.put(company.vatNumber, company);
        }

        return retCompanies;
    }
    
    private String getContent(String url) throws MalformedURLException, IOException {
        URL oracle = new URL(url);
        String content;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()))) {
            content = "";
            String readed = "";
            while ((readed = in.readLine()) != null) {
                content += readed;
            }
        }
        return content;
    }
}
