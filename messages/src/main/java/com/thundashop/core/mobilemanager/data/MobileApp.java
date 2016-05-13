/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mobilemanager.data;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class MobileApp extends DataCommon {
    
    /**
     * This is the password for the certificate .p12 files.
     */
    @Administrator
    public String iosPassword;
}
