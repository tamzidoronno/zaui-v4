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
public class OcrWarnings extends DataCommon {
    public List<String> ids = new ArrayList();
    
    public void addId(String id) {
        ids.add(id);
    }
    
    public boolean hasId(String id) {
        return ids.contains(id);
    }
}
