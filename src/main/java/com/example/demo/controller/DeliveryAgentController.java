package com.example.demo.controller;

import com.example.demo.model.DeliveryAgent;
import com.example.demo.service.DeliveryAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agents")
public class DeliveryAgentController {

    @Autowired
    private DeliveryAgentService service;

    @PostMapping
    public DeliveryAgent createAgent(@RequestBody DeliveryAgent agent) {
        return service.createAgent(agent);
    }

    @GetMapping("/available")
    public List<DeliveryAgent> getAvailableAgents() {
        return service.getAvailableAgents();
    }
}