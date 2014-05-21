/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class SedoxBinaryFile implements Serializable {
    public List<SedoxProductAttribute> attribues = new ArrayList();
    
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

    void updateParametersFromFileName(String fileName) {
        String[] productAttributes = fileName.split(";");
        
        if (productAttributes.length != 16) {
            System.out.println("WARNING! Cant update attributes, the filename is not the correct parameters. Check winols settings");
            return;
        }
        
        checksumCorrected = !productAttributes[15].equals("csnone");
    }
}
