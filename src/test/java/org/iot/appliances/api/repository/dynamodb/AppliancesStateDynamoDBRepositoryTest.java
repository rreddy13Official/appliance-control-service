package org.iot.appliances.api.repository.dynamodb;

import io.vertx.core.json.JsonObject;
import org.iot.appliances.api.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AppliancesStateDynamoDBRepositoryTest {

    private static final String DEVICE_ID = "123";
    private final Duration TIMEOUT = Duration.ofMillis(1000L);

    @Mock
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    @InjectMocks
    private AppliancesStateDynamoDBRepository sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveDeviceStateToRepository() {
        JsonObject deviceJsonObject = TestUtils.getTestData();
        SdkHttpResponse sd = SdkHttpResponse.builder().statusCode(200).build();
        PutItemResponse mockedResponse = (PutItemResponse) PutItemResponse.builder()
                .attributes(Map.of("state", AttributeValue.builder().s(deviceJsonObject.toString()).build()))
                .sdkHttpResponse(sd)
                .build();

        when(dynamoDbAsyncClient.putItem(any(PutItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockedResponse));

        var result = sut.upsert(DEVICE_ID, deviceJsonObject).await().atMost(TIMEOUT);

        assertEquals(true, result.isPresent());
        assertEquals(deviceJsonObject, result.get());
    }

    @Test
    void readDeviceStateFromRepository() {
        JsonObject deviceJsonObject = TestUtils.getTestData();
        SdkHttpResponse sd = SdkHttpResponse.builder().statusCode(200).build();
        var dynamoDbResponseMap = new HashMap<String, AttributeValue>();
        dynamoDbResponseMap.put("id", AttributeValue.builder().s(DEVICE_ID).build());
        dynamoDbResponseMap.put("state", AttributeValue.builder().s(deviceJsonObject.toString()).build());
        GetItemResponse mockedGetItemResponse = (GetItemResponse) GetItemResponse.builder()
                .item(dynamoDbResponseMap)
                .sdkHttpResponse(sd)
                .build();

        when(dynamoDbAsyncClient.getItem(any(GetItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockedGetItemResponse));

        var result = sut.read(DEVICE_ID).await().atMost(TIMEOUT);
        assertEquals(true, result.isPresent());
        assertEquals(deviceJsonObject, result.get());
    }

    @Test
    void readAllDeviceStateFromRepository() {
        JsonObject deviceJsonObject = TestUtils.getTestData();
        SdkHttpResponse sd = SdkHttpResponse.builder().statusCode(200).build();
        var dynamoDbResponseMap = new HashMap<String, AttributeValue>();
        dynamoDbResponseMap.put("id", AttributeValue.builder().s(DEVICE_ID).build());
        dynamoDbResponseMap.put("state", AttributeValue.builder().s(deviceJsonObject.toString()).build());
        ScanResponse mockedScanResponse = (ScanResponse) ScanResponse.builder()
                .items(dynamoDbResponseMap)
                .sdkHttpResponse(sd)
                .build();

        when(dynamoDbAsyncClient.scan(any(ScanRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockedScanResponse));

        var result = sut.readAll().await().atMost(TIMEOUT);
        assertEquals(true, result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(deviceJsonObject, result.get().get(0));
    }

    @Test
    void deleteDeviceStateFromRepository() {
        SdkHttpResponse sd = SdkHttpResponse.builder().statusCode(200).build();
        var dynamoDbResponseMap = new HashMap<String, AttributeValue>();
        dynamoDbResponseMap.put("id", AttributeValue.builder().s(DEVICE_ID).build());
        DeleteItemResponse mockedDeleteItemResponse = (DeleteItemResponse) DeleteItemResponse.builder()
                .sdkHttpResponse(sd)
                .build();

        when(dynamoDbAsyncClient.deleteItem(any(DeleteItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockedDeleteItemResponse));

        var result = sut.delete(DEVICE_ID).await().atMost(TIMEOUT);
        assertEquals(true, result);
    }
}