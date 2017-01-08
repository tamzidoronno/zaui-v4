/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.printmanager;

import java.io.Serializable;

/**
 *
 * @author ktonder
 */
public interface Receipt extends Serializable {
    public String getContent();
}
