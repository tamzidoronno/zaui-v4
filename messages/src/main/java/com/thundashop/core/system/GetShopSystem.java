package com.thundashop.core.system;


import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ktonder
 */
public class GetShopSystem extends DataCommon {
    public String systemName = "";
    public String serverVpnIpAddress = "";
    public String webAddresses = "";
    public String companyId = "";
    public String remoteStoreId = "";
    
    public Date activeFrom;
    public Date activeTo;
    
    public double monthlyPrice = 0;
    public String productId = "";
}