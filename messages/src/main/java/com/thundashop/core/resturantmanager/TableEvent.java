/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class TableEvent implements Serializable {
    public Date start;
    public Date end;
    public String tableSessionId;
}
