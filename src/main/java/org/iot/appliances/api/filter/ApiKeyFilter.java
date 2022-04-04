package org.iot.appliances.api.filter;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.iot.appliances.api.exception.ApplianceStateException;
import org.jboss.resteasy.core.interception.jaxrs.SuspendableContainerRequestContext;

import javax.enterprise.inject.Instance;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class ApiKeyFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "apiKey")
    Instance<String> applicationApiKey;

    @Override
    public void filter(final ContainerRequestContext requestContext) {
        final var suspendableRequestContext = (SuspendableContainerRequestContext) requestContext;
        final var xApiKey = requestContext.getHeaderString("x-api-key");

        if (xApiKey == null) {
            throw new ApplianceStateException("Missing header x-api-key", "Header x-api-key was missing from the request", 401);
        }

        suspendableRequestContext.suspend();
        if(!applicationApiKey.get().equals(xApiKey)) {
          throw new ApplianceStateException("Authentication error",
                            "The provided api-key is not valid", 401);
        }
        suspendableRequestContext.resume();
    }
}
