/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.GetShopLogHandler;
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
            GetShopLogHandler.logPrintStatic("WARNING! Cant update attributes, the filename is not the correct parameters. Check winols settings", null);
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
            if (type.equals("car")) {
                return Double.parseDouble(sedoxUser.fixedPrice);
            }
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
            return 6 + getAddons(type);
        }
        
        if (type.equals("tractor")) {
            return 11 + getAddons(type);
        }
        
        if (type.equals("truck")) {
            return 16 + getAddons(type);
        }
        
        GetShopLogHandler.logPrintStatic("Warning, file price is not registered to this file", null);
        return 6;
    }

    private int getAddons(String type) {
        int addon = 0;
        
        if (fileType != null && fileType.toLowerCase().equals("other"))
            return 0;
        
        if (options.requested_dpf && type.equals("car"))
            addon += 5;
        
        if (options.requested_adblue && type.equals("car"))
            addon += 5;
        
        if (options.requested_dpf && type.equals("truck"))
            addon += 10;
        
        if (options.requested_adblue && type.equals("truck"))
            addon += 10;
        
        if (options.requested_dpf && type.equals("tractor"))
            addon += 10;
        
        if (options.requested_adblue && type.equals("tractor"))
            addon += 10;
        
        return addon;
    }

    String getExtraInfo() {
        if (options == null)
            return "";
        
        String toAdd = "";
        
        if (options.requested_adblue)
            toAdd += "NOADBLUE,";
        
        if (options.requested_decat)
            toAdd += "NODECAT,";
        
        if (options.requested_dpf)
            toAdd += "NODPF,";
        
        if (options.requested_dtc)
            toAdd += "NODTC,";
        
        if (options.requested_egr)
            toAdd += "NOEGR,";
        
        if (options.requested_vmax)
            toAdd += "NOVMAX,";
        
        if (toAdd.isEmpty())
            return "";
        
        return "(" + toAdd.substring(0,toAdd.length()-1) + ")";
    }
}
