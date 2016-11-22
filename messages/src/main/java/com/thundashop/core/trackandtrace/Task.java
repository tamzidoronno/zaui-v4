/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class Task extends DataCommon {
    public boolean completed = false;
    public String exceptionId = "";
    public String comment;
}