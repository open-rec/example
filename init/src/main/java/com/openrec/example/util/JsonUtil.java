package com.openrec.example.util;

import com.google.gson.Gson;

public class JsonUtil {

    private static final Gson gson = new Gson();

    public static String objToJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T jsonToObj(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
