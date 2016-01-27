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
    public String requested_remaptype = "";
}
