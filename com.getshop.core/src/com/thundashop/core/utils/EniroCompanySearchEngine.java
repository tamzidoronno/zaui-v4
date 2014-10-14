/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class EniroCompanySearchEngine implements CompanySearchEngine {
	private Gson gson = new Gson();
	
	@Override
	public Company getCompany(String organisationNumber) {
		List<Company> companies = search(organisationNumber);
		if (companies.size() == 1) {
			return companies.iterator().next();
		}
		
		return null;
	}

	@Override
	public List<Company> search(String search) {
		List<Company> companies = new ArrayList();
		
		try {
			String content = getContent("http://api.eniro.com/cs/search/basic?country=se&search_word="+search+"&key=1085211466286583430&profile=GetShop&version=1.1.3");
			EniroSearchResult result = gson.fromJson(content, EniroSearchResult.class);
			for (EniroSearchResult.Adverts advert : result.adverts) {
				Company company = new Company();
				company.vatNumber = advert.companyInfo.orgNumber;
				
				if (company.vatNumber == null || company.vatNumber.contains("#")) {
					continue;
				}
				
				company.name = advert.companyInfo.companyName;
				company.city = advert.address.postArea;
				company.country = "SE";
				company.postnumber = advert.address.postCode;
				company.streetAddress = advert.address.streetName;
				companies.add(company);
			}
		} catch (IOException ex) {
			Logger.getLogger(EniroCompanySearchEngine.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return companies;
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

	@Override
	public String getName() {
		return "eniro";
	}
	
}
