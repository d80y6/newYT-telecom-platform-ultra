package com.yemenptc.bss.sdk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * Keycloak configuration for TMF720 Digital Identity.
 * OAuth2/OIDC integration with MFA (FIDO2) support.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakConfig {

    private String serverUrl = "http://keycloak:8080";
    private String realm = "yemenptc";
    private String clientId = "bss-platform";
    private String clientSecret;
    private boolean sslRequired = false;
    private boolean bearerOnly = true;
    private boolean publicClient = false;
    private String credentialsSecret;

    /**
     * Keycloak realm roles for TMF party roles.
     */
    public static final String ROLE_SUBSCRIBER = "tmf-subscriber";
    public static final String ROLE_PAYER = "tmf-payer";
    public static final String ROLE_ADMIN = "tmf-admin";
    public static final String ROLE_TECHNICIAN = "tmf-technician";
    public static final String ROLE_SUPPORT = "tmf-support";
}
