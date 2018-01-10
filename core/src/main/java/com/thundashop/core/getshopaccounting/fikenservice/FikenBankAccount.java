/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting.fikenservice;

import java.io.Serializable;
import org.springframework.hateoas.ResourceSupport;

/**
 *
 * @author ktonder
 */
public class FikenBankAccount extends ResourceSupport implements Serializable {
    private String name;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
