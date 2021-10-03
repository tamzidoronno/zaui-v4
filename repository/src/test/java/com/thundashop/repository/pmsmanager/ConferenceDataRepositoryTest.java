package com.thundashop.repository.pmsmanager;

import com.google.gson.reflect.TypeToken;
import com.thundashop.core.pmsmanager.ConferenceData;
import com.thundashop.repository.TestCommon;
import com.thundashop.repository.common.SessionInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.UUID;

import static com.thundashop.repository.testutils.JsonUtils.toPojo;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;

class ConferenceDataRepositoryTest extends TestCommon {

    static final String dbName = "ConferenceDataTest" + randomAlphabetic(5);
    final Type type = new TypeToken<ConferenceData>() {
    }.getType();

    static ConferenceDataRepository conferenceDataRepository;

    SessionInfo sessionInfo;

    @BeforeAll
    static void setUp() {
        init();
        conferenceDataRepository = new ConferenceDataRepository(database, dbName, ConferenceData.class.getName());
    }

    @BeforeEach
    void beforeEach() {
        sessionInfo = SessionInfo.builder()
                .setStoreId(UUID.randomUUID().toString())
                .setCurrentUserId("testUserId")
                .setLanguage("en")
                .build();
    }

    @AfterEach
    void afterEach() {
        database.dropDatabase(dbName);
    }

    @Test
    void save() {
        ConferenceData conferenceData = toPojo(type, "src/test/resources/pmsmanager/conferencedata/conferenceData.json");

        ConferenceData expected = (ConferenceData) conferenceDataRepository.save(conferenceData, sessionInfo);
        Optional<ConferenceData> actual = conferenceDataRepository.findById(expected.id, ConferenceData.class, sessionInfo);

        assertThat(actual).isNotEmpty()
                .map(it -> it.id)
                .contains(expected.id);

        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void findByBookingId() {
        final String bookingId = UUID.randomUUID().toString();
        final ConferenceData conferenceData = toPojo(type, "src/test/resources/pmsmanager/conferencedata/conferenceData.json");
        conferenceData.bookingId = bookingId;

        conferenceDataRepository.save(conferenceData, sessionInfo);
        Optional<ConferenceData> actual = conferenceDataRepository.findByBookingId(bookingId, sessionInfo);

        assertThat(actual).isNotEmpty()
                .map(it -> it.bookingId)
                .contains(bookingId);
    }

    @Test
    void returnOptionalEmptyIfBookingIdNotFound() {
        final String bookingId = UUID.randomUUID().toString();
        final ConferenceData conferenceData = toPojo(type, "src/test/resources/pmsmanager/conferencedata/conferenceData.json");
        conferenceData.bookingId = bookingId;

        conferenceDataRepository.save(conferenceData, sessionInfo);
        Optional<ConferenceData> actual = conferenceDataRepository.findByBookingId(UUID.randomUUID().toString(), sessionInfo);

        assertThat(actual).isEmpty();
    }

    @Test
    void updateWhenIdIsSame() {
        final ConferenceData conferenceData1 = toPojo(type, "src/test/resources/pmsmanager/conferencedata/conferenceData.json");
        final ConferenceData conferenceData2 = toPojo(type, "src/test/resources/pmsmanager/conferencedata/conferenceData1.json");
        final ConferenceData conferenceData3 = toPojo(type, "src/test/resources/pmsmanager/conferencedata/conferenceData1.json");

        ConferenceData previouslySaved = (ConferenceData) conferenceDataRepository.save(conferenceData1, sessionInfo);
        conferenceData2.id = previouslySaved.id;

        ConferenceData actual = (ConferenceData) conferenceDataRepository.save(conferenceData2, sessionInfo);

        // these are the fields set while saving. need to test separately.
        assertThat(actual).isNotNull()
                .extracting(it -> it.id, it -> it.storeId, it -> it.gs_manager)
                .containsExactly(previouslySaved.id, previouslySaved.storeId, previouslySaved.gs_manager);

        assertThat(actual).usingRecursiveComparison()
                // these fields are asserted previously. ignoring for now.
                .ignoringFields("id", "lastModifiedByUserId", "gs_manager", "lastModified", "rowCreatedDate", "storeId")
                .isEqualTo(conferenceData3);
    }
}