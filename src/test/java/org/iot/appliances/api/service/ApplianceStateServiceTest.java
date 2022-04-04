package org.iot.appliances.api.service;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.iot.appliances.api.TestUtils;
import org.iot.appliances.api.exception.ApplianceStateException;
import org.iot.appliances.api.repository.AppliancesStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ApplianceStateServiceTest {
    private static final String DEVICE_ID = "123";
    private final Duration TIMEOUT = Duration.ofMillis(1000L);

    @Mock
    private AppliancesStateRepository appliancesStateRepository;

    @InjectMocks
    private ApplianceStateService sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testThatAllAppliancesHaveBeenRetrieved() {
        JsonObject deviceJsonObject = TestUtils.getTestData();
        when(appliancesStateRepository.readAll())
                .thenReturn(Uni.createFrom().item(Optional.of(List.of(deviceJsonObject))));
        var allAppliances = sut.getAllAppliances().await().atMost(TIMEOUT);

        assertEquals(List.of(deviceJsonObject), allAppliances);
    }

    @Test
    void testThatApplianceIsReturnedByApplianceId() {
        JsonObject deviceJsonObject = TestUtils.getTestData();
        when(appliancesStateRepository.read(DEVICE_ID))
                .thenReturn(Uni.createFrom().item(Optional.of(deviceJsonObject)));
        var applianceResult = sut.getAppliance(DEVICE_ID).await().atMost(TIMEOUT);

        assertEquals(deviceJsonObject, applianceResult);
    }

    @Test
    void testThatExceptionThrownWhenByApplianceId() {
        when(appliancesStateRepository.read(DEVICE_ID))
                .thenReturn(Uni.createFrom().item(Optional.ofNullable(null)));

        assertThrows(ApplianceStateException.class, () -> sut.getAppliance(DEVICE_ID).await().atMost(TIMEOUT));

    }

    @Test
    void testThatRequestedApplianceAttributeIsFetched() {
        JsonObject deviceJsonObject = TestUtils.getTestData();
        JsonObject expectedLocationAttribute = deviceJsonObject.getJsonObject("location");
        when(appliancesStateRepository.read(DEVICE_ID))
                .thenReturn(Uni.createFrom().item(Optional.of(deviceJsonObject)));
        var applianceAttributeResult = sut.getApplianceAttribute(DEVICE_ID, "location").await().atMost(TIMEOUT);

        assertEquals(expectedLocationAttribute, applianceAttributeResult);
    }

    @Test
    void getApplianceAttributeResource() {
        JsonObject deviceJsonObject = TestUtils.getTestData();
        JsonObject locationAttribute = deviceJsonObject.getJsonObject("location");
        JsonObject expectedLatitudeResource = new JsonObject().put("latitude", locationAttribute.getString("latitude"));
        when(appliancesStateRepository.read(DEVICE_ID))
                .thenReturn(Uni.createFrom().item(Optional.of(deviceJsonObject)));
        var applianceAttributeResult = sut.getApplianceAttributeResource(DEVICE_ID, "location", "latitude").await().atMost(TIMEOUT);

        assertEquals(expectedLatitudeResource, applianceAttributeResult);
    }

    @Test
    void saveAppliance() {
        JsonObject deviceJsonObject = TestUtils.getTestData();
        when(appliancesStateRepository.upsert(DEVICE_ID, deviceJsonObject))
                .thenReturn(Uni.createFrom().item(Optional.of(deviceJsonObject)));
        var applianceAttributeResult = sut.saveAppliance(DEVICE_ID, deviceJsonObject).await().atMost(TIMEOUT);

        assertEquals(deviceJsonObject, applianceAttributeResult);
    }

    @Test
    void removeAppliance() {
        when(appliancesStateRepository.delete(DEVICE_ID))
                .thenReturn(Uni.createFrom().item(true));
        assertDoesNotThrow(() -> sut.removeAppliance(DEVICE_ID).await().atMost(TIMEOUT));
    }

    @Test
    void updateApplianceAttribute() {
        JsonObject deviceJsonObject = TestUtils.getTestData();
        when(appliancesStateRepository.read(DEVICE_ID))
                .thenReturn(Uni.createFrom().item(Optional.of(deviceJsonObject)));
        when(appliancesStateRepository.upsert(any(String.class), any(JsonObject.class)))
                .thenReturn(Uni.createFrom().item(Optional.of(deviceJsonObject)));
        var applianceAttributeResult = sut.updateApplianceAttribute(DEVICE_ID, "previousLocation", deviceJsonObject.getJsonObject("location")).await().atMost(TIMEOUT);

        assertEquals(applianceAttributeResult, deviceJsonObject.getJsonObject("location"));
    }

    @Test
    void updateApplianceAttributeResource() {
        var newLocationResource = new JsonObject().put("uncertainty", "22");
        JsonObject deviceJsonObject = TestUtils.getTestData();
        when(appliancesStateRepository.read(DEVICE_ID))
                .thenReturn(Uni.createFrom().item(Optional.of(deviceJsonObject)));
        when(appliancesStateRepository.upsert(any(String.class), any(JsonObject.class)))
                .thenReturn(Uni.createFrom().item(Optional.of(deviceJsonObject)));

        var applianceAttributeResult = sut.updateApplianceAttributeResource(DEVICE_ID, "location", "uncertainty", newLocationResource).await().atMost(TIMEOUT);

        assertEquals(newLocationResource, applianceAttributeResult);
    }
}