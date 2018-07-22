/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.applications;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ApplicationInstance;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.Settings;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IStoreApplicationInstancePool {

    @Administrator
    public ApplicationInstance createNewInstance(String applicationId);

    public ApplicationInstance getApplicationInstance(String applicationInstanceId);

    @Administrator
    public ApplicationInstance setApplicationSettings(Settings settings);
    
    @Editor
    public List<ApplicationInstance> getApplicationInstances(String applicationId);
    
    public ApplicationInstance getApplicationInstanceWithModule(String applicationInstanceId, String moduleName);
}
