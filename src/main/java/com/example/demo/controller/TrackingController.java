package com.example.demo.controller;

import com.example.demo.model.Shipment;
import com.example.demo.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tracking")
public class TrackingController {

    @Autowired
    private ShipmentService shipmentService;

    @GetMapping("/{shipmentId}")
    public ResponseEntity<Shipment> trackShipment(@PathVariable Long shipmentId) {
        Shipment shipment = shipmentService.getShipmentById(shipmentId);
        if (shipment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shipment);
    }
}