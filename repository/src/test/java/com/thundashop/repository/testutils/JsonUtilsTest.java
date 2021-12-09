package com.thundashop.repository.testutils;

import com.google.gson.reflect.TypeToken;
import com.thundashop.core.pmsmanager.ConferenceData;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    @Test
    void toPojo() {
        final Type type = new TypeToken<ConferenceData>() {
        }.getType();

        ConferenceData conferenceData = JsonUtils.toPojo(type, "src/test/resources/pmsmanager/conferencedata/conferenceData.json");

        System.out.println(conferenceData);
    }
}