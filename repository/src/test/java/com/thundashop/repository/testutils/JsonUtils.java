package com.thundashop.repository.testutils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

public class JsonUtils {

    public static <T> T toPojo(Type type, String path) {
        JsonReader reader;
        Gson gson = new Gson();

        try {
            reader = new JsonReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return gson.fromJson(reader, type);
    }

}
