package com.thundashop.core.jomres;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.PermenantlyDeleteData;

@PermenantlyDeleteData
public class JomresConfiguration extends DataCommon {
    String cmfClientTokenUrl = "";
    String clientBaseUrl = "";
    String cmfRestApiClientId = "";
    String cmfRestApiClientSecret = "";
    String channelName = "";
    boolean isEnable = false;
    
    void updateConfiguration(JomresConfiguration newConfiguration){
        cmfClientTokenUrl = newConfiguration.cmfClientTokenUrl;
        clientBaseUrl = newConfiguration.clientBaseUrl;
        cmfRestApiClientId = newConfiguration.cmfRestApiClientId;
        cmfRestApiClientSecret = newConfiguration.cmfRestApiClientSecret;
        channelName = newConfiguration.channelName;
        isEnable = newConfiguration.isEnable;
    }
}