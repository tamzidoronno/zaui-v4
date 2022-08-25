package com.thundashop.repository.common;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.repository.TestCommon;
import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.db.DbTest;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.utils.SessionInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class RepositoryTest extends TestCommon {

    private static final String testDbName = "repositoryTest_" + randomAlphanumeric(5);

    private static IRepository<DbTest> repositoryTest;
    SessionInfo sessionInfo;

    @BeforeAll
    static void setup() {
        init();
        repositoryTest = new RepositoryTestImpl(database);
    }

    @BeforeEach
    void beforeEach() {
        sessionInfo = buildSessionInfo(testDbName);
    }

    @AfterEach
    void afterEach() {
        database.dropDatabase(testDbName);
    }

    @Test
    void getOne() {
        saveTestData("code_1", "code_2", "code_3");
        DBObject query = new BasicDBObject("strMatch", "code_1");

        Optional<DbTest> actual = repositoryTest.getOne(query, sessionInfo);

        assertThat(actual).isNotEmpty().map(DbTest::getStrMatch).contains("code_1");
    }

    @Test
    void testWhenListSizeMoreThanOneThrowException() {
        saveTestData("code_1", "code_2", "code_1");
        DBObject query = new BasicDBObject("strMatch", "code_1");

        assertThatThrownBy(() -> repositoryTest.getOne(query, sessionInfo))
                .isInstanceOf(NotUniqueDataException.class)
                .hasMessage("Found multiple data count: 2 , entity: %s , query: { \"strMatch\" : \"code_1\"}",
                        DbTest.class.getName());
    }

    @Test
    void testWhenEmptyListReturnOptionalEmpty() {
        saveTestData("code_1", "code_2", "code_3");
        DBObject query = new BasicDBObject("strMatch", "code_4");

        Optional<DbTest> actual = repositoryTest.getOne(query, sessionInfo);

        assertThat(actual).isEmpty();
    }

    @Test
    void exist() {
        saveTestData("code_1");
        DBObject query = new BasicDBObject("strMatch", "code_1");

        assertThat(repositoryTest.exist(query, sessionInfo)).isTrue();
    }

    @Test
    void testNotExist() {
        assertThat(repositoryTest.exist(new BasicDBObject("strMatch", "codeShouldNotExist"), sessionInfo)).isFalse();
    }

    private void saveTestData(String... strMatch) {
        Stream.of(strMatch)
                .forEachOrdered(it -> repositoryTest.save(new DbTest(it), sessionInfo));
    }
}