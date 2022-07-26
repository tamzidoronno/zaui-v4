package com.thundashop.core.paymentmanager;

import com.thundashop.core.paymentmanager.EasyByNets.EasyByNetService;
import com.thundashop.core.storemanager.StoreIdRepository;
import com.thundashop.core.storemanager.data.Store;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
public class EasyByNetTest {
    // To run this tests, a mongo needs to be run on host: localhost and port: 27019

    @Autowired
    EasyByNetService easyByNetService;

    @Test
    public void testGetNextIncrementalStoreId() {
        easyByNetService.retrievePayment("sss");
        //Assert.assertEquals(3, nextIncrementalStoreId);
    }
}