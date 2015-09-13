/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.thundashop.core.common.ManagerSubBase;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ktonder
 */
public class GetShopSessionBeanNamed extends ManagerSubBase {
    
    @Autowired
    public GetShopSessionBeanNamed(String name) {
        this.name = name;
    }
    
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String getSessionBasedName() {
        return name;
    }
}
