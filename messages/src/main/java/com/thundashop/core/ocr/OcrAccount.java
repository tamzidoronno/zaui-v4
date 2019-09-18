/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ocr;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author boggi
 */
public class OcrAccount extends DataCommon {
    public String accountId;
    /**
     * @Deprecated
     * 
     * The lines should be stored directly in the manager in
     * a sepearted map..
     * 
     * Saving all lines into a list in a document is 
     * bad as the ducment size is limited!
     */
    private List<OcrFileLines> lines = new ArrayList();

    public List<OcrFileLines> getLines() {
        return lines;
    }
    
    public boolean hasLines() {
        return !lines.isEmpty();
    }

    void clearLines() {
        lines.clear();
    }
    
}
