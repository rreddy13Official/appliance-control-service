package org.iot.appliances.api.exceptionmapper;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(ExceptionExceptionMapper.class);

    @Override
    public Response toResponse(final Exception exception) {
        LOG.error("Mapper handled unexpected exception", exception);

        final Response.Status internalServerError = Response.Status.INTERNAL_SERVER_ERROR;

        final var errors = new JsonArray().add(
                new JsonObject()
                        .put("title", "Unexpected error occurred")
                        .put("detail", "An unexpected error has occurred, it has been logged.")
                        .put("status", String.valueOf(internalServerError.getStatusCode())));

        return Response.status(internalServerError)
                .entity(new JsonObject().put("errors", errors))
                .build();
    }

}
