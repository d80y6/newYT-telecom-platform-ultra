package com.yemenptc.bss.migration.extractors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Extracts data from legacy TITAN system (Oracle DB).
 * Handles ASCII CDRs and proprietary schema.
 */
@Slf4j
@Component
public class TitanExtractor {

    private final JdbcTemplate jdbcTemplate;

    public TitanExtractor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Extracts customers from TITAN exchange codes.
     */
    public List<Map<String, Object>> extractCustomers(String exchangeCode) {
        log.info("Extracting customers from TITAN exchange: {}", exchangeCode);

        String sql = """
            SELECT customer_id, name, address, phone_number, 
                   service_type, status, created_date
            FROM titan.customers 
            WHERE exchange_code = ?
            AND status = 'ACTIVE'
        """;

        return jdbcTemplate.queryForList(sql, exchangeCode);
    }

    /**
     * Extracts PSTN line records.
     */
    public List<Map<String, Object>> extractLines(String exchangeCode) {
        log.info("Extracting PSTN lines from TITAN exchange: {}", exchangeCode);

        String sql = """
            SELECT line_id, customer_id, directory_number, 
                   line_type, features, status
            FROM titan.lines 
            WHERE exchange_code = ?
            AND status = 'ACTIVE'
        """;

        return jdbcTemplate.queryForList(sql, exchangeCode);
    }

    /**
     * Extracts CDR history for billing period.
     */
    public List<Map<String, Object>> extractCdrs(String exchangeCode, String startDate, String endDate) {
        log.info("Extracting CDRs from TITAN: {} ({} to {})", exchangeCode, startDate, endDate);

        String sql = """
            SELECT cdr_id, originating_number, terminating_number,
                   start_time, duration_seconds, call_type, rated_amount
            FROM titan.cdrs 
            WHERE exchange_code = ?
            AND start_time BETWEEN ? AND ?
            ORDER BY start_time
        """;

        return jdbcTemplate.queryForList(sql, exchangeCode, startDate, endDate);
    }

    /**
     * Extracts billing accounts with balances.
     */
    public List<Map<String, Object>> extractAccounts(String exchangeCode) {
        log.info("Extracting billing accounts from TITAN exchange: {}", exchangeCode);

        String sql = """
            SELECT account_id, customer_id, account_number,
                   balance, credit_limit, payment_method, status
            FROM titan.accounts 
            WHERE exchange_code = ?
            AND status != 'CLOSED'
        """;

        return jdbcTemplate.queryForList(sql, exchangeCode);
    }

    /**
     * Parses ASCII fixed-width CDR format.
     */
    public Map<String, Object> parseAsciiCdr(String line) {
        Map<String, Object> cdr = new HashMap<>();

        if (line.length() < 100) {
            log.warn("Invalid CDR line length: {}", line.length());
            return cdr;
        }

        cdr.put("originatingNumber", line.substring(0, 15).trim());
        cdr.put("terminatingNumber", line.substring(15, 30).trim());
        cdr.put("startTime", line.substring(30, 48).trim());
        cdr.put("durationSeconds", Integer.parseInt(line.substring(48, 54).trim()));
        cdr.put("callType", line.substring(54, 56).trim());
        cdr.put("ratedAmount", Double.parseDouble(line.substring(56, 66).trim()));

        return cdr;
    }
}
