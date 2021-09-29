package com.thundashop.repository.db;

import com.mongodb.BasicDBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.repository.testutils.TestConfig;
import com.thundashop.repository.utils.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;

class DatabaseTest {

    private static final String testDbName = "DatabaseTest" + randomAlphabetic(5);

    private static final String testCollection = "DatabaseTest_Collection";

    static Database database;

    @BeforeAll
    static void setUp() {
        Config config = TestConfig.newInstance();
        database = Database.of(config.getAsString("mongo.host"), config.getAsInt("mongo.port"));
    }

    @AfterEach
    void afterEach() {
        database.dropDatabase(testDbName);
    }

    @Test
    void testQueryById() {
        String id = UUID.randomUUID().toString();
        DbTest expected = new DbTest();
        expected.id = id;
        expected.setStrMatch("123");

        DbTest extra = new DbTest();
        extra.id = UUID.randomUUID().toString();
        extra.setStrMatch("456");

        database.save(testDbName, testCollection, expected);
        database.save(testDbName, testCollection, extra);

        BasicDBObject query = new BasicDBObject();
        query.put("_id", id);

        List<DataCommon> result = database.query(testDbName, testCollection, query);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);

        DbTest actual = (DbTest) result.get(0);

        assertThat(actual.id).isEqualTo(expected.id);
        assertThat(actual.getStrMatch()).isEqualTo(expected.getStrMatch());
    }

    @Test
    void testQueryOrderAndLimit() {
        DbTest expected1 = new DbTest();
        expected1.id = UUID.randomUUID().toString();
        expected1.setStrMatch("123");
        expected1.setOrder(1);

        DbTest expected2 = new DbTest();
        expected2.id = UUID.randomUUID().toString();
        expected2.setStrMatch("456");
        expected2.setOrder(2);

        DbTest expected3 = new DbTest();
        expected3.id = UUID.randomUUID().toString();
        expected3.setStrMatch("789");
        expected3.setOrder(3);

        database.save(testDbName, testCollection, expected1);
        database.save(testDbName, testCollection, expected2);
        database.save(testDbName, testCollection, expected3);

        BasicDBObject sort = new BasicDBObject();
        sort.put("order", -1);
        int expectedLimit = 2;

        List<DataCommon> result = database.query(testDbName, testCollection, new BasicDBObject(), sort, expectedLimit);

        assertThat(result).isNotEmpty().size().isEqualTo(expectedLimit);

        List<DbTest> dbTestList = result.stream()
                .map(i -> (DbTest) i)
                .collect(toList());

        assertThat(dbTestList).extracting(DbTest::getOrder)
                .containsExactly(expected3.getOrder(), expected2.getOrder());
    }
}