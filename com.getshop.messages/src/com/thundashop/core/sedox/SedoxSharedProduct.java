/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class SedoxSharedProduct extends DataCommon implements Comparable<SedoxSharedProduct> {
    public List<SedoxBinaryFile> binaryFiles = new ArrayList();
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
    public String originalChecksum;
    public String gearType;
    public boolean isCmdEncryptedProduct=false;
    public String channel;
    public String ecuPartNumber;
    public String ecuHardwareNumber;
    public String ecuSoftwareNumber;
    public String ecuSoftwareVersion;
    public boolean saleAble = true;
    
    public void setParametersBasedOnFileString(String fileName) {
        String[] productAttributes = fileName.split(";");
        
        if (productAttributes.length != 17 && productAttributes.length != 18) {
            saleAble = false;
            return;
        }
            
        brand = productAttributes[0]; // %Vehicle.Producer% (bmw / audi)
        model = productAttributes[1]; // %Vehicle.Series% (e90 / passat)
        build = productAttributes[2]; // %Vehicle.Build% (Different values)
        engineSize = productAttributes[3]; // %Vehicle.Model% (2.0TDI)
        // charactristic = productAttributes[4]; // %Vehicle. Characteristic % (Not in use)
        year = productAttributes[5]; // %Vehicle.Modelyear% (year)
        power = productAttributes[6]; // %Engine.OutputPS% (hp)
        ecuBrand = productAttributes[7]; // %ECU.Producer% (bosch / siemens)
        ecuType = productAttributes[8]; // %ECU.Build% (edc15 / edc16)
        ecuPartNumber = productAttributes[9]; // %ECU.ECUProd% (car ecu part nr)
        ecuHardwareNumber = productAttributes[10]; // %ECU.ECUStg% (bosch / siemens part nr)
        ecuSoftwareNumber = productAttributes[11]; // %ECU. Software %; (softwaren nr)
        ecuSoftwareVersion = productAttributes[12]; // %ECU.Softwareversion % (software upg, or code)
        originalChecksum = productAttributes[13]; //%File.8Bitsum.Org% (org 8bit sum / checksum)
        // tuneChecksum = productAttributes[14]; //%File.8Bitsum% (tune 8bit sum / checksum) (not in use)
        // softwareSize = productAttributes[15]; //% ECU.Softwaresize % (size of file) (not in use)
        // chekcsumStatus = productAttributes[16]; 
         
        if (productAttributes.length > 17) {
            tool = productAttributes[17]; //% File.ReadHardware % (tool used for read)
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
        return getName();
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
    
    boolean hasMoreThenOriginalFile() {
        if (binaryFiles != null && binaryFiles.size() < 2) {
            return false;
        }
        
        return true;
    }

    boolean checkToolReplacement() {
        boolean ret = false;
        if (tool != null && tool.toLowerCase().equals("ctc")) {
            tool = "Alientech Powergate 2";
            ret = true;
            System.out.println("Product id: " + id);
        }
        
        if (tool != null && tool.toLowerCase().equals("ctc2")) {
            tool = "Alientech Powergate 3";
            ret = true;
            System.out.println("Product id2: " + id);
        }
        
        return ret;
    }

    @Override
    public int compareTo(SedoxSharedProduct o) {
        if (rowCreatedDate.after(o.rowCreatedDate)) {
            return -1;
        } else if (rowCreatedDate.before(o.rowCreatedDate)) {
            return 1;
        } else {
            return 0;
        }  
    }

    public String getName() {
        String checksumaddon = originalChecksum == null ? "" : " " + originalChecksum;
        return brand + " " + model + " " + engineSize + " " + power + " " + year + checksumaddon;
    }
}