/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.scormmanager;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class MoodleScormList {
    private List<MoodlePackage> packages = new ArrayList();

    MoodlePackage get(String scormId) {
        return packages.stream().filter(o -> o.id.equals(scormId)).findFirst().orElse(null);
    }
}
