package com.thundashop.core.storemanager;

import com.thundashop.core.storemanager.data.Store;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StoreIdRepositoryTestContext.class})
public class StoreIdRepositoryTest {
    // To run this tests, a mongo needs to be run on host: localhost and port: 27019

    @Autowired
    ExtendedDatabase3 extendedDatabase3;

    public void insert(final int count) {
        for (int i = 0; i < count; i++) {
            Store store = new Store();
            store.id = UUID.randomUUID().toString();
            store.incrementalStoreId = i + 1;
            extendedDatabase3.save("StoreManager", "all", store);
        }
    }

    @After
    public void after() {
        extendedDatabase3.dropDatabase("StoreManager");
    }

    @Test
    public void testGetNextIncrementalStoreId() {
        insert(2);
        StoreIdRepository storeIdRepository = new StoreIdRepository(extendedDatabase3);

        int nextIncrementalStoreId = storeIdRepository.getNextIncrementalStoreId();

        Assert.assertEquals(3, nextIncrementalStoreId);
    }

    @Test
    public void testReturn1WhenDatabaseIsEmpty() {
        StoreIdRepository storeIdRepository = new StoreIdRepository(extendedDatabase3);

        int nextIncrementalStoreId = storeIdRepository.getNextIncrementalStoreId();

        Assert.assertEquals(1, nextIncrementalStoreId);
    }

    @Test
    public void testMultipleSameIncrementalStoreId() {
        // insert multiple document with same incrementalStoreId
        insert(2);
        insert(2);
        StoreIdRepository storeIdRepository = new StoreIdRepository(extendedDatabase3);

        int nextIncrementalStoreId = storeIdRepository.getNextIncrementalStoreId();

        Assert.assertEquals(3, nextIncrementalStoreId);
    }

    @Test
    public void testWhenCurrentIncrementalStoreId3ThenReturn4() {
        insert(3);
        StoreIdRepository storeIdRepository = new StoreIdRepository(extendedDatabase3);

        int nextIncrementalStoreId = storeIdRepository.getNextIncrementalStoreId();

        Assert.assertEquals(4, nextIncrementalStoreId);
    }
}