package org.iot.appliances.api.repository.dynamodb;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.iot.appliances.api.repository.AppliancesStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class AppliancesStateDynamoDBRepository implements AppliancesStateRepository {
    private static final Logger LOG = LoggerFactory.getLogger(AppliancesStateDynamoDBRepository.class);

    @Inject
    DynamoDbAsyncClient dynamoDbAsyncClient;

    @ConfigProperty(name = "AppliancesStateTable")
    String table;

    @Override
    public Uni<Optional<JsonObject>> upsert(String id, JsonObject applianceState) {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put("id", AttributeValue.builder().s(id).build());
        map.put("state", AttributeValue.builder().s(applianceState.toString()).build());

        final PutItemRequest putItemRequest =
                PutItemRequest.builder()
                        .tableName(table)
                        .item(map)
                        .build();

        return Uni.createFrom().completionStage(() ->
                dynamoDbAsyncClient.putItem(putItemRequest)
                        .thenApply(response -> {
                            if (response != null && response.sdkHttpResponse().isSuccessful()) {
                                LOG.info("Successfully saved the appliance state with id: {} and response: {}",
                                        id, response);
                                return Optional.of(applianceState);
                            }
                            LOG.error("Failed to save appliance state for id: {} and response is: {}",
                                    id, response);
                            return Optional.empty();
                        }));
    }

    @Override
    public Uni<Optional<JsonObject>> read(String id) {
        Map<String, AttributeValue> keysMap = new HashMap<>();
        keysMap.put("id", AttributeValue.builder().s(id).build());
        final GetItemRequest getItemRequest =
                GetItemRequest.builder()
                        .tableName(table)
                        .key(keysMap)
                        .build();

        return Uni.createFrom().completionStage(() ->
                dynamoDbAsyncClient.getItem(getItemRequest)
                        .thenApply(response -> {
                            if (response != null && response.hasItem()) {
                                LOG.info("Get appliance state with id: {} success", id);
                                return Optional.of(this.parseResponse(response.item()));
                            }
                            LOG.error("Failed to get appliance state with id: {} and response is: {}", id, response);
                            return Optional.empty();
                        }));
    }

    @Override
    public Uni<Optional<List<JsonObject>>> readAll() {
        final ScanRequest scanRequest =
                ScanRequest.builder()
                        .tableName(table)
                        .build();

        return Uni.createFrom().completionStage(() ->
                dynamoDbAsyncClient.scan(scanRequest)
                        .thenApply(response -> {
                            if (response.hasItems()) {
                                LOG.info("Successfully fetched ALL {} number of appliances", response.count());
                                return Optional.of(this.parseResponses(response.items()));
                            }
                            LOG.error("Failed to get appliances and response is: {}", response);
                            return Optional.empty();
                        }));
    }

    private List<JsonObject> parseResponses(List<Map<String, AttributeValue>> items) {
        List<JsonObject> appliances = new ArrayList<>();
        items.forEach(item -> appliances.add(parseResponse(item)));
        return appliances;
    }

    private JsonObject parseResponse(Map<String, AttributeValue> item) {
        String attributeValue = item.get("state").s();
        return new JsonObject(attributeValue);
    }

    @Override
    public Uni<Boolean> delete(String id) {
        Map<String, AttributeValue> keysMap = new HashMap<>();
        keysMap.put("id", AttributeValue.builder().s(id).build());
        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName(table)
                .key(keysMap)
                .build();
        return Uni.createFrom().completionStage(() ->
            dynamoDbAsyncClient.deleteItem(deleteItemRequest)
                    .thenApply(response -> {
                        if(response.sdkHttpResponse().isSuccessful()) {
                            LOG.info("Successfully removed the appliance state: {}", id);
                            return true;
                        }
                        LOG.info("Failed remove the appliance state: {}", id);
                        return false;
                    }));
    }
}
