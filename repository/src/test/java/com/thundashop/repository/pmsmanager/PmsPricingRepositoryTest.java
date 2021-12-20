package com.thundashop.repository.pmsmanager;

import com.google.common.collect.ImmutableList;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.repository.TestCommon;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.thundashop.repository.testutils.JsonUtils.toPojo;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PmsPricingRepositoryTest extends TestCommon {

    static final String dbName = "PmsPricingTest" + randomAlphabetic(5);
    static final String jsonPath = "src/test/resources/pmsmanager/pmspricing/";
    final Type type = new TypeToken<PmsPricing>() {
    }.getType();

    SessionInfo sessionInfo;

    static PmsPricingRepository repository;

    @BeforeAll
    static void setUp() {
        init();
        repository = new PmsPricingRepository(database, PmsPricing.class.getName());
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
    void save() {
        PmsPricing pmsPricing = toPojo(type, jsonPath + "pmspricing.json");

        PmsPricing actual = repository.save(pmsPricing, sessionInfo);

        assertThat(actual).isNotNull();
        assertThat(actual.id).isNotEmpty();
        assertThat(actual.lastModifiedByUserId).isNotEmpty();
        assertThat(actual.rowCreatedDate).isInSameMinuteAs(new Date());
        assertThat(actual.lastModified).isInSameMinuteAs(new Date());
        assertThat(actual).extracting(it -> it.className, it -> it.gs_manager, it -> it.colection)
                .containsExactly(PmsPricing.class.getName(), dbName, "col_" + sessionInfo.getStoreId());
    }

    @Test
    void testFindPmsPricingByCode() {
        PmsPricing pmsPricing = repository.save(toPojo(type, jsonPath + "pmspricing.json"), sessionInfo);

        Optional<PmsPricing> actual = repository.findPmsPricingByCode("default", sessionInfo);

        assertThat(actual).isNotEmpty().map(it -> it.id).contains(pmsPricing.id);
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(pmsPricing);
    }

    @Test
    void testGetEmptyOptionalIfCodeNotFound() {
        repository.save(toPojo(type, jsonPath + "pmspricing.json"), sessionInfo);

        Optional<PmsPricing> actual = repository.findPmsPricingByCode("codeShouldNotExist", sessionInfo);

        assertThat(actual).isEmpty();
    }

    @Test
    void testIfMultipleHasSameCodeThenThrowNotUniqueDataException() {
        // db should contains two entity with same 'default' code
        repository.save(toPojo(type, jsonPath + "pmspricing.json"), sessionInfo);
        repository.save(toPojo(type, jsonPath + "pmspricing.json"), sessionInfo);

        assertThatThrownBy(() -> repository.findPmsPricingByCode("default", sessionInfo))
                .isInstanceOf(NotUniqueDataException.class);
    }

    @Test
    void testMarkDeleteByCode() {
        PmsPricing saved = repository.save(toPojo(type, jsonPath + "pmspricing.json"), sessionInfo);

        repository.markDeleteByCode(saved.code, sessionInfo);

        Optional<PmsPricing> actual = repository.findById(saved.id, PmsPricing.class, sessionInfo);
        assertThat(actual).isNotEmpty().map(it -> it.id).contains(saved.id);
        assertThat(actual).map(it -> it.gsDeletedBy).contains(sessionInfo.getCurrentUserId());
        assertThat(actual).map(it -> it.deleted).isNotEmpty();
    }

    @Test
    void testMarkDeleteByCodeMultiple() {
        // setup
        repository.save(toPojo(type, jsonPath + "pmspricing.json"), sessionInfo);
        repository.save(toPojo(type, jsonPath + "pmspricing.json"), sessionInfo);

        // when
        int numberOfUpdatedDoc = repository.markDeleteByCode("default", sessionInfo);

        // then
        assertThat(numberOfUpdatedDoc).isEqualTo(2);
    }

    @Test
    void getPriceCode() {
        // setup
        ImmutableList<String> codeList = ImmutableList.of("code_1", "code_2", "code_2", "code_3");
        saveTestPmsPricing(codeList);

        // when
        List<String> priceCodes = repository.getPriceCodes(sessionInfo);

        // then
        assertThat(priceCodes).isNotEmpty()
                .size()
                .isEqualTo(3)
                .returnToIterable()
                .contains("code_1", "code_2", "code_3");
    }

    private void saveTestPmsPricing(List<String> priceCodes) {
        priceCodes.forEach(code -> {
            PmsPricing obj = toPojo(type, jsonPath + "pmspricing.json");
            obj.code = code;
            repository.save(obj, sessionInfo);
        });
    }
}
