/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 *
 * @author ktonder
 */
public class LiAttr implements Serializable {
    public boolean id;
    public String nodeid = "";
    
    @SerializedName("class") 
    public String className = "";
}
