package com.yemenptc.bss.sdk.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Helper for storing TMF characteristics as JSONB in PostgreSQL.
 * Provides typed access to dynamic product/party attributes.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class TmfCharacteristics extends BaseTmfEntity {

    @Column(name = "characteristics", columnDefinition = "jsonb")
    private Map<String, Object> characteristics;

    @SuppressWarnings("unchecked")
    public <T> T getCharacteristic(String name) {
        if (characteristics == null) {
            return null;
        }
        return (T) characteristics.get(name);
    }

    public void setCharacteristic(String name, Object value) {
        if (characteristics == null) {
            characteristics = new java.util.HashMap<>();
        }
        characteristics.put(name, value);
    }

    public boolean hasCharacteristic(String name) {
        return characteristics != null && characteristics.containsKey(name);
    }
}
