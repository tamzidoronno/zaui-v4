/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.scormmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class Scorm extends DataCommon {
    public String scormId = "";
    public String userId = "";
    public int score = 0;
    public boolean completed = false;
    public boolean passed;
    public boolean failed;
    public Date passedDate = null;
    
    @Transient
    public String scormName = "";
    
    @Transient
    public boolean groupedScormPackage = false;
    
    @Transient
    public List<String> groupedScormPackageIds = new ArrayList();

    public boolean isPartOfOtherGroupScormPackages(Collection<ScormPackage> packages) {
        return packages.stream().filter(o -> o.groupedScormPackages.contains(scormId)).count() > 0;
    }    
}
