package com.thundashop.core.utils;

import com.google.gson.Gson;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.Company;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.springframework.stereotype.Component;

class BrRegCompany {
    String nkode2 = "";
    String regifr = "";
    String nkode1 = "";
    String forradrpoststed = "";
    String ppostland = "";
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
    String nkode3 = "";
}

class ReturnValue {
    List<BrRegCompany> entries = new ArrayList();
}
/**
 *
 * @author ktonder
 */
@Component
public class BrRegEngine implements CompanySearchEngine {

    private Gson gson = new Gson();

    @Override
    public Company getCompany(String organisationNumber, boolean fetch) {

        if (organisationNumber == null)
            return null;
        
        String content = read(organisationNumber.trim(), false);
        ReturnValue fromJson = gson.fromJson(content, ReturnValue.class);

        if (fromJson.entries.size() == 0) {
            content = read(organisationNumber.trim(), true);
            fromJson = gson.fromJson(content, ReturnValue.class);
        }

        if (fromJson.entries.size() == 1) {
            BrRegCompany brRegCompany = fromJson.entries.get(0);
            Company company = new Company();
            company.name = brRegCompany.navn;
            company.address = new Address();
            company.address.address = brRegCompany.forretningsadr;
            company.address.city = brRegCompany.forradrpoststed;
            company.address.postCode = brRegCompany.forradrpostnr;
            company.address.countrycode = brRegCompany.forradrland;
                    
            company.type = brRegCompany.organisasjonsform;
            company.vatNumber = brRegCompany.orgnr.trim();
            
            /** Legacy set for customers */
            company.country = brRegCompany.forradrland;
            company.city = brRegCompany.forradrpoststed;
            company.postnumber = brRegCompany.forradrpostnr;
            company.streetAddress = brRegCompany.forretningsadr;
            
            return company;
        }

        return null;
    }

    private String read(String organisationUrl, boolean subdep) {
        HttpsURLConnection.setDefaultHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        try {
            organisationUrl = URLEncoder.encode(organisationUrl, "UTF-8");
            if (subdep) {
                return getContent("https://hotell.difi.no/api/json/brreg/underenheter?query=" + organisationUrl);
            }
            return getContent("https://hotell.difi.no/api/json/brreg/enhetsregisteret?query=" + organisationUrl);
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

    @Override
    public List<Company> search(String search) {
        String normalSearchResult = read(search, false);
        List<Company> companies = getCompanies(normalSearchResult);

        if (companies.isEmpty()) {
            addSubDepartments(search, companies);
        }

        return companies;
    }

    private void addSubDepartments(String search, List<Company> companies) {
        String resultSubDep = read(search, true);
        companies.addAll(getCompanies(resultSubDep));
    }

    private List<Company> getCompanies(String result) {
        ReturnValue fromJson = gson.fromJson(result, ReturnValue.class);
        List<Company> returnvalue = new ArrayList();

        for (BrRegCompany company : fromJson.entries) {
            Company retCompany = new Company();
            retCompany.vatNumber = company.orgnr;
            retCompany.name = company.navn;
            retCompany.address = new Address();
            retCompany.address.address = company.forretningsadr;
            retCompany.address.postCode = company.forradrpostnr;
            retCompany.address.city = company.forradrpoststed;
            
            returnvalue.add(retCompany);
        }

        return returnvalue;
    }

    @Override
    public String getName() {
        return "brreg";
    }

}
