/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class LoginToken extends DataCommon {
    public String accessToken = "";
    public String ipAddress = "";
    public String token = UUID.randomUUID().toString();
    public Date lastUsed = null;
    public String userId = "";
}
