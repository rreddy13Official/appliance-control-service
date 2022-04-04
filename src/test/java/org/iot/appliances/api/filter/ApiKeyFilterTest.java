package org.iot.appliances.api.filter;

import io.quarkus.test.junit.QuarkusTest;
import org.iot.appliances.api.exception.ApplianceStateException;
import org.jboss.resteasy.core.interception.jaxrs.SuspendableContainerRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@QuarkusTest
class ApiKeyFilterTest {

    @Inject
    ApiKeyFilter sut;

    SuspendableContainerRequestContext requestContext = mock(SuspendableContainerRequestContext.class);

    @Test
    void requestWithNoApiKeyIsRejected() {
        when(requestContext.getHeaderString(eq("x-api-key"))).thenReturn(null);
        var exception = assertThrows(ApplianceStateException.class, () -> sut.filter(requestContext));

        assertEquals("Missing header x-api-key", exception.getTitle());
        assertEquals("Header x-api-key was missing from the request", exception.getDetail());
        assertEquals(401, exception.getStatus());

        verify(requestContext, times(0)).suspend();
    }

    @Test
    void requestWithValidApiKeyIdAccepted() {
        final var apiKey = "test";

        when(requestContext.getHeaderString(eq("x-api-key"))).thenReturn(apiKey);

        sut.filter(requestContext);

        verify(requestContext, times(1)).suspend();
        verify(requestContext, times(1)).resume();
    }

    @Test
    void requestWithInvalidApiKeyIsRejected() {
        when(requestContext.getHeaderString(eq("x-api-key"))).thenReturn("invalidKey");
        var exception = assertThrows(ApplianceStateException.class, () -> sut.filter(requestContext));

        assertEquals("Authentication error", exception.getTitle());
        assertEquals("The provided api-key is not valid", exception.getDetail());
        assertEquals(401, exception.getStatus());
    }

}