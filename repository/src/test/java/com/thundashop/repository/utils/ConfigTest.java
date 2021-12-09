package com.thundashop.repository.utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ConfigTest {

    static final String resource = "src/test/resources/configTest.properties";

    @BeforeAll
    static void setUp() throws IOException {
        String str = "testVal=testTest\ntestInt=123";
        Files.write(Paths.get(resource), str.getBytes());
    }

    @AfterAll
    static void afterAll() throws IOException {
        Files.delete(Paths.get(resource));
    }

    @Test
    void testThrowNullPointerExceptionWhenFileIsNull() {
        assertThatNullPointerException().isThrownBy(() -> Config.of(null));
    }

    @Test
    void testGetAsString() {
        Config config = Config.of(new File(resource));

        String actualValue = config.getAsString("testVal");

        assertThat(actualValue).isEqualTo("testTest");
    }

    @Test
    void testGetAsStringNullValue() {
        Config config = Config.of(new File(resource));

        assertThatNullPointerException().isThrownBy(() -> config.getAsString(null));
    }

    @Test
    void testGetAsStringWhenKeyDoesNotExist() {
        Config config = Config.of(new File(resource));

        String keyDoesNotExist = config.getAsString("keyDoesNotExist");

        assertThat(keyDoesNotExist).isNull();
    }


    @Test
    void testGetAsInt() {
        Config config = Config.of(new File(resource));

        Integer actual = config.getAsInt("testInt");

        assertThat(actual).isNotNull().isEqualTo(123);
    }

    @Test
    void testGetAsIntWhenKeyDoesNotExist() {
        Config config = Config.of(new File(resource));

        Integer actual = config.getAsInt("keyDoesNotExist");

        assertThat(actual).isNull();
    }
}