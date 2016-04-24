package com.thundashop.core.common;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author boggi
 */
public class JsonObject2 implements Serializable, Cloneable {
    public String method;
    public String interfaceName;
    public String sessionId;
    public String multiLevelName;
    public Map<String,String> args;
    
    public String addr;
    public String messageId = "";
    public String realInterfaceName;
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
