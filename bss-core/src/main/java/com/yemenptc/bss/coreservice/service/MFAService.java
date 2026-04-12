package com.yemenptc.bss.coreservice.service;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class MFAService {

    private final CodeVerifier codeVerifier;
    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final TimeProvider timeProvider = new SystemTimeProvider();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator();

    public String generateSecret() {
        return secretGenerator.generate();
    }

    public String generateQrCodeUrl(String secret, String email, String issuer) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
            issuer, email, secret, issuer);
    }

    public boolean verifyCode(String secret, String code) {
        try {
            return codeVerifier.isValidCode(secret, code);
        } catch (Exception e) {
            log.error("Error verifying MFA code: {}", e.getMessage());
            return false;
        }
    }

    public List<String> generateBackupCodes() {
        Set<String> codes = new HashSet<>();
        SecureRandom random = new SecureRandom();
        
        while (codes.size() < 10) {
            int code = 100000 + random.nextInt(900000);
            codes.add(String.valueOf(code));
        }
        
        return new ArrayList<>(codes);
    }

    public boolean verifyBackupCode(List<String> backupCodes, String code) {
        return backupCodes.contains(code);
    }

    public List<String> consumeBackupCode(List<String> backupCodes, String code) {
        List<String> updated = new ArrayList<>(backupCodes);
        updated.remove(code);
        return updated;
    }
}
