package com.yemenptc.bss.sdk.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Base entity for all TM Forum domain objects.
 * Provides standard TMF fields: id (UUID v7), href, timestamps, version.
 */
@MappedSuperclass
public abstract class BaseTmfEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(length = 512)
    private String href;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private Integer version;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "characteristics", columnDefinition = "jsonb")
    private Map<String, Object> characteristics;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getHref() { return href; }
    public void setHref(String href) { this.href = href; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public Map<String, Object> getCharacteristics() { return characteristics; }
    public void setCharacteristics(Map<String, Object> characteristics) { this.characteristics = characteristics; }

    @SuppressWarnings("unchecked")
    public <T> T getCharacteristic(String name) {
        if (characteristics == null) return null;
        return (T) characteristics.get(name);
    }

    public void setCharacteristic(String name, Object value) {
        if (characteristics == null) characteristics = new java.util.HashMap<>();
        characteristics.put(name, value);
    }

    public boolean hasCharacteristic(String name) {
        return characteristics != null && characteristics.containsKey(name);
    }

    @PrePersist
    protected void prePersist() {
        if (this.id == null) this.id = generateUuidV7();
        if (this.href == null) this.href = buildHref();
    }

    @PreUpdate
    protected void preUpdate() {
        this.href = buildHref();
    }

    private UUID generateUuidV7() {
        long epochMillis = Instant.now().toEpochMilli();
        long msb = (epochMillis << 16) | (System.nanoTime() & 0xFFFFL);
        long lsb = UUID.randomUUID().getLeastSignificantBits();
        return new UUID(msb, lsb);
    }

    protected String buildHref() {
        return getApiPath() + "/" + this.id;
    }

    protected abstract String getApiPath();
}
