/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import java.io.Serializable;

/**
 *
 * @author ktonder
 */
public class SedoxBinaryFileOptions implements Serializable {
    public boolean requested_dpf = false;
    public boolean requested_egr = false;
    public boolean requested_decat = false;
    public boolean requested_vmax = false;
    public boolean requested_adblue = false;
    public boolean requested_dtc = false;
    public boolean requested_flaps = false;
    public String requested_remaptype = "";

    boolean isEmpty() {
        return !requested_adblue && !requested_decat && !requested_dpf && !requested_dtc && !requested_egr && !requested_vmax;
    }

    @Override
    public String toString() {
        if (isEmpty())
            return "";
        
        String name = "";
        
        if (requested_adblue)
            name += "ADBLUE,";
        
        if (requested_decat)
            name += "DECAT,";
        
        if (requested_dpf)
            name += "DPF,";
        
        if (requested_dtc)
            name += "DTC,";
        
        if (requested_egr)
            name += "EGR,";
        
        if (requested_vmax)
            name += "VMAX,";
        
        name = name.substring(0,name.length()-1);
        
        return name;
    }
}
