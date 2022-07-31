package com.thundashop.core.paymentmanager;

import com.getshop.bookingengine.BookingEngineTestContext;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.paymentmanager.EasyByNets.DTO.RetrievePayment;
import com.thundashop.core.paymentmanager.EasyByNets.EasyByNetService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BookingEngineTestContext.class})
public class EasyByNetTest {
    // To run this tests, a mongo needs to be run on host: localhost and port: 27019

    //@Autowired private EasyByNetService easyByNetService;

    @Test
    public void testGetNextIncrementalStoreId() {
        RetrievePayment pay = new RetrievePayment();
       // TODO initialize StorePool pay = easyByNetService.retrievePayment("sss");
        assertNotNull(pay);
    }


}