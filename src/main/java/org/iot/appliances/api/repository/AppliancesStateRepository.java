package org.iot.appliances.api.repository;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Optional;

public interface AppliancesStateRepository {

    Uni<Optional<JsonObject>> upsert(String id, JsonObject applianceState);
    Uni<Optional<JsonObject>> read(String id);
    Uni<Optional<List<JsonObject>>> readAll();
    Uni<Boolean> delete(String id);
}
