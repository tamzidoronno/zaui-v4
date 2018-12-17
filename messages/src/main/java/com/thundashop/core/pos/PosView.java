/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class PosView extends DataCommon {
    public String type;
    public String name;
    
    public List<String> productListsIds = new ArrayList();
    public List<String> tableListIds = new ArrayList();
}
