package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.IdentityManagement;
import com.yemenptc.bss.coreservice.service.IdentityManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/identityManagement/v5")
@RequiredArgsConstructor
@Tag(name = "Identity Management", description = "TMF656 Identity Management API")
public class IdentityManagementController {

    private final IdentityManagementService identityService;

    @PostMapping("/identity")
    @Operation(summary = "Create identity", description = "Creates a new identity")
    public ResponseEntity<IdentityManagement> createIdentity(@RequestBody IdentityManagement request) {
        IdentityManagement identity = identityService.createIdentity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(identity);
    }

    @GetMapping("/identity")
    @Operation(summary = "List identities", description = "Retrieves all identities with pagination")
    public ResponseEntity<Page<IdentityManagement>> listIdentities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<IdentityManagement> identities = identityService.listIdentities(PageRequest.of(page, size));
        return ResponseEntity.ok(identities);
    }

    @GetMapping("/identity/{id}")
    @Operation(summary = "Get identity", description = "Retrieves an identity by ID")
    public ResponseEntity<IdentityManagement> getIdentity(@PathVariable UUID id) {
        IdentityManagement identity = identityService.getIdentity(id);
        return ResponseEntity.ok(identity);
    }

    @GetMapping("/identity/identityId/{identityId}")
    @Operation(summary = "Get identity by identity ID", description = "Retrieves an identity by identity ID")
    public ResponseEntity<IdentityManagement> getIdentityByIdentityId(@PathVariable String identityId) {
        IdentityManagement identity = identityService.getIdentityByIdentityId(identityId);
        return ResponseEntity.ok(identity);
    }

    @PutMapping("/identity/{id}")
    @Operation(summary = "Update identity", description = "Updates an existing identity")
    public ResponseEntity<IdentityManagement> updateIdentity(
            @PathVariable UUID id, 
            @RequestBody IdentityManagement request) {
        IdentityManagement identity = identityService.updateIdentity(id, request);
        return ResponseEntity.ok(identity);
    }

    @DeleteMapping("/identity/{id}")
    @Operation(summary = "Delete identity", description = "Deletes an identity")
    public ResponseEntity<Void> deleteIdentity(@PathVariable UUID id) {
        identityService.deleteIdentity(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/identity/{id}/verify")
    @Operation(summary = "Verify identity", description = "Verifies an identity")
    public ResponseEntity<IdentityManagement> verifyIdentity(@PathVariable UUID id) {
        IdentityManagement identity = identityService.verifyIdentity(id);
        return ResponseEntity.ok(identity);
    }

    @PatchMapping("/identity/{id}/primary")
    @Operation(summary = "Set as primary", description = "Sets an identity as primary")
    public ResponseEntity<IdentityManagement> setPrimary(@PathVariable UUID id) {
        IdentityManagement identity = identityService.setPrimary(id);
        return ResponseEntity.ok(identity);
    }

    @PatchMapping("/identity/{id}/revoke")
    @Operation(summary = "Revoke identity", description = "Revokes an identity")
    public ResponseEntity<IdentityManagement> revokeIdentity(@PathVariable UUID id) {
        IdentityManagement identity = identityService.revokeIdentity(id);
        return ResponseEntity.ok(identity);
    }

    @GetMapping("/identity/party/{partyId}")
    @Operation(summary = "Get identities by party", description = "Retrieves identities for a party")
    public ResponseEntity<List<IdentityManagement>> getIdentitiesByParty(@PathVariable UUID partyId) {
        List<IdentityManagement> identities = identityService.getIdentitiesByParty(partyId);
        return ResponseEntity.ok(identities);
    }

    @GetMapping("/identity/type/{identityType}")
    @Operation(summary = "Get identities by type", description = "Retrieves identities by type")
    public ResponseEntity<List<IdentityManagement>> getIdentitiesByType(@PathVariable IdentityManagement.IdentityType identityType) {
        List<IdentityManagement> identities = identityService.getIdentitiesByType(identityType);
        return ResponseEntity.ok(identities);
    }

    @GetMapping("/identity/status/{status}")
    @Operation(summary = "Get identities by status", description = "Retrieves identities by status")
    public ResponseEntity<List<IdentityManagement>> getIdentitiesByStatus(@PathVariable IdentityManagement.IdentityStatus status) {
        List<IdentityManagement> identities = identityService.getIdentitiesByStatus(status);
        return ResponseEntity.ok(identities);
    }
}
