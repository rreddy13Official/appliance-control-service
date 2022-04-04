package org.iot.appliances.api;

import io.vertx.core.json.JsonObject;

public class TestUtils {

    public static JsonObject getTestData() {
        JsonObject jsonApiObject = new JsonObject();
        jsonApiObject
                .put("id", "123")
                .put("location", new JsonObject()
                        .put("latitude", "12345")
                        .put("longitude", "23456"))
                .put("device", new JsonObject()
                        .put("serialNumber", "123")
                        .put("deviceType", "oven")
                        .put("manufacturer", "org"));
        return jsonApiObject;
    }
}
