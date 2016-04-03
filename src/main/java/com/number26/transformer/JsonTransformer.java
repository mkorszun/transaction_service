package com.number26.transformer;

import com.google.gson.Gson;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {

    private Gson gson = new Gson();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }

    public <T> T fromJson(Class<T> clazz, String body) {
        return gson.fromJson(body, clazz);
    }
}