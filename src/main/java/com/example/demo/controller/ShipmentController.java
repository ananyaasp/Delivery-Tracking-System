package com.example.demo.controller;

import com.example.demo.model.Shipment;
import com.example.demo.model.ShipmentResponse;
import com.example.demo.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    // ✅ Create shipment
    @PostMapping
    public Shipment createShipment(@RequestBody Shipment shipment) {
        return shipmentService.createShipment(shipment);
    }

    // ✅ Get all shipments
    @GetMapping
    public List<Shipment> getAllShipments() {
        return shipmentService.getAllShipments();
    }

    // ✅ Assign agent
    @PutMapping("/{shipmentId}/assign/{agentId}")
    public ShipmentResponse assignAgent(@PathVariable Long shipmentId, @PathVariable Long agentId) {
        return shipmentService.assignAgent(shipmentId, agentId);
    }



        // ✅ Update shipment status
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                        @RequestParam String status) {
        try {
            Shipment updated = shipmentService.updateStatus(id, status);
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
}
}