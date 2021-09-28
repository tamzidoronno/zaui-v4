/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.productmanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class ProductExtraConfig implements Serializable {
    
    public String id = UUID.randomUUID().toString();
    
    /**
     * group, checkbox, etc.
     */
    public String type = "";
    
    public String name = "";
    
    public List<ProductExtraConfigOption> extras = new ArrayList<>();
}

