/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class QuestTest extends DataCommon {
    public boolean forceCorrectAnswer = false;
    public List<String> userIds = new ArrayList();
    public List<String> questions = new ArrayList();
    public String name = "";
    
    public double redFrom = 0;
    public double redTo = 33;
    public String redText = "";
    
    public double yellowFrom = 33;
    public double yellowTo = 66;
    public String yellowText = "";
    
    public double greenFrom = 66;
    public double greenTo = 100;
    public String greenText = "";

    public QuestTest() {

    }
}