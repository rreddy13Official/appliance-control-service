package org.iot.appliances.api.exceptionmapper;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.iot.appliances.api.exception.ApplianceStateException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplianceStateExceptionMapper implements ExceptionMapper<ApplianceStateException> {

    @Override
    public Response toResponse(final ApplianceStateException exception) {
        final var errors = new JsonArray().add(
                new JsonObject()
                        .put("title", exception.getTitle())
                        .put("detail", exception.getDetail())
                        .put("status", String.valueOf(exception.getStatus())));

        return Response.status(exception.getStatus())
                .entity(new JsonObject().put("errors", errors))
                .build();
    }
}