package com.thundashop.core.common;

import com.google.gson.Gson;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author boggi
 */
public class JsonObject2 implements Serializable {
    public String method;
    public String interfaceName;
    public String sessionId;
    public String multiLevelName;
    public String getShopModuleName;
    public Map<String,String> args;
    
    public String addr;
    public String messageId = "";
    public String realInterfaceName;

    String getPrettyPrinted() {
        Gson gson = new Gson();
        
        String res = "";
        res += "Method: " + method + "\n";
        res += "InterfaceName: " + interfaceName + "\n";
        res += "SessionId: " + sessionId + "\n";
        res += "MultiLevelName: " + multiLevelName + "\n";
        res += "Addr: " + addr + "\n";
        res += "MessageId: " + messageId + "\n";
        res += "RealInterfaceName: " + realInterfaceName + "\n";
        res += "\n Args: \n";
        
        if ( args != null) {
            for (String key : args.values()) {
                String val = args.get(key);
                res += key + ": " + gson.toJson(val) + "\n";
            }
        }
        
        return res;
    }
}
