/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.io.Serializable;

/**
 *
 * @author boggi
 */
public class RoomCleanedInformation implements Serializable {
    public static class CleaningState {
        public static Integer initial = 0;
        public static Integer isClean = 1;
        public static Integer inUse = 2;
        public static Integer needCleaning = 3;
        public static Integer needIntervalCleaning = 4;
        public static Integer needCleaningCheckedOut = 5;
    }
    String roomId = "";
    Integer cleaningState = 0;
    public boolean hideFromCleaningProgram = false;
}
