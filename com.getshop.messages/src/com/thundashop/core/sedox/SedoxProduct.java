/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.BinaryRefAddr;

/**
 *
 * @author ktonder
 */
public class SedoxProduct extends DataCommon implements Comparable<SedoxProduct> {
    public List<SedoxBinaryFile> binaryFiles = new ArrayList();
    public List<SedoxProductHistory> histories = new ArrayList();
    public String filedesc;
    
    public String brand;
    public String model;
    public String engineSize;
    public String year;
    public String power;
    public String ecuType;
    public String build;
    public String ecuBrand;
    public String softwareNumber; 
    public String softwareSize;
    
    public String tool;
    public String status;
    public boolean saleAble = true;
    public String firstUploadedByUserId;
    public String originalChecksum;
    public String gearType;
    public String useCreditAccount;
    public String comment;
    
    public boolean started;
    public boolean isCmdEncryptedProduct=false;
    public String channel;
    public String ecuPartNumber;
    public String ecuHardwareNumber;
    public String ecuSoftwareNumber;
    public String ecuSoftwareVersion;
    
    public String uploadOrigin;
    
    public Map<String, Date> states = new HashMap();

    @Override
    public int compareTo(SedoxProduct o) {
        if (rowCreatedDate.after(o.rowCreatedDate)) {
            return -1;
        } else if (rowCreatedDate.before(o.rowCreatedDate)) {
            return 1;
        } else {
            return 0;
        }  
    }
    
    public void setParametersBasedOnFileString(String fileName) {
        String[] productAttributes = fileName.split(";");
        
        if (productAttributes.length != 16 &&  productAttributes.length != 17) {
            saleAble = false;
            return;
        }
        
        brand = productAttributes[0]; // %Vehicle.Producer%; (bmw / audi)
        model = productAttributes[1]; // %Vehicle.Series% (e90 / passat etc)
        engineSize = productAttributes[3]; // %Vehicle.Model% 
        build = productAttributes[2]; // %Vehicle.Build%;
        year = productAttributes[5]; // %Vehicle.Modelyear%
        power = productAttributes[6]; // %Engine.OutputPS%
        ecuBrand = productAttributes[7]; // %ECU.Producer%
        ecuType = productAttributes[8]; // %ECU.Build% 
        ecuPartNumber = productAttributes[9];
        ecuHardwareNumber = productAttributes[10]; // %ECU.ECUStg%;
        ecuSoftwareNumber = productAttributes[11]; // %ECU.ECUStg%;
        ecuSoftwareVersion = productAttributes[12]; // %ECU.ECUStg%;
        if (productAttributes.length > 16) {
            tool = productAttributes[16]; 
        }
    }

    public SedoxBinaryFile getFileById(int fileId) {
        for (SedoxBinaryFile binFile : binaryFiles) {
            if (binFile.id == fileId) {
                return binFile;
            }
        }
        
        return null;
    }

    @Override
    public String toString() {
        String checksumaddon = originalChecksum == null ? "" : " " + originalChecksum;
        return brand + " " + model + " " + engineSize + " " + power + " " + year + checksumaddon;
    }

    void removeBinaryFile(int fileId) {
        List<SedoxBinaryFile> sedoxBinFiles = new ArrayList();
        for (SedoxBinaryFile binFile : binaryFiles) {
            if (binFile.id != fileId) {
                sedoxBinFiles.add(binFile);
            }
        }
        this.binaryFiles = sedoxBinFiles;
    }
}