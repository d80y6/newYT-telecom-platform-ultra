package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.User;
import com.yemenptc.bss.coreservice.service.UserService;
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
@RequestMapping("/tmf-api/userManagement/v5")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "TMF653 User Management API")
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    @Operation(summary = "Create user", description = "Creates a new user")
    public ResponseEntity<User> createUser(@RequestBody User request) {
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/user")
    @Operation(summary = "List users", description = "Retrieves all users with pagination")
    public ResponseEntity<Page<User>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<User> users = userService.listUsers(PageRequest.of(page, size));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "Get user", description = "Retrieves a user by ID")
    public ResponseEntity<User> getUser(@PathVariable UUID id) {
        User user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieves a user by username")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user")
    public ResponseEntity<User> updateUser(
            @PathVariable UUID id, 
            @RequestBody User request) {
        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/user/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/user/{id}/suspend")
    @Operation(summary = "Suspend user", description = "Suspends a user")
    public ResponseEntity<User> suspendUser(@PathVariable UUID id) {
        User user = userService.suspendUser(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/user/{id}/activate")
    @Operation(summary = "Activate user", description = "Activates a user")
    public ResponseEntity<User> activateUser(@PathVariable UUID id) {
        User user = userService.activateUser(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/user/{id}/password")
    @Operation(summary = "Change password", description = "Changes user password")
    public ResponseEntity<User> changePassword(
            @PathVariable UUID id, 
            @RequestBody String newPassword) {
        User user = userService.changePassword(id, newPassword);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user/role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieves users by role")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable User.UserRole role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/status/{status}")
    @Operation(summary = "Get users by status", description = "Retrieves users by status")
    public ResponseEntity<List<User>> getUsersByStatus(@PathVariable User.UserStatus status) {
        List<User> users = userService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }
}
