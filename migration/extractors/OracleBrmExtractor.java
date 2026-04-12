package com.yemenptc.bss.migration.extractors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Extracts data from Oracle BRM (4G/LTE WFB convergent charging).
 * Handles object-oriented DB schema (Account/Billinfo/Bal GRP).
 */
@Slf4j
@Component
public class OracleBrmExtractor {

    private final JdbcTemplate jdbcTemplate;

    public OracleBrmExtractor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Extracts account hierarchies from BRM.
     */
    public List<Map<String, Object>> extractAccounts() {
        log.info("Extracting account hierarchies from Oracle BRM");

        String sql = """
            SELECT a.poid_id0 as account_id, 
                   a.account_no as account_number,
                   a.name as account_name,
                   a.status as account_status,
                   b.billinfo_id0 as billinfo_id,
                   b.pay_type as payment_type,
                   bg.obj_id0 as balance_group_id
            FROM account_t a
            LEFT JOIN billinfo_t b ON a.poid_id0 = b.account_obj_id0
            LEFT JOIN bal_grp_t bg ON a.poid_id0 = bg.account_obj_id0
            WHERE a.status = 1
        """;

        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Extracts real-time balances for prepaid.
     */
    public List<Map<String, Object>> extractBalances() {
        log.info("Extracting real-time balances from Oracle BRM");

        String sql = """
            SELECT bg.obj_id0 as balance_group_id,
                   b.rec_id as resource_id,
                   b.current_bal as current_balance,
                   b.reserved_bal as reserved_balance,
                   b.credit_limit as credit_limit
            FROM bal_grp_t bg
            JOIN bal_grp_bals_t b ON bg.obj_id0 = b.obj_id0
            WHERE b.current_bal != 0 OR b.reserved_bal != 0
        """;

        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Extracts active data sessions for migration.
     */
    public List<Map<String, Object>> extractActiveSessions() {
        log.info("Extracting active data sessions from Oracle BRM");

        String sql = """
            SELECT session_id, account_obj_id0 as account_id,
                   msisdn, ip_address, start_t as start_time,
                   usage_bytes_in, usage_bytes_out
            FROM session_t
            WHERE status = 'ACTIVE'
            AND end_t IS NULL
        """;

        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Extracts product/rate plan definitions.
     */
    public List<Map<String, Object>> extractRatePlans() {
        log.info("Extracting rate plans from Oracle BRM");

        String sql = """
            SELECT p.poid_id0 as plan_id,
                   p.name as plan_name,
                   p.descr as plan_description,
                   r.rate_id as rate_id,
                   r.rate_name as rate_name,
                   r.per_unit as per_unit_rate,
                   r.start_minute as start_minute,
                   r.end_minute as end_minute
            FROM product_t p
            LEFT JOIN rate_t r ON p.poid_id0 = r.plan_obj_id0
            WHERE p.status = 1
        """;

        return jdbcTemplate.queryForList(sql);
    }
}
