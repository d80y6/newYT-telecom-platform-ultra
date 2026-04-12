package com.yemenptc.bss.coreservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    public static final String ROLE_CUSTOMER_SERVICE_REP = "CUSTOMER_SERVICE_REP";
    public static final String ROLE_NETWORK_ENGINEER = "NETWORK_ENGINEER";
    public static final String ROLE_BILLING_ADMIN = "BILLING_ADMIN";
    public static final String ROLE_SYSTEM_OPERATOR = "SYSTEM_OPERATOR";
    public static final String ROLE_AUDITOR = "AUDITOR";

    @Value("${keycloak.realm:yemenptc-bss}")
    private String realm;

    @Value("${keycloak.auth-server-url:http://keycloak:8080/}")
    private String authServerUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/health", "/api/v1/metrics", "/api-docs/**", "/swagger-ui/**", "/actuator/**").permitAll()
                
                .requestMatchers("/tmf-api/customerManagement/**", "/tmf-api/productOrderingManagement/**", "/tmf-api/serviceOrderingManagement/**").hasAnyRole(ROLE_CUSTOMER_SERVICE_REP, ROLE_BILLING_ADMIN)
                
                .requestMatchers("/tmf-api/resourceInventoryManagement/**", "/tmf-api/serviceInventoryManagement/**").hasAnyRole(ROLE_NETWORK_ENGINEER, ROLE_CUSTOMER_SERVICE_REP)
                
                .requestMatchers("/tmf-api/billing/**", "/tmf-api/customerBillManagement/**", "/tmf-api/usageManagement/**").hasRole(ROLE_BILLING_ADMIN)
                
                .requestMatchers("/tmf-api/alarmManagement/**", "/tmf-api/troubleTicketManagement/**", "/tmf-api/notificationListener/**").hasAnyRole(ROLE_SYSTEM_OPERATOR, ROLE_NETWORK_ENGINEER, ROLE_CUSTOMER_SERVICE_REP)
                
                .requestMatchers("/audit/**").hasRole(ROLE_AUDITOR)
                
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(authServerUrl + "realms/" + realm + "/protocol/openid-connect/certs").build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access/roles");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authenticationConverter;
    }
}