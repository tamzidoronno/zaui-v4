/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.settingsmanager;

import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ISettingsManager {
    
    @Administrator
    public List<ApplicationSettings> getDashBoardApplications() throws ErrorException;
	
	
}
