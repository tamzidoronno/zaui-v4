/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.Date;

/**
 *
 * @author boggi
 */
public class PmsEventEntry {
    public Date from;
    public Date to;
    public String text;
    public Integer count = 0;
    public String extendedText = "";
    public String pmsEventId = "";
}
