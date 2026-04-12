package com.yemenptc.bss.coreservice.entity;

import com.yemenptc.bss.sdk.entity.BaseTmfEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_portal_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PortalSession extends BaseTmfEntity {

    @Column(name = "customer_id", nullable = false, length = 50)
    private String customerId;

    @Column(name = "session_token", unique = true, length = 200)
    private String sessionToken;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private SessionStatus status = SessionStatus.ACTIVE;

    @Column(name = "login_time")
    private LocalDateTime loginTime;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    public enum SessionStatus {
        ACTIVE, EXPIRED, LOGGED_OUT
    }

    @Override
    protected String getApiPath() {
        return "/tmf-api/customerPortal/v5/session";
    }
}