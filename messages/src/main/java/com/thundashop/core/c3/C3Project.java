/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class C3Project extends DataCommon {
    public String name = "";
    public String projectNumber = "";
    public String projectOwner = "";
    public List<String> workPackages = new ArrayList();
}
