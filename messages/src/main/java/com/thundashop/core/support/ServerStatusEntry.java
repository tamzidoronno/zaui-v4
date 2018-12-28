/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.support;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author boggi
 */
public class ServerStatusEntry implements Serializable {
    public String hostname = "";
    public String givenName = "";
    public String id = "";
    public String storeId = "";
    public Integer status = 0;
    public Date lastPing = null;
    public String webaddr = "";
}
