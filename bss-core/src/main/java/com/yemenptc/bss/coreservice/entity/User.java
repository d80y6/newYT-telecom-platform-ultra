package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class User extends BaseTmfEntity {

    @Column(name = "user_id", unique = true, nullable = false, length = 50)
    private String userId;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(unique = true, length = 200)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 30)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    @Column(name = "must_change_password")
    @Builder.Default
    private Boolean mustChangePassword = false;

    @Column(name = "department", length = 100)
    private String department;

    public enum UserRole {
        ADMIN, OPERATOR, AGENT, TECHNICIAN, VIEWER, CUSTOMER
    }

    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, LOCKED
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/userManagement/v5/user";
    }

    @PrePersist
    protected void onCreate() {
        if (userId == null) {
            userId = "USR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (status == null) {
            status = UserStatus.ACTIVE;
        }
    }
}
