package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.User;
import com.yemenptc.bss.coreservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User createUser(User request) {
        log.info("Creating user: {}", request.getUsername());

        User user = User.builder()
            .userId(request.getUserId() != null ? 
                request.getUserId() : "USR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .username(request.getUsername())
            .password(request.getPassword())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phoneNumber(request.getPhoneNumber())
            .role(request.getRole() != null ? request.getRole() : User.UserRole.VIEWER)
            .status(User.UserStatus.ACTIVE)
            .failedLoginAttempts(0)
            .mustChangePassword(false)
            .department(request.getDepartment())
            .build();

        User saved = userRepository.save(user);
        log.info("User created: {}", saved.getUserId());
        return saved;
    }

    @Transactional(readOnly = true)
    public User getUser(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Transactional(readOnly = true)
    public Page<User> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByStatus(User.UserStatus status) {
        return userRepository.findByStatus(status);
    }

    @Transactional
    public User updateUser(UUID id, User request) {
        User user = getUser(id);
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getDepartment() != null) user.setDepartment(request.getDepartment());
        return userRepository.save(user);
    }

    @Transactional
    public User changePassword(UUID id, String newPassword) {
        User user = getUser(id);
        user.setPassword(newPassword);
        user.setPasswordChangedAt(Instant.now());
        user.setMustChangePassword(false);
        log.info("Password changed for user: {}", user.getUserId());
        return userRepository.save(user);
    }

    @Transactional
    public User suspendUser(UUID id) {
        User user = getUser(id);
        user.setStatus(User.UserStatus.SUSPENDED);
        log.info("User suspended: {}", user.getUserId());
        return userRepository.save(user);
    }

    @Transactional
    public User activateUser(UUID id) {
        User user = getUser(id);
        user.setStatus(User.UserStatus.ACTIVE);
        log.info("User activated: {}", user.getUserId());
        return userRepository.save(user);
    }

    @Transactional
    public User lockUser(UUID id, Instant lockedUntil) {
        User user = getUser(id);
        user.setStatus(User.UserStatus.LOCKED);
        user.setLockedUntil(lockedUntil);
        log.info("User locked: {}", user.getUserId());
        return userRepository.save(user);
    }

    @Transactional
    public User recordFailedLogin(UUID id) {
        User user = getUser(id);
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        
        if (user.getFailedLoginAttempts() >= 5) {
            user.setStatus(User.UserStatus.LOCKED);
            user.setLockedUntil(Instant.now().plusSeconds(900));
            log.warn("User locked due to failed attempts: {}", user.getUserId());
        }
        
        return userRepository.save(user);
    }

    @Transactional
    public User recordSuccessfulLogin(UUID id) {
        User user = getUser(id);
        user.setLastLoginAt(Instant.now());
        user.setFailedLoginAttempts(0);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = getUser(id);
        userRepository.delete(user);
        log.info("User deleted: {}", user.getUserId());
    }

    @Transactional(readOnly = true)
    public long countByRole(User.UserRole role) {
        return userRepository.countByRole(role);
    }

    @Transactional(readOnly = true)
    public long countByStatus(User.UserStatus status) {
        return userRepository.countByStatus(status);
    }
}
