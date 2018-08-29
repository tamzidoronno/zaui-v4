/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pga;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
class PgaCleaning implements Serializable {
    public Date date;
    public boolean shouldBeCleaned = false;
}
