package org.iot.appliances.api.exceptionmapper;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionExceptionMapperTest {

    final ExceptionExceptionMapper sut = new ExceptionExceptionMapper();

    @Test
    void toResponse() {
        final var response = sut.toResponse(new IllegalArgumentException("Oh no!"));

        final var errors = new JsonArray().add(
                new JsonObject()
                        .put("title", "Unexpected error occurred")
                        .put("detail", "An unexpected error has occurred, it has been logged.")
                        .put("status", "500"));

        final var expectedEntity = new JsonObject().put("errors", errors);

        assertEquals(500, response.getStatus());
        assertEquals(expectedEntity, response.getEntity());
    }

}
