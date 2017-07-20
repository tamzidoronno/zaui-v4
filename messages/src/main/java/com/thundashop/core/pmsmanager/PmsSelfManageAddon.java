/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class PmsSelfManageAddon implements Serializable {
    public String addonId;
    public String productId;
    public String roomId;
    public List<AddonDay> days = new ArrayList();
}
