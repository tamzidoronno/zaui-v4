/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.meca;

import com.thundashop.core.mecamanager.MecaCarRequestKilomters;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author ktonder
 */
public class TestMecaCarRequest {
    
    @Test
    public void returnTrueFirstTime() {
        MecaCarRequestKilomters res = new MecaCarRequestKilomters();
        Assert.assertTrue(res.canSendPushNotification());        
    }
    
    @Test
    public void dontSendNotificationIfKilomtersHasJustBeenMarkedAsReceived() {
        MecaCarRequestKilomters res = new MecaCarRequestKilomters();
        res.markReceivedKilomters();
        Assert.assertFalse(res.canSendPushNotification());        
    }
    
    @Test
    public void sendNotificationIfLastReceivedAndLastedPushedMoreThenOneMonth() {
        MecaCarRequestKilomters res = new MecaCarRequestKilomters();
        res.setLastReceivedKilomters(getMonthOldDate(0));
        res.setRequestedLastTime(getMonthOldDate(0));
        Assert.assertTrue(res.canSendPushNotification());
    }
    
    @Test
    public void testThatNotSendingPushNotificationIfJustMarkedAsSent() {
        MecaCarRequestKilomters res = new MecaCarRequestKilomters();
        res.setLastReceivedKilomters(getMonthOldDate(0));
        res.markAsSentPushNotification();
        Assert.assertFalse(res.canSendPushNotification());
    }
    
    @Test
    public void testThatCanReSendPushNotification() {
        MecaCarRequestKilomters res = new MecaCarRequestKilomters();
        res.setLastReceivedKilomters(getMonthOldDate(2));
        res.setRequestedLastTime(getOneDayOldDate(2));
        boolean resBool = res.canSendPushNotification();
        Assert.assertTrue(resBool);
    }
    
    @Test
    public void testThatCanReSendPushNotificationFails() {
        MecaCarRequestKilomters res = new MecaCarRequestKilomters();
        res.setLastReceivedKilomters(getMonthOldDate(2));
        res.setRequestedLastTime(getOneDayOldDate(2));
        boolean resBool = res.canSendPushNotification();
        Assert.assertTrue(resBool);
        res.markAsSentPushNotification();
        Assert.assertFalse(res.canSendPushNotification());
    }
    
    @Test
    public void smsCanSendTest() {
        MecaCarRequestKilomters res = new MecaCarRequestKilomters();
        res.setLastReceivedKilomters(getMonthOldDate(8));
        res.setRequestedLastTime(getOneDayOldDate(8));
        boolean resBool = res.canSendSmsNotification();
//        Assert.assertTrue(resBool);
//        res.markAsSentSmsNotification();
//        Assert.assertFalse(res.canSendSmsNotification());
    }
    
    @Test
    public void smsCanNotSendTest() {
        MecaCarRequestKilomters res = new MecaCarRequestKilomters();
        res.setLastReceivedKilomters(getMonthOldDate(5));
        res.setRequestedLastTime(getOneDayOldDate(5));
        boolean resBool = res.canSendSmsNotification();
        Assert.assertFalse(resBool);
    }

    private Date getMonthOldDate(int extradays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1);
        cal.add(Calendar.DAY_OF_MONTH, -extradays);
        return cal.getTime();
    }

    private Date getOneDayOldDate(int extradays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -extradays);
        return cal.getTime();
    }
}

