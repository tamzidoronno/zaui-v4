/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class QuestionTreeItem {
    public List<QuestionTreeItem> children = new ArrayList();
    public LiAttr li_attr = new LiAttr();
    public String id = "";
    public String text = "";
}