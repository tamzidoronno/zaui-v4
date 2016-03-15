/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.utils;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.utilmanager.data.FileObject;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.usermanager.data.Company;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class UtilManager extends ManagerBase implements IUtilManager {

    public HashMap<String, FileObject> files = new HashMap();

    @Autowired
    private FrameworkConfig frameworkConfig;
    
    @Autowired
    private List<CompanySearchEngine> companyEngines;
    
    @Autowired
    private StoreApplicationPool storeApplicationPool;
            
    private int currentStartup;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon tmpData : data.data) {
            if (tmpData instanceof FileObject) {
                files.put(tmpData.id, (FileObject) tmpData);
            }
        }
        
        String startupCount = getManagerSetting("startupcount");
        if (startupCount == null) {
            currentStartup = 0;
        } else {
            currentStartup = Integer.parseInt(startupCount);
        }
        
        currentStartup++;
        setManagerSetting("startupcount", ""+currentStartup);
    }

    
    @Override
    public Company getCompanyFromBrReg(String companyVatNumber) throws ErrorException {
        return getCompanySearchEngine().getCompany(companyVatNumber, true);
    }

    @Override
    public List<Company> getCompaniesFromBrReg(String search) throws ErrorException {
        return getCompanySearchEngine().search(search);
    }

    @Override
    public String saveFile(FileObject file) throws ErrorException {
        file.storeId = storeId;
        databaseSaver.saveObject(file, credentials);
        files.put(file.id, file);
        return file.id;
    }

    @Override
    public FileObject getFile(String id) throws ErrorException {
        return files.get(id);
    }

    @Override
    public String getBase64EncodedPDFWebPage(String urlToPage) {
        urlToPage = urlToPage.replaceAll("&amp;", "&");
        String tmpPdfName = "/tmp/"+UUID.randomUUID().toString() + ".pdf";
        boolean executed = executeCommand("/usr/local/bin/wkhtmltopdf.sh " + urlToPage + " " + tmpPdfName);
        
        if (!executed) {
            executed = executeCommand("wkhtmltopdf -B 0 -L 0 -R 0 -T 0  " + urlToPage + " " + tmpPdfName);
        }
        
        if (!executed) {
            throw new ErrorException(1033);
        }
        
        File file = new File(tmpPdfName);
        byte[] bytes;
        try {
            bytes = InvoiceManager.loadFile(file);
            byte[] encoded = Base64.encodeBase64(bytes);
            String encodedString = new String(encoded);
            file.delete();
            return encodedString;
        } catch (IOException ex) {
            throw new ErrorException(1033);
        }
    }
    
    public String getBase64EncodedPDFWebPageWithBorders(String urlToPage) {
        String tmpPdfName = "/tmp/"+UUID.randomUUID().toString() + ".pdf";
        boolean executed = executeCommand("/usr/local/bin/wkhtmltopdfwithborders.sh " + urlToPage + " " + tmpPdfName);
        
        if (!executed) {
            executed = executeCommand("wkhtmltopdf " + urlToPage + " " + tmpPdfName);
        }
        
        if (!executed) {
            throw new ErrorException(1033);
        }
        
        File file = new File(tmpPdfName);
        byte[] bytes;
        try {
            bytes = InvoiceManager.loadFile(file);
            byte[] encoded = Base64.encodeBase64(bytes);
            String encodedString = new String(encoded);
            file.delete();
            return encodedString;
        } catch (IOException ex) {
            throw new ErrorException(1033);
        }
    }
    
    private boolean executeCommand(String command) {
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String d = "";
            
            boolean ok = true;
            while ((d = stdError.readLine()) != null) {
                if (d.contains("error"))
                    ok = false;
            }
            
            return ok;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isInProductionMode() {
        return frameworkConfig.productionMode;
    }

    @Override
    public int getStartupCount() {
        return currentStartup;
    }
    
    @Override
    public Company getCompanyFree(String companyVatNumber) throws ErrorException {
        return getCompanySearchEngine().getCompany(companyVatNumber, false);
    }

    private CompanySearchEngine getCompanySearchEngine() {
        Application app = storeApplicationPool.getApplication("278fc1b5-efec-47af-b3d2-f7dbe2194968");
        if (app != null) {
            return companyEngines.stream().filter(o -> o.getName().equals("allabolag"))
                    .findFirst()
                    .orElse(null);
        } else {
            return companyEngines.stream().filter(o -> o.getName().equals("brreg"))
                    .findFirst()
                    .orElse(null);
        }
        
    }

}
