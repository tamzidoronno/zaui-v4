/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.bookingengine;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.BookingEngineAbstract;
import com.thundashop.core.bookingengine.data.BookingEngineConfiguration;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.Credentials;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author ktonder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/All.xml")
public class BookingEngineConfigurationTest extends TestCommon {
    @InjectMocks
    @Spy
    BookingEngineAbstract abstractEngine;

    @InjectMocks
    BookingEngine bookingEngine;

    
    @Test
    public void testIsConfirmationRequired() {
        bookingEngine.setConfirmationRequired(true);
        boolean isConfirmationRequired = bookingEngine.isConfirmationRequired();
        Assert.assertEquals(true, isConfirmationRequired);
        
        Mockito.verify(databaseSaver).saveObject(any(DataCommon.class), any(Credentials.class));
        
        bookingEngine.setConfirmationRequired(false);
        boolean isConfirmationRequired2 = bookingEngine.isConfirmationRequired();
        Assert.assertEquals(false, isConfirmationRequired2);
    }
    
    @Test 
    public void testUsingConfigurationFromDatabase() {
        BookingEngineConfiguration dataObject = new BookingEngineConfiguration(); 
        dataObject.confirmationRequired = true;
        feedDataFromDatabase(abstractEngine, dataObject);
        
        boolean isConfirmationRequired2 = bookingEngine.isConfirmationRequired();
        Assert.assertEquals(true, isConfirmationRequired2);
    }

    
}
