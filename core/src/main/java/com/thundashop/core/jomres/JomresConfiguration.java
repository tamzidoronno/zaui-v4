package com.thundashop.core.jomres;

import com.thundashop.core.common.DataCommon;

public class JomresConfiguration extends DataCommon {
    String cmfClientTokenUrl = "";
    String clientBaseUrl = "";
    String cmfRestApiClientId = "";
    String cmfRestApiClientSecret = "";
    String channelName = "";
    void updateConfiguration(JomresConfiguration newConfiguration){
        cmfClientTokenUrl = newConfiguration.cmfClientTokenUrl;
        clientBaseUrl = newConfiguration.clientBaseUrl;
        cmfRestApiClientId = newConfiguration.cmfRestApiClientId;
        cmfRestApiClientSecret = newConfiguration.cmfRestApiClientSecret;
        channelName = newConfiguration.channelName;
    }
}