/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.scormmanager;

import com.thundashop.core.common.DataCommon;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class Scorm extends DataCommon {
    public String scormId = "";
    public String userId = "";
    public int score = 0;
    public boolean completed = false;
    
    @Transient
    public String scormName = "";
}
