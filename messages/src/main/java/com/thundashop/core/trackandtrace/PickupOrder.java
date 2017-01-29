/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class PickupOrder extends TntOrder {
    public String instruction = "";
    public List<String> barcodeScanned = new ArrayList();
    public int countedBundles = -1;
    public boolean mustScanBarcode;
    public String returnLabelNumber = "";
}
