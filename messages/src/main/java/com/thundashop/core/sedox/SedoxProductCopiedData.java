/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class SedoxProductCopiedData extends DataCommon {
    @Transient
    private List<SedoxBinaryFile> binaryFiles = new ArrayList();
    
    @Transient
    private String filedesc;
    
    @Transient
    private String brand;
    
    @Transient
    private String model;
    
    @Transient
    private String engineSize;
    
    @Transient
    private String year;
    
    @Transient
    private String power;
    
    @Transient
    private String ecuType;
    
    @Transient
    private String build;
    
    @Transient
    private String ecuBrand;
    
    @Transient
    private String softwareNumber; 
    
    @Transient
    private String softwareSize;
    
    @Transient
    private String tool;
    
    @Transient
    private String status;
    
    @Transient
    private String originalChecksum;
    
    @Transient
    private String gearType;
    
    @Transient
    public boolean isCmdEncryptedProduct=false;
    
    @Transient
    private String channel;
    
    @Transient
    private String ecuPartNumber;
    
    @Transient
    private String ecuHardwareNumber;
    
    @Transient
    private String ecuSoftwareNumber;
    
    @Transient
    private String ecuSoftwareVersion;
    
    @Transient
    private boolean saleAble = true;
    
    private String printableName = "";
    
    void populate(SedoxSharedProduct sharedProduct) {
        this.binaryFiles = sharedProduct.binaryFiles;
        this.filedesc = sharedProduct.filedesc;
        this.brand = sharedProduct.brand;
        this.model = sharedProduct.model;
        this.engineSize = sharedProduct.engineSize;
        this.year = sharedProduct.year;
        this.power = sharedProduct.power;
        this.ecuType = sharedProduct.ecuType;
        this.build = sharedProduct.build;
        this.ecuBrand = sharedProduct.ecuBrand;
        this.softwareNumber = sharedProduct.softwareNumber; 
        this.softwareSize = sharedProduct.softwareSize;
        this.tool = sharedProduct.tool;
        this.status = sharedProduct.status;
        this.originalChecksum = sharedProduct.originalChecksum;
        this.gearType = sharedProduct.gearType;
        this.isCmdEncryptedProduct = sharedProduct.isCmdEncryptedProduct;
        this.channel = sharedProduct.channel;
        this.ecuPartNumber = sharedProduct.ecuPartNumber;
        this.ecuHardwareNumber = sharedProduct.ecuHardwareNumber;
        this.ecuSoftwareNumber = sharedProduct.ecuSoftwareNumber;
        this.ecuSoftwareVersion = sharedProduct.ecuSoftwareVersion;
        this.saleAble = sharedProduct.saleAble;
        this.printableName = sharedProduct.getName();
    }
}
