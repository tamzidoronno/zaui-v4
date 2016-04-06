/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.meca.data.RPCResult;
import com.thundashop.core.meca.data.Vehicle;

/**
 *
 * @author emil
 */
@GetShopApi
public interface IMecaApi {
    
    RPCResult createAccount(String phoneNumber);
    RPCResult login(String phoneNumber, String password);
    RPCResult changePassword(String phoneNumber, String oldPassword, String newPassword1, String newPassword2);
    RPCResult addVehicle(String phoneNumber, Vehicle vehicle);
    
}
