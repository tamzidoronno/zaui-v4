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
    public String fileType;
    public boolean checksumCorrected;
    public String orgFilename;
    public String extraInformation;
    public String additionalInformation;
}
