/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class ExportedData extends DataCommon {
    public List<AcculogixExport> exportedData = new ArrayList();
    public int exportSequence;
    public String routeId;
    
    public int getCount() {
        return exportedData.size();
    }
}
