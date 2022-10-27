package dev.kryz.utils;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {
    public String errorMessage;

    public ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getAsString() {
        Map<String, Object> map = new HashMap<>();
        map.put("message", errorMessage);
        return new Gson().toJson(map);
    }
}
