/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import java.util.Date;

/**
 *
 * @author ktonder
 */
public interface UpdateScript {
    public void run();
    public String getId();
    Date getAddedDate();
}
