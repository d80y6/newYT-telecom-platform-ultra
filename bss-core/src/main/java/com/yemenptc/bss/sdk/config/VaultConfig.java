package com.yemenptc.bss.sdk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.core.VaultTemplate;

/**
 * HashiCorp Vault integration for secrets management.
 * Only active in prod profile.
 */
@Configuration
@Profile("prod")
public class VaultConfig {

    @Bean
    public VaultTemplate vaultTemplate() {
        var endpoint = org.springframework.vault.client.VaultEndpoint.from(
                java.net.URI.create("http://vault:8200"));
        var auth = new org.springframework.vault.authentication.TokenAuthentication("token");
        return new VaultTemplate(endpoint, auth);
    }
}
