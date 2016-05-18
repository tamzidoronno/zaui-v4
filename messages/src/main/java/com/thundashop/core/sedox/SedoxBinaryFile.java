/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import java.io.Serializable;

/**
 *
 * @author ktonder
 */
public class SedoxBinaryFile implements Serializable {
    
    public int id;
    public String md5sum;
    
    /**
     * Original
     * Tune
     * CmdEncrypted
     * ECO
     * Encrypted
     * Various
     * Power
     */
    public String fileType;
    public Boolean checksumCorrected;
    public String orgFilename;
    public String extraInformation;
    public String additionalInformation;
    public String cmdFileType;

    public SedoxBinaryFileOptions options = new SedoxBinaryFileOptions();
    
    void updateParametersFromFileName(String fileName) {
        String[] productAttributes = fileName.split(";");
        
        if (productAttributes.length != 17 && productAttributes.length != 18) { 
            System.out.println("WARNING! Cant update attributes, the filename is not the correct parameters. Check winols settings");
            return;
        }
        
        checksumCorrected = !productAttributes[16].equals("csnone"); //% ECU.Checksumstatus % (checksum is corrected or not)
    }

    public double getPrice(SedoxUser sedoxUser, String type) {
        
        if (sedoxUser.fixedPrice != null 
                && !sedoxUser.fixedPrice.isEmpty() 
                && !fileType.toLowerCase().equals("cmdencrypted") 
                && !fileType.toLowerCase().equals("cmd original") 
                && !fileType.toLowerCase().equals("original") 
                ) {
            return Double.parseDouble(sedoxUser.fixedPrice);
        }
        
        if (fileType.toLowerCase().equals("original")) {
            return 0;
        }
        
        if (fileType.toLowerCase().equals("cmdencrypted")) {
            return 0;
        }
        
        if (fileType.toLowerCase().equals("cmd original")) {
            return 0;
        }
        
        if (type.equals("car") || type.equals("boat")) {
            return 60 + getAddons(type);
        }
        
        if (type.equals("tractor")) {
            return 110 + getAddons(type);
        }
        
        if (type.equals("truck")) {
            return 160 + getAddons(type);
        }
        
        System.out.println("Warning, file price is not registered to this file");
        return 60;
    }

    private int getAddons(String type) {
        int addon = 0;
        
        if (options.requested_dpf && type.equals("car"))
            addon += 50;
        
        if (options.requested_adblue && type.equals("car"))
            addon += 50;
        
        if (options.requested_dpf && type.equals("truck"))
            addon += 100;
        
        if (options.requested_adblue && type.equals("truck"))
            addon += 100;
        
        if (options.requested_dpf && type.equals("tractor"))
            addon += 100;
        
        if (options.requested_adblue && type.equals("tractor"))
            addon += 100;
        
        return addon;
    }
}
