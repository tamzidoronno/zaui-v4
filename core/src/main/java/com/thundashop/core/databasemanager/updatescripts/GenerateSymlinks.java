/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.appmanager.data.Application;
import java.net.UnknownHostException;
import java.util.List;

public class GenerateSymlinks extends UpgradeBase {
    
    public static void main(String[] args) throws UnknownHostException {
         GenerateSymlinks gen = new GenerateSymlinks();
         gen.printSymlinks();
    }

    private void printSymlinks() throws UnknownHostException {
        List<Application> allApps = getAllAppSettings();
        for(Application setting : allApps) {
            String ns = "ns_" + setting.id.replace("-", "_");
            System.out.println("ln -s ../apps/"+setting.appName+" "+ ns + ";");
        }
    }
    
}
