package com.example.demo.service;

import com.example.demo.model.DeliveryAgent;
import com.example.demo.repository.DeliveryAgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryAgentService {

    @Autowired
    private DeliveryAgentRepository repository;

    public DeliveryAgent createAgent(DeliveryAgent agent) {
        agent.setAvailable(true);
        return repository.save(agent);
    }

    public List<DeliveryAgent> getAvailableAgents() {
        return repository.findByAvailableTrue();
    }

    public void markUnavailable(Long id) {
        DeliveryAgent agent = repository.findById(id).orElse(null);
        if (agent != null) {
            agent.setAvailable(false);
            repository.save(agent);
        }
    }
}