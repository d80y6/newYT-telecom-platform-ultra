package com.yemenptc.bss.coreservice.security;

import lombok.*;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    
    private String id;
    private String name;
    private String resource;
    private String action;
    private String description;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionKey {
        private String resource;
        private String action;
    }
}
