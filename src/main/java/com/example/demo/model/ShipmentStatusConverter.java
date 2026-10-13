package com.example.demo.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ShipmentStatusConverter implements AttributeConverter<ShipmentStatus, String> {

    @Override
    public String convertToDatabaseColumn(ShipmentStatus status) {
        return status != null ? status.getDisplayName() : null;
    }

    @Override
    public ShipmentStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? ShipmentStatus.fromString(dbData) : null;
    }
}
