/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.utils;

import com.google.gson.Gson;
import com.thundashop.core.usermanager.data.Company;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;

class BrRegCompany {
    String nkode2 = "";
    String regifr = "";
    String nkode1 = "";
    String forradrpoststed = "";
    String ppostland  = "";
    String forradrpostnr = "";
    String navn = "";
    String organisasjonsform = "";
    String ppoststed = "";
    String forretningsadr = "";
    String moms = "";
    String orgnr = "";
    String forradrland = "";
    String postadresse = "";
    String ppostnr = "";
    String forradrkommnr = "";
    String nkode3 =  "";
}

class ReturnValue {
    List<BrRegCompany> entries = new ArrayList();
}
/**
 *
 * @author ktonder
 */
@Component
public class BrRegEngine {
    private Gson gson = new Gson();
    
    public Company getCompany(String organisationNumber) {
        
        String content = read(organisationNumber.trim());
        ReturnValue fromJson = gson.fromJson(content, ReturnValue.class);
        if (fromJson.entries.size() == 1) {
            BrRegCompany brRegCompany = fromJson.entries.get(0);
            Company company = new Company();
            company.name = brRegCompany.navn;
            company.country = brRegCompany.forradrland;
            company.city = brRegCompany.forradrpoststed;
            company.postnumber = brRegCompany.forradrpostnr;
            company.streetAddress = brRegCompany.forretningsadr;
            company.type = brRegCompany.organisasjonsform;
            company.vatNumber = brRegCompany.orgnr.trim();
            return company;
        }

        return null;
    }
    
    private String read(String organisationUrl) {
        try {
            organisationUrl = URLEncoder.encode(organisationUrl, "UTF-8");
            return getContent("http://hotell.difi.no/api/json/brreg/enhetsregisteret?query="+organisationUrl);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return "";
    }
    
    private String getContent(String url) throws MalformedURLException, IOException {
        URL oracle = new URL(url);
        String content;
        try (BufferedReader in = new BufferedReader(
             new InputStreamReader(oracle.openStream()))) {
            content = "";
            String readed = "";
            while ((readed = in.readLine()) != null) {
                content += readed;
            }
        }
        return content;
    }

    HashMap<String, String> search(String search) {
        String result = read(search);
        ReturnValue fromJson = gson.fromJson(result, ReturnValue.class);
        HashMap<String,String> returnvalue = new HashMap();
        for(BrRegCompany company : fromJson.entries) {
            returnvalue.put(company.orgnr, company.navn);
        }
        return returnvalue;
    }
    
}
