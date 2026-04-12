package com.yemenptc.bss.coreservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Yemen PTC BSS/OSS Platform API")
                .version("1.0.0")
                .description("""
                    BSS/OSS Platform REST API for Yemen PTC Telecom
                    
                    ## TMF Compliance
                    This API follows TM Forum Open API standards:
                    - TMF629: Customer Management
                    - TMF620: Product Catalog  
                    - TMF622: Product Order
                    - TMF641: Service Order
                    - TMF637: Product Inventory
                    
                    ## Features
                    - Customer Management
                    - Account Management
                    - Order Processing
                    - Billing & Invoicing
                    - Payment Processing
                    - Subscription Management
                    - Product Catalog
                    """)
                .contact(new Contact()
                    .name("Yemen PTC")
                    .email("api@yemenptc.com.ye")
                    .url("https://www.yemenptc.com.ye"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server().url("http://localhost:8080" + contextPath).description("Local Development"),
                new Server().url("https://api.yemenptc.com" + contextPath).description("Production")
            ))
            .components(new Components());
    }
}
