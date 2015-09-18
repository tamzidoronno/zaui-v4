/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.pullserver;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class JsonObject2 implements Serializable {
    public String method;
    public String interfaceName;
    public String sessionId;
    public String multiLevelName;
    public Map<String,String> args;
    
    public String addr;
    public String messageId = "";
    public String realInterfaceName;
}

