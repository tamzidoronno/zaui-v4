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

    void updateParametersFromFileName(String fileName) {
        String[] productAttributes = fileName.split(";");
        
        if (productAttributes.length != 16) {
            System.out.println("WARNING! Cant update attributes, the filename is not the correct parameters. Check winols settings");
            return;
        }
        
        checksumCorrected = !productAttributes[15].equals("csnone");
    }

    public double getPrice() {
        
        if (fileType.toLowerCase().equals("tune")) {
            return 60;
        }
        
        if (fileType.toLowerCase().equals("original")) {
            return 0;
        }
        
        if (fileType.toLowerCase().equals("power")) {
            return 70;
        }
        
        if (fileType.toLowerCase().equals("eco")) {
            return 50;
        }
        
        if (fileType.toLowerCase().equals("various")) {
            return 110;
        }
        
        if (fileType.toLowerCase().equals("cmdencrypted")) {
            return 0;
        }
        
        System.out.println("Warning, file price is not registered to this file");
        return 60;
    }
}
