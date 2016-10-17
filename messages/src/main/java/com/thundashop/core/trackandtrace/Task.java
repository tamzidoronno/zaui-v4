/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.usermanager.data.Company;
import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class Task implements Serializable {
    public String id = UUID.randomUUID().toString();
    
}