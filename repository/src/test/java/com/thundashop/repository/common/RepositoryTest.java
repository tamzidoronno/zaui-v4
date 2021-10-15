package com.thundashop.repository.common;

import com.google.common.collect.ImmutableList;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.db.DbTest;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;


class RepositoryTest {

    Database mockedDatabase;
    RepositoryTestImpl repositoryTest;

    @BeforeEach
    void beforeEach() {
        mockedDatabase = mock(Database.class);
        repositoryTest = new RepositoryTestImpl(mockedDatabase, "testDbName");
    }

    @Test
    void getSingle() {
        final String id = UUID.randomUUID().toString();
        List<DbTest> list = ImmutableList.of(new DbTest(id));

        Optional<DbTest> actual = repositoryTest.getSingle(list, () -> "Exception Message");

        assertThat(actual).isNotEmpty().map(it -> it.id).contains(id);
    }

    @Test
    void testWhenListSizeMoreThanOneThrowException() {
        List<DbTest> list = ImmutableList.of(new DbTest(), new DbTest());
        String exceptionMessage = "Multiple entity found";

        assertThatThrownBy(() -> repositoryTest.getSingle(list, () -> exceptionMessage))
                .isInstanceOf(NotUniqueDataException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void testWhenEmptyListReturnOptionalEmpty() {
        List<DbTest> list = ImmutableList.of();

        Optional<DbTest> actual = repositoryTest.getSingle(list, () -> "");

        assertThat(actual).isEmpty();
    }

    @Test
    void testWhenResultListParameterIsNullThrowNullPointer() {
        assertThatThrownBy(() -> repositoryTest.getSingle(null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("resultList parameter is null");
    }

    @Test
    void testWhenExceptionMessageParameterIsNullThrowNullPointer() {
        assertThatThrownBy(() -> repositoryTest.getSingle(ImmutableList.of(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("notUniqueExceptionMessage parameter is null");
    }
}