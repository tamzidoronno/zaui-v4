/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshop.data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author boggi
 */
public class LeadHistory implements Serializable {
    public String comment = "";
    public Date historyDate = new Date();
    public String userId = "";
    public Integer leadState = 0;
}
