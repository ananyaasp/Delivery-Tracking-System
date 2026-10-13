package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ShipmentStatus {
    CREATED("Created"),
    PICKED_UP("PickedUp"),
    IN_TRANSIT("InTransit"),
    OUT_FOR_DELIVERY("OutForDelivery"),
    DELIVERED("Delivered");

    private final String displayName;

    ShipmentStatus(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static ShipmentStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        String clean = text.trim();
        for (ShipmentStatus s : ShipmentStatus.values()) {
            if (s.name().equalsIgnoreCase(clean) 
                || s.displayName.equalsIgnoreCase(clean)
                || s.displayName.replace(" ", "").equalsIgnoreCase(clean.replace(" ", ""))
                || s.name().replace("_", "").equalsIgnoreCase(clean.replace("_", ""))) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid shipment status: '" + text + "'. Allowed statuses: Created, PickedUp, InTransit, OutForDelivery, Delivered");
    }

    public boolean canTransitionTo(ShipmentStatus target) {
        if (target == null) return false;
        switch (this) {
            case CREATED:
                return target == PICKED_UP;
            case PICKED_UP:
                return target == IN_TRANSIT;
            case IN_TRANSIT:
                return target == OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY:
                return target == DELIVERED;
            case DELIVERED:
                return false; // Terminal state
            default:
                return false;
        }
    }
}
