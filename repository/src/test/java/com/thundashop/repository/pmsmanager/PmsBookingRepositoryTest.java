package com.thundashop.repository.pmsmanager;


import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import com.thundashop.repository.TestCommon;
import com.thundashop.repository.utils.SessionInfo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PmsBookingRepositoryTest extends TestCommon {

    static final String dbName = "PmsBookingTest" + randomAlphabetic(5);

    SessionInfo sessionInfo;

    private static IPmsBookingRepository repository;

    @BeforeAll
    static void setUp() {
        init();
        repository = new PmsBookingRepository(database);
    }

    @BeforeEach
    void beforeEach() {
        sessionInfo = buildSessionInfo(dbName);
    }

    @AfterEach
    void afterEach() {
        database.dropDatabase(dbName);
    }

    @Test
    void findBySessionId() {
    }

    @Test
    void findByPmsBookingRoomId() {
    }
}