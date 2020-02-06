/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.central;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class CentralAccessToken extends DataCommon {
    @Administrator
    public String accessToken = UUID.randomUUID().toString()+"_"+UUID.randomUUID().toString()+"_"+UUID.randomUUID().toString();
    
    public String description = "";
}
