/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class UserProjectAccess {
    public String projectId;
    public List<String> workPackageIds = new ArrayList();
    
    @Transient
    public List<C3Hour> hours = new ArrayList();
    
    @Transient
    public List<C3OtherCosts> otherCosts = new ArrayList();
}
