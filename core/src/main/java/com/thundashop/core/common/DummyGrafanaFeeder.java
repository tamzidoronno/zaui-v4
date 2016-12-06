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
public class DummyGrafanaFeeder implements GrafanaFeeder {

    @Override
    public void setDbName(String dbName) {}

    @Override
    public void setPoint(String point) {}

    @Override
    public void setValues(HashMap<String, Object> values) {}

    @Override
    public void run() {}
    
}
