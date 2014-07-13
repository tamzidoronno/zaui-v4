/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;
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
    public String ecuBrand;
    public String softwareNumber; 
    public int softwareSize;
    
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
        
        if (productAttributes.length != 16) {
            System.out.println("WARNING! Cant update attributes, the filename is not the correct parameters. Check winols settings");
            return;
        }
        
        brand = productAttributes[0];
        model = productAttributes[1];
        engineSize = productAttributes[3];
        year = productAttributes[5];
        power = productAttributes[6];
        ecuBrand = productAttributes[7];
        ecuType = productAttributes[8];
        softwareNumber = productAttributes[10];
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