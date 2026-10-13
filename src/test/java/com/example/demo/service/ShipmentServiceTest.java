package com.example.demo.service;

import com.example.demo.model.DeliveryAgent;
import com.example.demo.model.Shipment;
import com.example.demo.model.ShipmentResponse;
import com.example.demo.model.ShipmentStatus;
import com.example.demo.model.StatusLog;
import com.example.demo.repository.DeliveryAgentRepository;
import com.example.demo.repository.ShipmentRepository;
import com.example.demo.repository.StatusLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private DeliveryAgentRepository agentRepository;

    @Mock
    private StatusLogRepository statusLogRepository;

    @InjectMocks
    private ShipmentService shipmentService;

    private Shipment sampleShipment;
    private DeliveryAgent sampleAgent;

    @BeforeEach
    void setUp() {
        sampleShipment = new Shipment("Mumbai", "Delhi", ShipmentStatus.CREATED);
        sampleShipment.setId(1L);

        sampleAgent = new DeliveryAgent("Rajesh Kumar", "9876543210", true);
        sampleAgent.setId(10L);
    }

    @Test
    @DisplayName("1. Create shipment sets status to CREATED and persists initial audit log")
    void testCreateShipment_InitializesStatusAndCreatesLog() {
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shipment created = shipmentService.createShipment(sampleShipment);

        assertNotNull(created);
        assertEquals(ShipmentStatus.CREATED, created.getStatus());

        ArgumentCaptor<StatusLog> logCaptor = ArgumentCaptor.forClass(StatusLog.class);
        verify(statusLogRepository, times(1)).save(logCaptor.capture());
        assertEquals("Created", logCaptor.getValue().getStatus());
        assertEquals(created, logCaptor.getValue().getShipment());
    }

    @Test
    @DisplayName("2. PickedUp status automatically assigns locked available agent and sets agent unavailable")
    void testUpdateStatus_PickedUp_AutoAssignsAgentAndLocks() {
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(sampleShipment));
        when(agentRepository.findAvailableAgentsWithLock()).thenReturn(List.of(sampleAgent));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shipment updated = shipmentService.updateStatus(1L, "PickedUp");

        assertNotNull(updated);
        assertEquals(ShipmentStatus.PICKED_UP, updated.getStatus());
        assertNotNull(updated.getAgent());
        assertEquals("Rajesh Kumar", updated.getAgent().getName());
        assertFalse(sampleAgent.isAvailable(), "Agent should be marked unavailable after assignment");

        verify(agentRepository, times(1)).findAvailableAgentsWithLock();
        verify(agentRepository, times(1)).save(sampleAgent);
        verify(statusLogRepository, times(1)).save(any(StatusLog.class));
    }

    @Test
    @DisplayName("3. PickedUp status with no agents available throws RuntimeException")
    void testUpdateStatus_PickedUp_NoAgentAvailable_ThrowsException() {
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(sampleShipment));
        when(agentRepository.findAvailableAgentsWithLock()).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            shipmentService.updateStatus(1L, "PickedUp");
        });

        assertTrue(exception.getMessage().contains("No delivery agents available"));
        verify(shipmentRepository, never()).save(any(Shipment.class));
        verify(statusLogRepository, never()).save(any(StatusLog.class));
    }

    @Test
    @DisplayName("4. Reject invalid reverse transition: Delivered -> Created throws IllegalStateException")
    void testUpdateStatus_InvalidTransition_DeliveredToCreated_ThrowsException() {
        sampleShipment.setStatus(ShipmentStatus.DELIVERED);
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(sampleShipment));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            shipmentService.updateStatus(1L, "Created");
        });

        assertTrue(exception.getMessage().contains("Invalid status transition from Delivered to Created"));
        verify(shipmentRepository, never()).save(any(Shipment.class));
        verify(statusLogRepository, never()).save(any(StatusLog.class));
    }

    @Test
    @DisplayName("5. Reject invalid skipping transition: Created -> Delivered throws IllegalStateException")
    void testUpdateStatus_InvalidTransition_CreatedToDelivered_ThrowsException() {
        sampleShipment.setStatus(ShipmentStatus.CREATED);
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(sampleShipment));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            shipmentService.updateStatus(1L, "Delivered");
        });

        assertTrue(exception.getMessage().contains("Invalid status transition from Created to Delivered"));
        verify(shipmentRepository, never()).save(any(Shipment.class));
        verify(statusLogRepository, never()).save(any(StatusLog.class));
    }

    @Test
    @DisplayName("6. Reaching Delivered status automatically frees assigned agent back to available")
    void testUpdateStatus_Delivered_FreesAssignedAgent() {
        sampleShipment.setStatus(ShipmentStatus.OUT_FOR_DELIVERY);
        sampleAgent.setAvailable(false);
        sampleShipment.setAgent(sampleAgent);

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(sampleShipment));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shipment updated = shipmentService.updateStatus(1L, "Delivered");

        assertNotNull(updated);
        assertEquals(ShipmentStatus.DELIVERED, updated.getStatus());
        assertTrue(sampleAgent.isAvailable(), "Assigned agent should be freed (available = true) on delivery");

        verify(agentRepository, times(1)).save(sampleAgent);
        verify(statusLogRepository, times(1)).save(any(StatusLog.class));
    }

    @Test
    @DisplayName("7. Audit log captures the exact status transition with timestamp and shipment link")
    void testUpdateStatus_AuditLogCreatedWithCorrectDetails() {
        sampleShipment.setStatus(ShipmentStatus.PICKED_UP);
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(sampleShipment));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        shipmentService.updateStatus(1L, "InTransit");

        ArgumentCaptor<StatusLog> logCaptor = ArgumentCaptor.forClass(StatusLog.class);
        verify(statusLogRepository, times(1)).save(logCaptor.capture());

        StatusLog capturedLog = logCaptor.getValue();
        assertEquals("InTransit", capturedLog.getStatus());
        assertNotNull(capturedLog.getTimestamp());
        assertEquals(sampleShipment, capturedLog.getShipment());
    }

    @Test
    @DisplayName("8. Manual agent assignment assigns agent and marks agent unavailable")
    void testAssignAgent_ManualSuccess() {
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(sampleShipment));
        when(agentRepository.findById(10L)).thenReturn(Optional.of(sampleAgent));

        ShipmentResponse response = shipmentService.assignAgent(1L, 10L);

        assertNotNull(response);
        assertEquals("Rajesh Kumar", response.getAgentName());
        assertEquals("9876543210", response.getAgentPhone());
        assertFalse(sampleAgent.isAvailable());
        assertEquals(sampleAgent, sampleShipment.getAgent());

        verify(agentRepository, times(1)).save(sampleAgent);
        verify(shipmentRepository, times(1)).save(sampleShipment);
    }
}
