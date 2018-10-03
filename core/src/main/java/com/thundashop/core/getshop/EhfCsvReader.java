/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshop;

import com.thundashop.core.getshop.data.EhfComplientCompany;
import java.util.ArrayList;
import java.util.List;

import java.net.*;
import java.io.*;


/**
 *
 * @author ktonder
 */
public class EhfCsvReader {

    public static void main(String[] args) throws Exception {
        
        EhfCsvReader reader = new EhfCsvReader();
        List<EhfComplientCompany> companies = reader.getCompanies();
        System.out.println("Found: " + companies.size());
    }
    
    private List<String[]> readCsv() throws Exception {
        List<String> texts = getText("http://hotell.difi.no/download/difi/elma/participants?download");
        
        BufferedReader br = null;
        String cvsSplitBy = ";";
        List<String[]> lines = new ArrayList();
        
        for (String line : texts) {
            String[] country = line.split(cvsSplitBy);
            lines.add(country);
        }

        System.out.println("Lines from difi: " + lines.size());
        return lines;
    }

    public List<EhfComplientCompany> getCompanies() {
        try {
            return getCompaniesInternal();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList();
        }
    }
    
    public List<EhfComplientCompany> getCompaniesInternal() throws Exception {
        List<String[]> lines = readCsv();
        List<EhfComplientCompany> retList = new ArrayList();
        boolean firstLine = true;
        for (String[] s : lines) {
            if (firstLine) {
                firstLine = false;
                continue;
            }
            int vatnumber = Integer.parseInt(s[1].replaceAll("\"", ""));
            boolean canUse = s[6].replaceAll("\"", "").equals("Ja");
            if (canUse) {
                EhfComplientCompany ehfComp = new EhfComplientCompany();
                ehfComp.name = s[2].replaceAll("\"", "");
                ehfComp.vatNumber = vatnumber;
                retList.add(ehfComp);
            }
        }
        
        System.out.println("Added retlist: " + lines.size());
        
        return retList;
    }

    private List<String> getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        List<String> response = new ArrayList();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.add(inputLine);
        }

        in.close();

        return response;
    }
}
