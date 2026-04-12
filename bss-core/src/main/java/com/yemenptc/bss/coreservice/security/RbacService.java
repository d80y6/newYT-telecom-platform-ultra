package com.yemenptc.bss.coreservice.security;

import com.yemenptc.bss.coreservice.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RbacService {

    private final Map<String, Set<String>> userPermissions = new ConcurrentHashMap<>();
    private final Map<String, Role> roleStore = new ConcurrentHashMap<>();

    public boolean hasPermission(String userId, String resource, String action) {
        Set<String> permissions = userPermissions.get(userId);
        if (permissions == null) {
            permissions = getDefaultPermissions(userId);
        }
        
        String permissionKey = resource + ":" + action;
        boolean hasPermission = permissions.contains(permissionKey) || permissions.contains(resource + ":*");
        
        log.debug("Permission check: user={} resource={} action={} result={}", 
            userId, resource, action, hasPermission);
        
        return hasPermission;
    }

    public void grantPermission(String userId, String resource, String action) {
        userPermissions.computeIfAbsent(userId, k -> new HashSet<>())
            .add(resource + ":" + action);
        log.info("Granted permission: user={} resource={} action={}", userId, resource, action);
    }

    public void revokePermission(String userId, String resource, String action) {
        Set<String> permissions = userPermissions.get(userId);
        if (permissions != null) {
            permissions.remove(resource + ":" + action);
            log.info("Revoked permission: user={} resource={} action={}", userId, resource, action);
        }
    }

    public Set<String> getUserPermissions(String userId) {
        return userPermissions.computeIfAbsent(userId, this::getDefaultPermissions);
    }

    public List<String> filterAccessibleResources(String userId, List<String> resources) {
        Set<String> permissions = getUserPermissions(userId);
        
        return resources.stream()
            .filter(r -> permissions.contains(r + ":*") || 
                        permissions.stream().anyMatch(p -> p.startsWith(r + ":")))
            .collect(Collectors.toList());
    }

    private Set<String> getDefaultPermissions(String userId) {
        Set<String> defaults = new HashSet<>();
        
        defaults.add("customer:read");
        defaults.add("customer:write");
        defaults.add("order:read");
        defaults.add("order:write");
        defaults.add("billing:read");
        defaults.add("usage:read");
        defaults.add("service:read");
        
        if (userId.startsWith("admin")) {
            defaults.add("*");
        }
        
        return defaults;
    }

    public void createRole(String roleName, Set<String> permissions) {
        Role role = Role.builder()
            .roleName(roleName)
            .permissions(permissions)
            .build();
        roleStore.put(roleName, role);
        log.info("Created role: {} with {} permissions", roleName, permissions.size());
    }

    public Optional<Role> getRole(String roleName) {
        return Optional.ofNullable(roleStore.get(roleName));
    }

    public void assignRole(String userId, String roleName) {
        Role role = roleStore.get(roleName);
        if (role != null) {
            userPermissions.computeIfAbsent(userId, k -> new HashSet<>())
                .addAll(role.getPermissions());
            log.info("Assigned role: user={} role={}", userId, roleName);
        }
    }
}
