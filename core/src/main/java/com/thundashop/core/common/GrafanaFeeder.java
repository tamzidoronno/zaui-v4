/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.util.HashMap;

/**
 *
 * @author ktonder
 */
public interface GrafanaFeeder extends Runnable {
    public void setDbName(String dbName);

    public void setPoint(String point);

    public void setValues(HashMap<String, Object> values);
}
