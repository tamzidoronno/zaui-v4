/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import com.thundashop.core.common.DataCommon;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class MecaCar extends DataCommon {
    public String licensePlate = "";
    
    public Date prevControll = null;
    public int kilometers = 0;
    public String cellPhone = "";

    public String pageId;
    
    public Date lastService;
    public Integer lastServiceKilomters;
    
    public int monthsBetweenServices = 18;
    public int kilometersBetweenEachService = 10000;
    
    @Transient
    public Date nextService = null;
    
    @Transient
    public Integer nextServiceKilometers = null;
    
    @Transient
    public Integer kilometersToNextService = null;
    
    @Transient
    public Integer monthsToNextService = 0;
    
    
    @Transient
    public Date nextControll = null;

    /**
     * Caluclate the next EU Controll, service date etc.
     */
    public void calculateNextValues() {
        if (lastServiceKilomters != null) {
            nextServiceKilometers = lastServiceKilomters + kilometersBetweenEachService;
            kilometersToNextService = nextServiceKilometers - kilometers;
            monthsToNextService = getMonthsBetweenService();
        }
        
        if (prevControll != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(prevControll);
            cal.add(Calendar.MONTH, 24);
            nextControll = cal.getTime();
        }
        
        if (lastService != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(lastService);
            cal.add(Calendar.MONTH, monthsBetweenServices);
            nextService = cal.getTime();
        }
        
        
    }

    private Integer getMonthsBetweenService() {
        if (nextService == null) {
            return 0;
        }
        
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(new Date());
        
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(nextService);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        
        return diffMonth;
    }
}
