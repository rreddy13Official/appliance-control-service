package org.iot.appliances.api.resource;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.iot.appliances.api.exception.ApplianceStateException;
import org.iot.appliances.api.service.ApplianceStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/appliance")
public class ApplianceResource {
    private static final Logger LOG = LoggerFactory.getLogger(ApplianceResource.class);

    @Inject
    ApplianceStateService applianceStateService;

    public ApplianceResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<JsonObject>> getAllAppliances() {
        LOG.info("Received get request for all appliance");
        return applianceStateService.getAllAppliances();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<JsonObject> getAppliance(@PathParam("id") final String id) {
        LOG.info("Received get request for appliance: {}", id);
        return applianceStateService.getAppliance(id);
    }

    @GET
    @Path("/{id}/{attribute}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<JsonObject> getApplianceAttribute(@PathParam("id") final String id,
                                        @PathParam("attribute") final String attribute) {
        LOG.info("Received get request for appliance {} and attribute: {}", id, attribute);
        return applianceStateService.getApplianceAttribute(id, attribute);
    }

    @GET
    @Path("/{id}/{attribute}/{resource}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<JsonObject> getApplianceAttributeResource(@PathParam("id") final String id,
                                                 @PathParam("attribute") final String attribute,
                                                 @PathParam("resource") final String resource) {
        LOG.info("Received get request for resource {}, attribute {} and appliance: {}", resource, attribute, id);
        return applianceStateService.getApplianceAttributeResource(id, attribute, resource);
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<JsonObject> updateApplianceAttribute(@PathParam("id") final String id,
                                                    final String attributePayload) {
        LOG.error("Rejecting patch request. To update complete appliance, use put {}", id);
        throw new ApplianceStateException("Use PUT request to update whole appliance",
                "Use PUT request to update whole appliance",
                400);
    }

    @PATCH
    @Path("/{id}/{attribute}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<JsonObject> updateApplianceAttribute(@PathParam("id") final String id,
                                                 @PathParam("attribute") final String attribute,
                                                    final JsonObject attributePayload) {
        LOG.info("Received patch request for appliance {} and attribute: {}", id, attribute);
        return applianceStateService.updateApplianceAttribute(id, attribute, attributePayload);
    }

    @PATCH
    @Path("/{id}/{attribute}/{resource}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<JsonObject> updateApplianceAttribute(@PathParam("id") final String id,
                                                    @PathParam("attribute") final String attribute,
                                                    @PathParam("resource") final String resource,
                                                    final JsonObject resourcePayload) {
        LOG.info("Received patch request for appliance {}, attribute: {} and resource {}", id, attribute, resource);
        return applianceStateService.updateApplianceAttributeResource(id, attribute, resource, resourcePayload);
    }

    @POST
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<JsonObject> saveAppliance(@PathParam("id") final String id, final JsonObject payload) throws ApplianceStateException {
        LOG.info("Received save request for appliance: {}", id);
        return applianceStateService.saveAppliance(id, payload);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> removeAppliance(@PathParam("id") final String id) {
        LOG.info("Received remove request for appliance: {}", id);
        return applianceStateService.removeAppliance(id);
    }

}
