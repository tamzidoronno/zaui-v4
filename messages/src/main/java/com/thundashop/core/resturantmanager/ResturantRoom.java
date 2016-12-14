/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class ResturantRoom extends DataCommon {

    public String name;
    
    public List<String> tableIds = new ArrayList();
    
    @Transient
    public List<ResturantTable> tables = new ArrayList();
}
