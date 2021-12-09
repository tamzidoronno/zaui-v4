package com.thundashop.repository.pmsmanager;

import com.thundashop.core.pmsmanager.PmsLog;
import com.thundashop.repository.TestCommon;
import com.thundashop.repository.utils.SessionInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class PmsLogRepositoryTest extends TestCommon {

    private static final String testDbName = "PmsLogManagerTest" + randomAlphabetic(5);

    static PmsLogRepository pmsLogRepository;

    String storeId;

    @BeforeAll
    static void setUp() {
        init();
        pmsLogRepository = new PmsLogRepository(database, testDbName);
    }

    @BeforeEach
    void beforeEach() {
        storeId = UUID.randomUUID().toString();
    }

    @AfterEach
    void afterEach() {
        database.dropDatabase(testDbName);
    }

    @Test
    void testGetDbName() {
        String expectedDbName = "TestDbName";
        PmsLogRepository pmsLogRepository = new PmsLogRepository(null, expectedDbName);

        String actual = pmsLogRepository.getDbName();

        assertThat(actual).isEqualTo(expectedDbName);
    }

    @Test
    void save() {
        Date rowCreated = new Date();
        PmsLog expectedPmsLog = new PmsLog();
        expectedPmsLog.storeId = storeId;
        expectedPmsLog.rowCreatedDate = rowCreated;

        SessionInfo sessionInfo = SessionInfo.builder()
                .setStoreId(storeId)
                .setCurrentUserId("12345")
                .setLanguage("en")
                .build();

        PmsLog actualPmsLog = pmsLogRepository.save(expectedPmsLog, sessionInfo);

        assertThat(actualPmsLog).isNotNull();
        assertThat(actualPmsLog.id).isNotEmpty();
        assertThat((actualPmsLog.storeId)).isEqualTo(expectedPmsLog.storeId);

        assertThat(expectedPmsLog.rowCreatedDate).isEqualTo(rowCreated);
    }

//    @Test
    void query() {
        // TODO: This test should be rewrite. Mark as Ignore for the time being
        // rowCreated value should be set explicitly with proper date time
        System.out.println("Running Ignored Test");
        String roomId = "123";

        PmsLog filter = new PmsLog();
        filter.roomId = roomId;

        PmsLog pmsLog1 = new PmsLog();
        pmsLog1.id = UUID.randomUUID().toString();
        pmsLog1.roomId = roomId;

        PmsLog pmsLog2 = new PmsLog();
        pmsLog2.id = UUID.randomUUID().toString();
        pmsLog2.roomId = roomId;

        PmsLog pmsLog3 = new PmsLog();
        pmsLog3.id = UUID.randomUUID().toString();
        pmsLog3.roomId = "456";

        SessionInfo sessionInfo = SessionInfo.builder()
                .setStoreId(storeId)
                .setCurrentUserId("12345")
                .setLanguage("en")
                .build();

        pmsLogRepository.save(pmsLog1, sessionInfo);
        pmsLogRepository.save(pmsLog2, sessionInfo);
        pmsLogRepository.save(pmsLog3, sessionInfo);

        List<PmsLog> actual = pmsLogRepository.query(filter, sessionInfo);

        assertThat(actual).isNotNull().isNotEmpty();
        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual).extracting(pmsLog -> pmsLog.id, pmsLog -> pmsLog.roomId)
                .containsExactly(tuple(pmsLog2.id, pmsLog2.roomId), tuple(pmsLog1.id, pmsLog1.roomId));
    }
}