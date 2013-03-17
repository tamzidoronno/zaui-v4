/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.appmanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class AvailableApplications extends DataCommon {
    public List<ApplicationSettings> addedApplications = new ArrayList();
    public List<ApplicationSettings> applications = new ArrayList();
}
