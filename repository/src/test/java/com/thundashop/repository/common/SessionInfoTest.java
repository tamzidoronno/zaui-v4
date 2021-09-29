package com.thundashop.repository.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessionInfoTest {

    String storeId;
    String currentId;
    String language;

    @BeforeEach
    void setUp() {
        storeId = UUID.randomUUID().toString();
        currentId = UUID.randomUUID().toString();
        language = "testLang";
    }

    @Test
    void testEmptyBuilder() {
        SessionInfo sessionInfo = SessionInfo.builder()
                .build();

        assertThat(sessionInfo.getStoreId()).isEmpty();
        assertThat(sessionInfo.getCurrentUserId()).isEmpty();
        assertThat(sessionInfo.getLanguage()).isEmpty();
    }

    @Test
    void testWithSingleValue() {
        SessionInfo sessionInfo = SessionInfo.builder()
                .setStoreId(storeId)
                .build();

        assertThat(sessionInfo.getStoreId()).isEqualTo(storeId);
        assertThat(sessionInfo.getCurrentUserId()).isEmpty();
        assertThat(sessionInfo.getLanguage()).isEmpty();
    }

    @Test
    void testWithAllValue() {
        SessionInfo sessionInfo = SessionInfo.builder()
                .setStoreId(storeId)
                .setCurrentUserId(currentId)
                .setLanguage(language)
                .build();

        assertThat(sessionInfo.getStoreId()).isEqualTo(storeId);
        assertThat(sessionInfo.getCurrentUserId()).isEqualTo(currentId);
        assertThat(sessionInfo.getLanguage()).isEqualTo(language);
    }

    @Test
    void testEmpty() {
        SessionInfo sessionInfo = SessionInfo.empty();

        assertThat(sessionInfo.getStoreId()).isEmpty();
        assertThat(sessionInfo.getCurrentUserId()).isEmpty();
        assertThat(sessionInfo.getLanguage()).isEmpty();
    }
}