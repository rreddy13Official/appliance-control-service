package org.iot.appliances.api.exceptionmapper;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.iot.appliances.api.exception.ApplianceStateException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplianceStateExceptionMapperTest {

    final ApplianceStateExceptionMapper sut = new ApplianceStateExceptionMapper();

    @Test
    void toResponse() {
        final var title = UUID.randomUUID().toString();
        final var detail = UUID.randomUUID().toString();
        final var status = 400;
        final var response = sut.toResponse(new ApplianceStateException(title, detail, status));

        final var errors = new JsonArray().add(
                new JsonObject()
                        .put("title", title)
                        .put("detail", detail)
                        .put("status", String.valueOf(status)));

        final var expectedEntity = new JsonObject().put("errors", errors);

        assertEquals(status, response.getStatus());
        assertEquals(expectedEntity, response.getEntity());
    }

}
