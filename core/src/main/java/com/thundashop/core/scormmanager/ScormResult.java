/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.scormmanager;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author ktonder
 */
public class ScormResult {
    public String username = "";
    
    public String scormid = ""; 
    
    @SerializedName(value = "cmi.core.lesson_status")
    public String status = "";
    
    @SerializedName(value = "cmi.core.score.raw")
    public String score = "";
    
    public boolean isCompleted() {
        return status.equals("completed") || status.equals("passed") || status.equals("failed");
    }
}
