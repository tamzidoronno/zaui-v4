/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class QuestBackImportRow {
    public List<QuestBackImportRow> children = new ArrayList();
    public String col1;
    public String col2;
    public String col3;
    public String col4;
    public String col5;
    
    public String toString() {
        return col1 + " " + col2 + " " + col3 + " " + col4 + " " + col5;
    }
}
