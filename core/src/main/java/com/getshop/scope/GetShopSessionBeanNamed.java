/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.thundashop.core.common.ManagerSubBase;

/**
 *
 * @author ktonder
 */
public class GetShopSessionBeanNamed extends ManagerSubBase {
    
    public GetShopSessionBeanNamed() {
    }
    
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getSessionBasedName() {
        return name;
    }
}
