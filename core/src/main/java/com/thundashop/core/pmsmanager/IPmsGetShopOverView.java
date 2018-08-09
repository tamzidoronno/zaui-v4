/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.List;
import javax.xml.ws.soap.Addressing;

/**
 *
 * @author boggi
 */
@GetShopApi
public interface IPmsGetShopOverView {
    @Administrator
    public List<CustomerSetupObject> getCustomerToSetup();
    @Administrator
    public void saveCustomerObject(CustomerSetupObject object);
    @Administrator
    public CustomerSetupObject getCustomerObject(String storeI);
}
