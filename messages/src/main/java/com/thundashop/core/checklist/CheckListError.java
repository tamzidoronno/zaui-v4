/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.checklist;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author ktonder
 */
public class CheckListError implements Serializable {
    public HashMap<String, Object> metaData = new HashMap();
    public String filterType = "";
    public String description = "";
}
