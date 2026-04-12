package com.yemenptc.bss.coreservice.ivr;

import com.yemenptc.bss.coreservice.entity.Account;
import com.yemenptc.bss.coreservice.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IvrService {

    private static final Logger logger = LoggerFactory.getLogger(IvrService.class);

    private final Map<String, IvrCallSession> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, IvrMenu> menuConfigurations = new ConcurrentHashMap<>();
    private final AccountRepository accountRepository;

    public IvrService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public IvrCallSession startCall(String callerId, String calledNumber) {
        logger.info("Starting IVR call from {} to {}", callerId, calledNumber);
        
        String sessionId = UUID.randomUUID().toString();
        IvrCallSession session = new IvrCallSession();
        session.sessionId = sessionId;
        session.callerId = callerId;
        session.calledNumber = calledNumber;
        session.startTime = new Date();
        session.state = CallState.INITIATED;
        
        activeSessions.put(sessionId, session);
        
        IvrMenu mainMenu = getMainMenu();
        session.currentMenuId = mainMenu.menuId;
        
        return session;
    }

    public IvrMenu getMainMenu() {
        return menuConfigurations.computeIfAbsent("main", k -> {
            IvrMenu menu = new IvrMenu();
            menu.menuId = "main";
            menu.welcomeMessage = "Welcome to Yemen PTC. Press 1 for balance inquiry, 2 for payment, 3 for customer service, 4 for technical support, 0 for operator.";
            menu.timeoutSeconds = 30;
            menu.maxRetries = 3;
            
            IvrOption opt1 = new IvrOption();
            opt1.digit = "1";
            opt1.label = "Balance Inquiry";
            opt1.action = "BALANCE_INQUIRY";
            opt1.nextMenuId = "balance";
            
            IvrOption opt2 = new IvrOption();
            opt2.digit = "2";
            opt2.label = "Make Payment";
            opt2.action = "MAKE_PAYMENT";
            opt2.nextMenuId = "payment";
            
            IvrOption opt3 = new IvrOption();
            opt3.digit = "3";
            opt3.label = "Customer Service";
            opt3.action = "CUSTOMER_SERVICE";
            opt3.nextMenuId = "customer_service";
            
            IvrOption opt4 = new IvrOption();
            opt4.digit = "4";
            opt4.label = "Technical Support";
            opt4.action = "TECHNICAL_SUPPORT";
            opt4.nextMenuId = "technical_support";
            
            IvrOption opt0 = new IvrOption();
            opt0.digit = "0";
            opt0.label = "Operator";
            opt0.action = "CONNECT_OPERATOR";
            opt0.nextMenuId = null;
            
            menu.options = List.of(opt1, opt2, opt3, opt4, opt0);
            return menu;
        });
    }

    public IvrCallSession processDtmf(String sessionId, String digit) {
        IvrCallSession session = activeSessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        
        logger.info("Processing DTMF {} for session {}", digit, sessionId);
        
        IvrMenu menu = menuConfigurations.get(session.currentMenuId);
        if (menu == null) {
            menu = getMainMenu();
        }
        
        Optional<IvrOption> option = menu.options.stream()
            .filter(o -> o.digit.equals(digit))
            .findFirst();
        
        if (option.isPresent()) {
            IvrOption selected = option.get();
            session.lastAction = selected.action;
            session.lastInput = digit;
            
            if (selected.nextMenuId != null) {
                session.currentMenuId = selected.nextMenuId;
            } else {
                session.state = CallState.TRANSFERRING;
            }
        } else {
            session.errors++;
            if (session.errors >= menu.maxRetries) {
                session.state = CallState.FAILED;
                session.failureReason = "Max retries exceeded";
            }
        }
        
        return session;
    }

    public IvrCallSession endCall(String sessionId) {
        IvrCallSession session = activeSessions.remove(sessionId);
        if (session != null) {
            session.endTime = new Date();
            session.state = CallState.COMPLETED;
            logger.info("Call ended for session {}", sessionId);
        }
        return session;
    }

    public IvrCallSession getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }

    public List<IvrCallSession> getActiveSessions() {
        return new ArrayList<>(activeSessions.values());
    }

    public Map<String, Object> handleBalanceInquiry(String callerId) {
        logger.info("Processing balance inquiry for caller: {}", callerId);
        
        try {
            Optional<Account> account = accountRepository.findByAccountNumber(callerId);
            if (account.isEmpty()) {
                account = accountRepository.findById(java.util.UUID.fromString(callerId));
            }
            
            if (account.isPresent()) {
                Account acc = account.get();
                return Map.of(
                    "success", true,
                    "accountId", acc.getId().toString(),
                    "accountNumber", acc.getAccountNumber() != null ? acc.getAccountNumber() : "N/A",
                    "balance", acc.getBalance() != null ? acc.getBalance().toString() : "0.00",
                    "currency", acc.getCurrency() != null ? acc.getCurrency() : "YER",
                    "message", "Your current balance is " + (acc.getBalance() != null ? acc.getBalance().toString() : "0.00") + " " + (acc.getCurrency() != null ? acc.getCurrency() : "YER")
                );
            }
            
            return Map.of(
                "success", false,
                "message", "Account not found"
            );
        } catch (Exception e) {
            logger.error("Error processing balance inquiry: {}", e.getMessage());
            return Map.of(
                "success", false,
                "message", "Error retrieving balance"
            );
        }
    }

    public Map<String, Object> handlePayment(String callerId, BigDecimal amount) {
        logger.info("Processing payment request for caller: {}, amount: {}", callerId, amount);
        
        try {
            Optional<Account> account = accountRepository.findByAccountNumber(callerId);
            if (account.isEmpty()) {
                try {
                    account = accountRepository.findById(java.util.UUID.fromString(callerId));
                } catch (Exception ex) {
                    account = Optional.empty();
                }
            }
            
            if (account.isPresent()) {
                Account acc = account.get();
                BigDecimal currentBalance = acc.getBalance() != null ? acc.getBalance() : BigDecimal.ZERO;
                BigDecimal newBalance = currentBalance.add(amount);
                acc.setBalance(newBalance);
                accountRepository.save(acc);
                
                return Map.of(
                    "success", true,
                    "accountId", acc.getId().toString(),
                    "previousBalance", currentBalance.toString(),
                    "amountPaid", amount.toString(),
                    "newBalance", newBalance.toString(),
                    "message", "Payment of " + amount + " YER processed successfully"
                );
            }
            
            return Map.of(
                "success", false,
                "message", "Account not found"
            );
        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage());
            return Map.of(
                "success", false,
                "message", "Error processing payment"
            );
        }
    }

    public static class IvrCallSession {
        public String sessionId;
        public String callerId;
        public String calledNumber;
        public Date startTime;
        public Date endTime;
        public CallState state;
        public String currentMenuId;
        public String lastAction;
        public String lastInput;
        public int errors;
        public String failureReason;
    }

    public static class IvrMenu {
        public String menuId;
        public String welcomeMessage;
        public int timeoutSeconds;
        public int maxRetries;
        public List<IvrOption> options;
    }

    public static class IvrOption {
        public String digit;
        public String label;
        public String action;
        public String nextMenuId;
    }

    public enum CallState {
        INITIATED, IN_PROGRESS, WAITING_INPUT, TRANSFERRING, COMPLETED, FAILED
    }
}