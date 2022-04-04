package org.iot.appliances.api.service;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.iot.appliances.api.exception.ApplianceStateException;
import org.iot.appliances.api.repository.AppliancesStateRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ApplianceStateService {
    @Inject
    AppliancesStateRepository appliancesStateRepository;

    public Uni<List<JsonObject>> getAllAppliances() {
        return appliancesStateRepository.readAll()
                .onItem()
                .transform(result -> {
                    if(result.isPresent()) {
                        return result.get();
                    }
                    throw new ApplianceStateException("No appliances exist",
                            "No appliances exist in repository",
                            404);
                });
    }

    public Uni<JsonObject> getAppliance(String id) {
        return appliancesStateRepository.read(id)
                .onItem()
                .transform(result -> {
                    if(result.isPresent()) {
                        return result.get();
                    }
                    throw new ApplianceStateException("Appliance does not exist",
                            String.format("Appliance not found for id: %s", id),
                            404);
                });
    }

    public Uni<JsonObject> getApplianceAttribute(String id, String attribute) {
        Uni<JsonObject> applianceJsonObject =  this.getAppliance(id);
        return  applianceJsonObject
                .onItem()
                .transform(appliance -> {
                    var attributeObject = appliance.getJsonObject(attribute);
                    if(attributeObject != null) {
                        return attributeObject;
                    }
                    throw new ApplianceStateException("Attribute does not exist",
                            String.format("Attribute: %s does not exist for appliance: %s", attribute, id),
                            404);
                });
    }

    public Uni<JsonObject> getApplianceAttributeResource(String id, String attribute, String resource) {
        Uni<JsonObject> applianceAttributeJsonObject =  getApplianceAttribute(id, attribute);
        return  applianceAttributeJsonObject
                .onItem()
                .transform(applianceAttribute -> {
                    var resourceValue = applianceAttribute.getString(resource);
                    if(resourceValue != null) {
                        return new JsonObject().put(resource, resourceValue);
                    }
                    throw new ApplianceStateException("Resource does not exist",
                            String.format("Resource %s does not exist for Attribute: %s of appliance: %s", resource, attribute, id),
                            404);
                });
    }

    public Uni<JsonObject> saveAppliance(String id, JsonObject jsonObject) {
        return appliancesStateRepository.upsert(id, jsonObject)
                .onItem()
                .transform(result -> {
                    if(result.isPresent()) {
                        return result.get();
                    }
                    throw new ApplianceStateException("Save appliance failed",
                            String.format("Save appliance failed for %s ", id),
                            400);
                });
    }

    public Uni<Void> removeAppliance(String id) {
        return appliancesStateRepository.delete(id)
                .onItem()
                .transform(result -> {
                    if(!result) {
                        throw new ApplianceStateException("Remove appliance failed",
                                String.format("Remove appliance failed for %s ", id),
                                400);
                    }
                    return null;
                });
    }

    public Uni<JsonObject> updateApplianceAttribute(String id, String attribute, JsonObject attributePayloadObj) {
        return getAppliance(id)
                .onItem()
                .transformToUni(appliance -> {
                    appliance.put(attribute, attributePayloadObj);
                    return saveAppliance(id, appliance)
                            .onItem()
                            .transform(result -> attributePayloadObj);
                });
    }

    public Uni<JsonObject> updateApplianceAttributeResource(String id, String attribute, String resource, JsonObject resourcePayloadObject) {
        return getAppliance(id)
                .onItem()
                .transformToUni(appliance -> {
                    var attributeObj = appliance.getJsonObject(attribute);
                    if(attributeObj != null) {
                        attributeObj.put(resource, resourcePayloadObject.getString(resource));
                        appliance.put(attribute, attributeObj);
                        return saveAppliance(id, appliance)
                                .onItem()
                                .transform(result -> resourcePayloadObject);
                    }
                    return null;
                });
    }
}
