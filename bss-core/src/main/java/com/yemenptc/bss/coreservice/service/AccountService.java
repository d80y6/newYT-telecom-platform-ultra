package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Account;
import com.yemenptc.bss.coreservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Account Service - TMF666 Account Management API
 * Manages billing and service accounts
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public Account createAccount(Account request) {
        log.info("Creating account for customer: {}", request.getCustomerId());
        
        Account account = Account.builder()
            .accountNumber("ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .customerId(request.getCustomerId())
            .accountType(request.getAccountType())
            .serviceCategory(request.getServiceCategory())
            .status(Account.AccountStatus.ACTIVE)
            .balance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO)
            .creditLimit(request.getCreditLimit())
            .currency(request.getCurrency() != null ? request.getCurrency() : "YER")
            .build();
        
        Account saved = accountRepository.save(account);
        log.info("Account created: {}", saved.getAccountNumber());
        return saved;
    }

    @Transactional(readOnly = true)
    public Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
    }

    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByCustomer(UUID customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByStatus(Account.AccountStatus status) {
        return accountRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public Page<Account> listAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Transactional
    public Account updateAccount(UUID accountId, Account request) {
        Account existing = getAccount(accountId);
        
        Account updated = Account.builder()
            .accountNumber(existing.getAccountNumber())
            .customerId(request.getCustomerId() != null ? request.getCustomerId() : existing.getCustomerId())
            .accountType(request.getAccountType() != null ? request.getAccountType() : existing.getAccountType())
            .serviceCategory(request.getServiceCategory() != null ? request.getServiceCategory() : existing.getServiceCategory())
            .status(request.getStatus() != null ? request.getStatus() : existing.getStatus())
            .balance(request.getBalance() != null ? request.getBalance() : existing.getBalance())
            .creditLimit(request.getCreditLimit() != null ? request.getCreditLimit() : existing.getCreditLimit())
            .currency(request.getCurrency() != null ? request.getCurrency() : existing.getCurrency())
            .build();
        
        updated.setId(accountId);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(Instant.now());
        updated.setVersion(existing.getVersion() + 1);
        
        return accountRepository.save(updated);
    }

    @Transactional
    public Account updateAccountStatus(UUID accountId, Account.AccountStatus status) {
        Account account = getAccount(accountId);
        account.setStatus(status);
        account.setUpdatedAt(Instant.now());
        account.setVersion(account.getVersion() + 1);
        return accountRepository.save(account);
    }

    @Transactional
    public Account addFunds(UUID accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }
        
        Account account = getAccount(accountId);
        BigDecimal newBalance = account.getBalance().add(amount);
        
        account.setBalance(newBalance);
        account.setUpdatedAt(Instant.now());
        
        log.info("Added {} to account {}, new balance: {}", amount, account.getAccountNumber(), newBalance);
        return accountRepository.save(account);
    }

    @Transactional
    public Account deductFunds(UUID accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }
        
        Account account = getAccount(accountId);
        
        BigDecimal availableBalance = account.getBalance();
        if (account.getCreditLimit() != null) {
            availableBalance = availableBalance.add(account.getCreditLimit());
        }
        
        if (availableBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        account.setUpdatedAt(Instant.now());
        
        log.info("Deducted {} from account {}, new balance: {}", amount, account.getAccountNumber(), newBalance);
        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Account not found: " + accountId);
        }
        accountRepository.deleteById(accountId);
    }
}
