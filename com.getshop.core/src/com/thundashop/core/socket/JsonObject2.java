package com.thundashop.core.socket;

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
    public Map<String,String> args;
    
    public String addr;
}
