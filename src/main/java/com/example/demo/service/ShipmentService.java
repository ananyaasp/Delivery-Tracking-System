package com.example.demo.service;

import com.example.demo.model.DeliveryAgent;
import com.example.demo.model.Shipment;
import com.example.demo.model.ShipmentResponse;
import com.example.demo.model.ShipmentStatus;
import com.example.demo.model.StatusLog;
import com.example.demo.repository.DeliveryAgentRepository;
import com.example.demo.repository.ShipmentRepository;
import com.example.demo.repository.StatusLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private DeliveryAgentRepository agentRepository;

    @Autowired
    private StatusLogRepository statusLogRepository;

    // ✅ CREATE SHIPMENT + LOG
    @Transactional
    public Shipment createShipment(Shipment shipment) {
        shipment.setStatus(ShipmentStatus.CREATED);
        Shipment saved = shipmentRepository.save(shipment);

        StatusLog log = new StatusLog(
                ShipmentStatus.CREATED.getDisplayName(),
                LocalDateTime.now(),
                saved);
        statusLogRepository.save(log);

        return saved;
    }

    // ✅ GET ALL
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    // ✅ GET SHIPMENT BY ID (for tracking)
    public Shipment getShipmentById(Long id) {
        return shipmentRepository.findById(id).orElse(null);
    }

    // ✅ UPDATE STATUS + AUTO-ASSIGN AGENT ON PICKEDUP + FREE AGENT ON DELIVERED
    @Transactional
    public Shipment updateStatus(Long id, String statusStr) {
        Shipment shipment = shipmentRepository.findById(id).orElse(null);

        if (shipment == null) {
            return null;
        }

        ShipmentStatus currentStatus = shipment.getStatus() != null ? shipment.getStatus() : ShipmentStatus.CREATED;
        ShipmentStatus targetStatus = ShipmentStatus.fromString(statusStr);

        // Reject invalid transitions
        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new IllegalStateException(
                    "Invalid status transition from " + currentStatus.getDisplayName() + " to " + targetStatus.getDisplayName()
            );
        }

        // 🔑 AUTO-ASSIGN available agent when status becomes PickedUp
        if (targetStatus == ShipmentStatus.PICKED_UP && shipment.getAgent() == null) {
            List<DeliveryAgent> availableAgents = agentRepository.findAvailableAgentsWithLock();

            if (!availableAgents.isEmpty()) {
                DeliveryAgent agent = availableAgents.get(0);
                agent.setAvailable(false);
                agentRepository.save(agent);
                shipment.setAgent(agent);
            } else {
                // 🚫 No agents available — throw exception
                throw new RuntimeException("No delivery agents available. Please try again later.");
            }
        }

        // 🔑 FREE AGENT when status becomes Delivered
        if (targetStatus == ShipmentStatus.DELIVERED && shipment.getAgent() != null) {
            DeliveryAgent agent = shipment.getAgent();
            agent.setAvailable(true);
            agentRepository.save(agent);
        }

        shipment.setStatus(targetStatus);
        Shipment updated = shipmentRepository.save(shipment);

        StatusLog log = new StatusLog(
                targetStatus.getDisplayName(),
                LocalDateTime.now(),
                updated);
        statusLogRepository.save(log);

        return updated;
    }

    // ✅ ASSIGN AGENT MANUALLY
    @Transactional
    public ShipmentResponse assignAgent(Long shipmentId, Long agentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId).orElse(null);
        DeliveryAgent agent = agentRepository.findById(agentId).orElse(null);

        if (shipment != null && agent != null && agent.isAvailable()) {
            shipment.setAgent(agent);
            agent.setAvailable(false);

            agentRepository.save(agent);
            shipmentRepository.save(shipment);

            return new ShipmentResponse(
                    shipment.getId(),
                    shipment.getSourceAddress(),
                    shipment.getDestinationAddress(),
                    shipment.getStatus() != null ? shipment.getStatus().getDisplayName() : null,
                    agent.getName(),
                    agent.getPhone());
        }
        return null;
    }
}