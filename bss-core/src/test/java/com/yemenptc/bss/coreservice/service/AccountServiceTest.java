package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Account;
import com.yemenptc.bss.coreservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account createTestAccount() {
        Account account = new Account();
        account.setCustomerId(UUID.randomUUID());
        account.setAccountType(Account.AccountType.BILLING);
        account.setServiceCategory(Account.ServiceCategory.MOBILE);
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency("YER");
        return account;
    }

    @Test
    void createAccount_Success() {
        Account request = createTestAccount();
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> {
            Account a = i.getArgument(0);
            a.setId(UUID.randomUUID());
            a.setAccountNumber("ACC-TEST123");
            return a;
        });

        Account result = accountService.createAccount(request);

        assertNotNull(result);
        assertEquals(Account.AccountStatus.ACTIVE, result.getStatus());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void getAccount_Success() {
        UUID id = UUID.randomUUID();
        Account account = createTestAccount();
        account.setId(id);
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        Account result = accountService.getAccount(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void addFunds_Success() {
        UUID id = UUID.randomUUID();
        Account account = createTestAccount();
        account.setId(id);
        account.setBalance(BigDecimal.valueOf(1000));
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account result = accountService.addFunds(id, BigDecimal.valueOf(500));

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1500), result.getBalance());
    }

    @Test
    void deductFunds_Success() {
        UUID id = UUID.randomUUID();
        Account account = createTestAccount();
        account.setId(id);
        account.setBalance(BigDecimal.valueOf(1000));
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account result = accountService.deductFunds(id, BigDecimal.valueOf(300));

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(700), result.getBalance());
    }

    @Test
    void deductFunds_InsufficientFunds() {
        UUID id = UUID.randomUUID();
        Account account = createTestAccount();
        account.setId(id);
        account.setBalance(BigDecimal.valueOf(100));
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        assertThrows(RuntimeException.class, () -> accountService.deductFunds(id, BigDecimal.valueOf(500)));
    }

    @Test
    void listAccounts_Success() {
        Page<Account> page = new PageImpl<>(List.of());
        when(accountRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Account> result = accountService.listAccounts(PageRequest.of(0, 20));

        assertNotNull(result);
    }
}
