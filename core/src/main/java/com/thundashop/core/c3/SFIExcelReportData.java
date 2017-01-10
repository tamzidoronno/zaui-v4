/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class SFIExcelReportData {
    public String nameOfPartner = "TEST";
    public String delprosjekter = "WP0,WP4,WP4,WP9";
    public String periode = "2015";
    public List<SFIExcelReportDataPost11> post11 = new ArrayList();
    public List<SFIExcelReportDataPost13> post13 = new ArrayList();
    public List<SFIExcelReportDataPost14> post14 = new ArrayList();
    public String date = "12/5-2016";
    public String ansvarlig = "";
    public String wpId;
    
    public SFIExcelReportData() {
    }

    public double getTotal() {
        double total = 0;
        total += post11.stream().mapToDouble(post -> post.totalt).sum();
        total += post13.stream().mapToDouble(post -> post.totalt).sum();
        total += post14.stream().mapToDouble(post -> post.totalt).sum();
        
        return Math.round(total);
    }

    public double getTotalInkind() {
        double total = 0;
        total += post11.stream().mapToDouble(post -> post.inkind).sum();
        total += post13.stream().mapToDouble(post -> post.inkind).sum();
        total += post14.stream().mapToDouble(post -> post.inkind).sum();
        
        return Math.round(total);
    }
    
    
}


