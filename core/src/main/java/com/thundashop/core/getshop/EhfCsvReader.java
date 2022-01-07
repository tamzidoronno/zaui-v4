/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshop;

import com.thundashop.core.getshop.data.EhfComplientCompany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import java.net.*;
import java.io.*;


/**
 *
 * @author ktonder
 */
public class EhfCsvReader {
    private static final Logger log = LoggerFactory.getLogger(EhfCsvReader.class);
    public static void main(String[] args) throws Exception {
        
        EhfCsvReader reader = new EhfCsvReader();
        List<EhfComplientCompany> companies = reader.getCompanies();
    }
    
    private List<String[]> readCsv() throws Exception {
        List<String> texts = getText("https://hotell.difi.no/download/difi/elma/participants?download");
        
        BufferedReader br = null;
        String cvsSplitBy = ";";
        List<String[]> lines = new ArrayList<>();
        
        for (String line : texts) {
            String[] country = line.split(cvsSplitBy);
            lines.add(country);
        }

        return lines;
    }

    public List<EhfComplientCompany> getCompanies() {
        try {
            return getCompaniesInternal();
        } catch (Exception ex) {
            log.error("Get Companies Error: ", ex);
            return new ArrayList<>();
        }
    }
    
    public List<EhfComplientCompany> getCompaniesInternal() throws Exception {
        List<String[]> lines = readCsv();
        List<EhfComplientCompany> retList = new ArrayList<>();
        boolean firstLine = true;
        int positionToCheck = 0;
        int positionToCheck_ehf_3_0 = 0;
        
        for (String[] s : lines) {
            if (firstLine) {
                for (String a : s) {
                    if (a.trim().toLowerCase().equals("\"ehf_invoice_2_0\"")) {
                        break;
                    }
                    positionToCheck++;
                }
                
                for (String a : s) {
                    if (a.trim().toLowerCase().equals("\"peppolbis_3_0_billing_01_ubl\"")) {
                        break;
                    }
                    positionToCheck_ehf_3_0++;
                }
                
                
                
                firstLine = false;
                continue;
            }
            Long vatnumber = Long.parseLong(s[1].replaceAll("\"", ""));
            boolean canUse = s[positionToCheck-1].replaceAll("\"", "").equals("Ja");
            boolean canUse30 = s[positionToCheck_ehf_3_0].replaceAll("\"", "").equals("Ja");
            if (canUse || canUse30) {
                EhfComplientCompany ehfComp = new EhfComplientCompany();
                ehfComp.name = s[2].replaceAll("\"", "");
                ehfComp.vatNumber = vatnumber;
                retList.add(ehfComp);
            }
        }
        
        return retList;
    }

    private List<String> getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        List<String> response = new ArrayList<>();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.add(inputLine);
        }

        in.close();

        return response;
    }
}
