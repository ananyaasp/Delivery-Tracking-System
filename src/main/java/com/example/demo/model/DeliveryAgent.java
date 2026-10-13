package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class DeliveryAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private boolean available = true;

    @Version
    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long version = 0L;

    public DeliveryAgent() {
    }

    public DeliveryAgent(String name, String phone, boolean available) {
        this.name = name;
        this.phone = phone;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}