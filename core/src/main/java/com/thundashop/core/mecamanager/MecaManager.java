/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.meca.data.MecaUser;
import com.thundashop.core.meca.data.RPCResult;
import com.thundashop.core.meca.data.Vehicle;
import com.thundashop.core.meca.data.Workshop;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author emil
 */
@Component
@GetShopSession
public class MecaManager extends ManagerBase implements IMecaApi {

    private Map<String, MecaUser> mecaUsers = new HashMap<String, MecaUser>();
    private Map<String, Workshop> workshops = new HashMap<String, Workshop>();
    
    @Autowired
    private UserManager userManager;
    @Autowired
    private MessageManager messageManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        
        for (DataCommon obj : data.data) {
            // load users
            if (obj instanceof MecaUser) {
                MecaUser mu = (MecaUser) obj;
                mecaUsers.put(mu.getUserId(), mu);
                
                // link meca user to getshop user on transient field
                // just for coding convenience
                User getshopUser = userManager.getUserById(mu.getUserId());
                mu.setGetshopUser(getshopUser);
                if (getshopUser == null) {
                    System.out.println("Found meca user for which there is no getshop user. Id: " + mu.getUserId());
                }
            }
            // load workshops
            if (obj instanceof Workshop) {
                Workshop workshop = (Workshop) obj;
                workshops.put(workshop.id, workshop);
            }
        }
        
    }

    @Override
    public RPCResult createAccount(String phoneNumber) {
        // Validate input
        if (phoneNumber == null || phoneNumber.isEmpty())
            return new RPCResult(RPCResult.ERROR, "phoneNumber is null or empty");
        if (phoneNumber.length() != 8 || phoneNumber.matches("[0-9]+"))
            return new RPCResult(RPCResult.ERROR, "phoneNumber is invalid");
        
        // Check if given phone number doesn't already have an account
        for (MecaUser mecaUser : mecaUsers.values()) {
            if (mecaUser.getGetshopUser() == null) 
                continue;
            if (phoneNumber.equals(mecaUser.getGetshopUser().cellPhone)) {
                return new RPCResult(RPCResult.ERROR, "This phone number already has an account.");
            }
        }
        
        // Generate 4 digit number and set it as temporary password for new user
        String generatedNumber = "9999";
        // Create new user
        User getshopUser = new User();
        getshopUser.id = UUID.randomUUID().toString();
        getshopUser.type = User.Type.CUSTOMER;
        getshopUser.password = generatedNumber;
        getshopUser.storeId = storeId;
        getshopUser.cellPhone = phoneNumber;
        getshopUser = userManager.createUser(getshopUser);

        MecaUser mecaUser = new MecaUser();
        mecaUser.setGetshopUser(getshopUser);
        mecaUser.setUserId(getshopUser.id);
        mecaUser.storeId = storeId;
        
        saveObject(mecaUser);
        
        mecaUsers.put(mecaUser.getUserId(), mecaUser);
        
        // Send temporary password to the given phone number
        // For now everything goes to Emil's phone number
//        messageManager.sendSms("46505705", "Meca here. Your temporary password is: " + generatedNumber);
        
        return new RPCResult();
    }

    @Override
    public RPCResult login(String phoneNumber, String password) {
        // Check if such account exists and try to authenticate
        
        // Check if it is first-time login. If so we should ask to change password.
        
        
        return null;
    }

    @Override
    public RPCResult changePassword(String phoneNumber, String oldPassword, String newPassword1, String newPassword2) {
        // Check if user exists
        
        // First try to authenticate user with old password
        
        // Validate new password
        
        // Set new password
        return null;
    }

    @Override
    public RPCResult addVehicle(String phoneNumber, Vehicle vehicle) {
        return null;
    }

}
