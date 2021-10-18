package com.thundashop.repository.db;

import com.mongodb.BasicDBObject;
import com.thundashop.repository.testutils.TestConfig;
import com.thundashop.repository.utils.Config;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;

class DatabaseTest {

    private static final String testDbName = "DatabaseTest" + randomAlphabetic(5);

    private static final String testCollection = "DatabaseTest_Collection";

    static Database database;

    @BeforeAll
    static void setUp() {
        Config config = TestConfig.newInstance();
        database = Database.of(config.getAsString("mongo.host"),
                config.getAsInt("mongo.port"), new DbTestEntityMappers());
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

        List<DbTest> result = database.query(testDbName, testCollection, DbTest.class, query);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);

        DbTest actual = result.get(0);

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

        List<DbTest> result = database.query(testDbName, testCollection, DbTest.class, new BasicDBObject(), sort, expectedLimit);

        assertThat(result).isNotEmpty().size().isEqualTo(expectedLimit);
        assertThat(result).extracting(DbTest::getOrder)
                .containsExactly(expected3.getOrder(), expected2.getOrder());
    }

    @Test
    void testFullPathClassName() {
        Assertions.assertThat(DbTest.class.getName()).isEqualTo("com.thundashop.repository.db.DbTest");
    }

    @Test
    void update() {
        // setup
        DbTest dbTest = new DbTest(UUID.randomUUID().toString(), "code_1");
        database.save(testDbName, testCollection, dbTest);

        // when
        BasicDBObject query = new BasicDBObject("strMatch", "code_1");
        BasicDBObject updateField = new BasicDBObject("testDate", new Date()).append("order", 100);
        BasicDBObject setQuery = new BasicDBObject("$set", updateField);

        database.update(testDbName, testCollection, query, setQuery);

        // then
        List<DbTest> list = database.query(testDbName, testCollection, DbTest.class, new BasicDBObject("_id", dbTest.id));
        assertThat(list).isNotEmpty();

        DbTest actual = list.get(0);
        assertThat(actual.id).isEqualTo(dbTest.id);
        assertThat(actual.getTestDate()).isNotNull();
        assertThat(actual.getOrder()).isEqualTo(100);
    }
}