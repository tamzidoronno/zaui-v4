/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mobilemanager.data;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class Token extends DataCommon {
    public String tokenId = "";
    public TokenType type;
    public String appName = "";
    public String userId = "";
    public boolean testMode = false;
}
