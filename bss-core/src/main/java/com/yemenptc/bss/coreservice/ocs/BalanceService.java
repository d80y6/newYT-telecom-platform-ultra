package com.yemenptc.bss.coreservice.ocs;

import com.yemenptc.bss.coreservice.entity.Account;
import com.yemenptc.bss.coreservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final AccountRepository accountRepository;
    
    private static final String BALANCE_KEY_PREFIX = "balance:";
    private static final String RESERVATION_KEY_PREFIX = "reservation:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(24);
    
    private static final String DEDUCT_SCRIPT = """
        local balanceKey = KEYS[1]
        local amount = tonumber(ARGV[1])
        local reservationId = ARGV[2]
        local ttl = tonumber(ARGV[3])
        
        local currentBalance = tonumber(redis.call('GET', balanceKey) or '0')
        
        if currentBalance >= amount then
            local newBalance = currentBalance - amount
            redis.call('SET', balanceKey, tostring(newBalance))
            redis.call('HSET', 'reservation:' .. reservationId, 'originalBalance', tostring(currentBalance), 'reservedAmount', tostring(amount))
            redis.call('EXPIRE', 'reservation:' .. reservationId, ttl)
            return tostring(newBalance)
        else
            return 'INSUFFICIENT_BALANCE'
        end
        """;

    public BigDecimal getBalance(String accountId) {
        String key = BALANCE_KEY_PREFIX + accountId;
        String cached = redisTemplate.opsForValue().get(key);
        
        if (cached != null) {
            try {
                return new BigDecimal(cached);
            } catch (NumberFormatException e) {
                log.warn("Invalid balance in cache for {}: {}", accountId, cached);
            }
        }
        
        Account account = accountRepository.findByAccountNumber(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        
        BigDecimal balance = account.getBalance();
        redisTemplate.opsForValue().set(key, balance.toString(), DEFAULT_TTL);
        
        return balance;
    }

    @Transactional
    public BalanceResult reserveBalance(String accountId, BigDecimal amount, String serviceType) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BalanceResult.builder()
                    .success(false)
                    .errorCode("INVALID_AMOUNT")
                    .errorMessage("Amount must be positive")
                    .build();
        }
        
        String reservationId = UUID.randomUUID().toString();
        String balanceKey = BALANCE_KEY_PREFIX + accountId;
        
        try {
            DefaultRedisScript<String> script = new DefaultRedisScript<>();
            script.setScriptText(DEDUCT_SCRIPT);
            script.setResultType(String.class);
            
            String result = redisTemplate.execute(
                    script,
                    Collections.singletonList(balanceKey),
                    amount.toString(),
                    reservationId,
                    String.valueOf(DEFAULT_TTL.toSeconds())
            );
            
            if ("INSUFFICIENT_BALANCE".equals(result)) {
                log.warn("Insufficient balance for account {}: requested {}", accountId, amount);
                return BalanceResult.builder()
                        .success(false)
                        .errorCode("INSUFFICIENT_BALANCE")
                        .errorMessage("Account balance is insufficient")
                        .currentBalance(getBalance(accountId))
                        .requestedAmount(amount)
                        .build();
            }
            
            BigDecimal newBalance = new BigDecimal(result);
            
            redisTemplate.opsForHash().put(
                    RESERVATION_KEY_PREFIX + reservationId,
                    "accountId", accountId);
            redisTemplate.opsForHash().put(
                    RESERVATION_KEY_PREFIX + reservationId,
                    "amount", amount.toString());
            redisTemplate.opsForHash().put(
                    RESERVATION_KEY_PREFIX + reservationId,
                    "serviceType", serviceType);
            redisTemplate.opsForHash().put(
                    RESERVATION_KEY_PREFIX + reservationId,
                    "status", "RESERVED");
            redisTemplate.opsForHash().put(
                    RESERVATION_KEY_PREFIX + reservationId,
                    "createdAt", Instant.now().toString());
            redisTemplate.expire(RESERVATION_KEY_PREFIX + reservationId, DEFAULT_TTL);
            
            log.info("Reserved {} for account {}, reservation {}", amount, accountId, reservationId);
            
            return BalanceResult.builder()
                    .success(true)
                    .reservationId(reservationId)
                    .accountId(accountId)
                    .reservedAmount(amount)
                    .newBalance(newBalance)
                    .expiryTime(Instant.now().plus(DEFAULT_TTL))
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to reserve balance for {}: {}", accountId, e.getMessage());
            return BalanceResult.builder()
                    .success(false)
                    .errorCode("RESERVATION_FAILED")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Transactional
    public BalanceResult commitReservation(String reservationId) {
        Map<Object, Object> reservation = redisTemplate.opsForHash()
                .entries(RESERVATION_KEY_PREFIX + reservationId);
        
        if (reservation.isEmpty()) {
            return BalanceResult.builder()
                    .success(false)
                    .errorCode("RESERVATION_NOT_FOUND")
                    .errorMessage("Reservation not found or expired")
                    .build();
        }
        
        String status = (String) reservation.get("status");
        if (!"RESERVED".equals(status)) {
            return BalanceResult.builder()
                    .success(false)
                    .errorCode("INVALID_RESERVATION_STATUS")
                    .errorMessage("Reservation is not in RESERVED state: " + status)
                    .build();
        }
        
        String accountId = (String) reservation.get("accountId");
        String amountStr = (String) reservation.get("amount");
        BigDecimal amount = new BigDecimal(amountStr);
        
        redisTemplate.opsForHash().put(
                RESERVATION_KEY_PREFIX + reservationId,
                "status", "COMMITTED");
        redisTemplate.opsForHash().put(
                RESERVATION_KEY_PREFIX + reservationId,
                "committedAt", Instant.now().toString());
        
        Account account = accountRepository.findByAccountNumber(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        account.setUpdatedAt(Instant.now());
        accountRepository.save(account);
        
        String balanceKey = BALANCE_KEY_PREFIX + accountId;
        redisTemplate.opsForValue().set(balanceKey, newBalance.toString());
        
        log.info("Committed reservation {} for account {}, amount {}", reservationId, accountId, amount);
        
        return BalanceResult.builder()
                .success(true)
                .reservationId(reservationId)
                .accountId(accountId)
                .committedAmount(amount)
                .newBalance(newBalance)
                .build();
    }

    @Transactional
    public BalanceResult rollbackReservation(String reservationId) {
        Map<Object, Object> reservation = redisTemplate.opsForHash()
                .entries(RESERVATION_KEY_PREFIX + reservationId);
        
        if (reservation.isEmpty()) {
            log.warn("Reservation {} not found for rollback", reservationId);
            return BalanceResult.builder()
                    .success(true)
                    .message("Reservation already expired or not found - no rollback needed")
                    .build();
        }
        
        String status = (String) reservation.get("status");
        if ("COMMITTED".equals(status)) {
            return BalanceResult.builder()
                    .success(false)
                    .errorCode("CANNOT_ROLLBACK_COMMITTED")
                    .errorMessage("Cannot rollback a committed reservation")
                    .build();
        }
        
        if ("ROLLED_BACK".equals(status)) {
            return BalanceResult.builder()
                    .success(true)
                    .message("Reservation already rolled back")
                    .reservationId(reservationId)
                    .build();
        }
        
        String accountId = (String) reservation.get("accountId");
        String amountStr = (String) reservation.get("amount");
        BigDecimal amount = new BigDecimal(amountStr);
        
        String balanceKey = BALANCE_KEY_PREFIX + accountId;
        String currentBalanceStr = redisTemplate.opsForValue().get(balanceKey);
        BigDecimal currentBalance = currentBalanceStr != null ? 
                new BigDecimal(currentBalanceStr) : getBalance(accountId);
        
        BigDecimal restoredBalance = currentBalance.add(amount);
        redisTemplate.opsForValue().set(balanceKey, restoredBalance.toString(), DEFAULT_TTL);
        
        redisTemplate.opsForHash().put(
                RESERVATION_KEY_PREFIX + reservationId,
                "status", "ROLLED_BACK");
        redisTemplate.opsForHash().put(
                RESERVATION_KEY_PREFIX + reservationId,
                "rolledBackAt", Instant.now().toString());
        
        log.info("Rolled back reservation {} for account {}, amount {} restored", 
                reservationId, accountId, amount);
        
        return BalanceResult.builder()
                .success(true)
                .reservationId(reservationId)
                .accountId(accountId)
                .restoredAmount(amount)
                .newBalance(restoredBalance)
                .build();
    }

    @Transactional
    public BalanceResult deductImmediate(String accountId, BigDecimal amount, String reason) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BalanceResult.builder()
                    .success(false)
                    .errorCode("INVALID_AMOUNT")
                    .errorMessage("Amount must be positive")
                    .build();
        }
        
        try {
            BigDecimal currentBalance = getBalance(accountId);
            
            if (currentBalance.compareTo(amount) < 0) {
                return BalanceResult.builder()
                        .success(false)
                        .errorCode("INSUFFICIENT_BALANCE")
                        .errorMessage("Account balance is insufficient")
                        .currentBalance(currentBalance)
                        .requestedAmount(amount)
                        .build();
            }
            
            BigDecimal newBalance = currentBalance.subtract(amount);
            
            Account account = accountRepository.findByAccountNumber(accountId)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
            account.setBalance(newBalance);
            account.setUpdatedAt(Instant.now());
            accountRepository.save(account);
            
            String balanceKey = BALANCE_KEY_PREFIX + accountId;
            redisTemplate.opsForValue().set(balanceKey, newBalance.toString(), DEFAULT_TTL);
            
            String transactionId = UUID.randomUUID().toString();
            log.info("Immediate deduction {} from {} for {}, transaction {}", 
                    amount, accountId, reason, transactionId);
            
            return BalanceResult.builder()
                    .success(true)
                    .accountId(accountId)
                    .deductedAmount(amount)
                    .newBalance(newBalance)
                    .transactionId(transactionId)
                    .build();
            
        } catch (Exception e) {
            log.error("Failed immediate deduction for {}: {}", accountId, e.getMessage());
            return BalanceResult.builder()
                    .success(false)
                    .errorCode("DEDUCTION_FAILED")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Transactional
    public BalanceResult recharge(String accountId, BigDecimal amount, String paymentMethod) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BalanceResult.builder()
                    .success(false)
                    .errorCode("INVALID_AMOUNT")
                    .errorMessage("Amount must be positive")
                    .build();
        }
        
        try {
            Account account = accountRepository.findByAccountNumber(accountId)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
            
            BigDecimal oldBalance = account.getBalance();
            BigDecimal newBalance = oldBalance.add(amount);
            
            account.setBalance(newBalance);
            account.setUpdatedAt(Instant.now());
            accountRepository.save(account);
            
            String balanceKey = BALANCE_KEY_PREFIX + accountId;
            redisTemplate.opsForValue().set(balanceKey, newBalance.toString(), DEFAULT_TTL);
            
            String rechargeId = UUID.randomUUID().toString();
            log.info("Recharged {} to account {}, new balance {}, recharge {}", 
                    amount, accountId, newBalance, rechargeId);
            
            return BalanceResult.builder()
                    .success(true)
                    .accountId(accountId)
                    .rechargedAmount(amount)
                    .oldBalance(oldBalance)
                    .newBalance(newBalance)
                    .paymentMethod(paymentMethod)
                    .rechargeId(rechargeId)
                    .build();
            
        } catch (Exception e) {
            log.error("Failed recharge for {}: {}", accountId, e.getMessage());
            return BalanceResult.builder()
                    .success(false)
                    .errorCode("RECHARGE_FAILED")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    public ReservationStatus getReservationStatus(String reservationId) {
        Map<Object, Object> reservation = redisTemplate.opsForHash()
                .entries(RESERVATION_KEY_PREFIX + reservationId);
        
        if (reservation.isEmpty()) {
            return null;
        }
        
        return ReservationStatus.builder()
                .reservationId(reservationId)
                .accountId((String) reservation.get("accountId"))
                .amount(new BigDecimal((String) reservation.get("amount")))
                .serviceType((String) reservation.get("serviceType"))
                .status((String) reservation.get("status"))
                .createdAt(Instant.parse((String) reservation.get("createdAt")))
                .committedAt(reservation.get("committedAt") != null ? 
                        Instant.parse((String) reservation.get("committedAt")) : null)
                .rolledBackAt(reservation.get("rolledBackAt") != null ? 
                        Instant.parse((String) reservation.get("rolledBackAt")) : null)
                .build();
    }

    @lombok.Builder
    @lombok.Data
    public static class BalanceResult {
        private boolean success;
        private String errorCode;
        private String errorMessage;
        private String reservationId;
        private String transactionId;
        private String rechargeId;
        private String accountId;
        private BigDecimal requestedAmount;
        private BigDecimal reservedAmount;
        private BigDecimal committedAmount;
        private BigDecimal restoredAmount;
        private BigDecimal deductedAmount;
        private BigDecimal rechargedAmount;
        private BigDecimal currentBalance;
        private BigDecimal oldBalance;
        private BigDecimal newBalance;
        private String paymentMethod;
        private Instant expiryTime;
        private String message;
    }

    @lombok.Builder
    @lombok.Data
    public static class ReservationStatus {
        private String reservationId;
        private String accountId;
        private BigDecimal amount;
        private String serviceType;
        private String status;
        private Instant createdAt;
        private Instant committedAt;
        private Instant rolledBackAt;
    }
}
