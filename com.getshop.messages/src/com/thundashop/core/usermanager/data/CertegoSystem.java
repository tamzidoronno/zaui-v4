/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class CertegoSystem implements GroupInformation {
    public String id = "";
    public String name = "";

    @Override
    public void setId(String id) {
        id = UUID.randomUUID().toString();
    }

    @Override
    public String getId() {
        return id;
    }
}
