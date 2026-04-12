-- ============================================================================
-- YEMEN PTC BSS/OSS DATA MIGRATION SCRIPTS
-- Migrate data from existing systems to new platform
-- ============================================================================

-- ============================================================================
-- MIGRATION FROM TITAN (Landline Voice)
-- ============================================================================

-- Step 1: Create staging table for Titan data
CREATE TABLE IF NOT EXISTS staging_titan_customers (
    titan_id VARCHAR(50),
    msisdn VARCHAR(20),
    name VARCHAR(200),
    address TEXT,
    plan_code VARCHAR(50),
    status VARCHAR(20),
    created_date TIMESTAMP,
    migrated BOOLEAN DEFAULT FALSE,
    migration_date TIMESTAMP,
    error_message TEXT
);

-- Step 2: Import Titan customer data
-- This would typically be done via ETL tool or COPY command
-- COPY staging_titan_customers FROM '/data/titan_customers.csv' WITH CSV HEADER;

-- Step 3: Migrate customers to new platform
INSERT INTO customers (
    id,
    external_id,
    customer_type,
    status,
    first_name,
    last_name,
    primary_phone,
    street,
    city,
    governorate,
    created_at,
    updated_at,
    version
)
SELECT 
    uuid_generate_v4()::varchar,
    titan_id,
    'RESIDENTIAL',
    CASE 
        WHEN status = 'ACTIVE' THEN 'ACTIVE'
        WHEN status = 'SUSPENDED' THEN 'SUSPENDED'
        ELSE 'INACTIVE'
    END,
    SPLIT_PART(name, ' ', 1),
    SPLIT_PART(name, ' ', 2),
    msisdn,
    address,
    'Sanaa',
    'Sanaa',
    COALESCE(created_date, CURRENT_TIMESTAMP),
    CURRENT_TIMESTAMP,
    1
FROM staging_titan_customers
WHERE migrated = FALSE;

-- Step 4: Create subscriptions for migrated customers
INSERT INTO subscriptions (
    id,
    subscription_number,
    customer_id,
    account_id,
    service_type,
    status,
    service_identifier,
    plan_name,
    start_date,
    created_at,
    updated_at,
    version
)
SELECT 
    uuid_generate_v4()::varchar,
    'SUB-' || st.msisdn,
    c.id,
    a.id,
    'FIXED_LINE',
    c.status,
    st.msisdn,
    st.plan_code,
    st.created_date,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1
FROM staging_titan_customers st
JOIN customers c ON c.external_id = st.titan_id
JOIN accounts a ON a.customer_id = c.id
WHERE st.migrated = FALSE;

-- Step 5: Mark as migrated
UPDATE staging_titan_customers SET migrated = TRUE, migration_date = CURRENT_TIMESTAMP WHERE migrated = FALSE;

-- ============================================================================
-- MIGRATION FROM ORACLE BRM (4G/LTE)
-- ============================================================================

CREATE TABLE IF NOT EXISTS staging_oracle_customers (
    oracle_account_no VARCHAR(50),
    msisdn VARCHAR(20),
    imsi VARCHAR(50),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(500),
    service_type VARCHAR(50),
    plan_code VARCHAR(50),
    balance DECIMAL(15,2),
    status VARCHAR(20),
    created_date TIMESTAMP,
    migrated BOOLEAN DEFAULT FALSE
);

-- Migrate Oracle customers
INSERT INTO customers (
    id, external_id, customer_type, status, first_name, last_name,
    primary_phone, email, created_at, updated_at, version
)
SELECT 
    uuid_generate_v4()::varchar, oracle_account_no, 'RESIDENTIAL',
    CASE WHEN status = 'A' THEN 'ACTIVE' ELSE 'INACTIVE' END,
    first_name, last_name, phone, email,
    COALESCE(created_date, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, 1
FROM staging_oracle_customers WHERE migrated = FALSE;

-- ============================================================================
-- MIGRATION FROM IN-HOUSE ADSL/FTTH SYSTEM
-- ============================================================================

CREATE TABLE IF NOT EXISTS staging_adsl_customers (
    adsl_username VARCHAR(50),
    customer_name VARCHAR(200),
    phone VARCHAR(20),
    email VARCHAR(100),
    service_type VARCHAR(20), -- ADSL or FTTH
    speed_profile VARCHAR(50),
    ip_address VARCHAR(50),
    status VARCHAR(20),
    created_date TIMESTAMP,
    migrated BOOLEAN DEFAULT FALSE
);

-- Migrate ADSL/FTTH customers
INSERT INTO customers (
    id, external_id, customer_type, status, first_name, last_name,
    primary_phone, email, created_at, updated_at, version
)
SELECT 
    uuid_generate_v4()::varchar, adsl_username, 'RESIDENTIAL',
    CASE WHEN status = 'ACTIVE' THEN 'ACTIVE' ELSE 'INACTIVE' END,
    SPLIT_PART(customer_name, ' ', 1), SPLIT_PART(customer_name, ' ', 2),
    phone, email, COALESCE(created_date, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, 1
FROM staging_adsl_customers WHERE migrated = FALSE;

-- ============================================================================
-- MIGRATION FROM WHM (Hosting)
-- ============================================================================

CREATE TABLE IF NOT EXISTS staging_whm_accounts (
    cpanel_username VARCHAR(50),
    domain VARCHAR(200),
    customer_email VARCHAR(100),
    package_name VARCHAR(100),
    disk_usage_mb INTEGER,
    bandwidth_mb INTEGER,
    status VARCHAR(20),
    created_date TIMESTAMP,
    migrated BOOLEAN DEFAULT FALSE
);

-- Migrate hosting customers
INSERT INTO customers (
    id, external_id, customer_type, status, first_name, email,
    created_at, updated_at, version
)
SELECT 
    uuid_generate_v4()::varchar, cpanel_username, 'BUSINESS',
    CASE WHEN status = 'active' THEN 'ACTIVE' ELSE 'INACTIVE' END,
    domain, customer_email,
    COALESCE(created_date, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, 1
FROM staging_whm_accounts WHERE migrated = FALSE;

-- ============================================================================
-- MIGRATION FROM MPLS/PRI (Enterprise)
-- ============================================================================

CREATE TABLE IF NOT EXISTS staging_enterprise_customers (
    enterprise_id VARCHAR(50),
    company_name VARCHAR(200),
    contact_name VARCHAR(200),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    service_type VARCHAR(50), -- MPLS, PRI
    circuit_id VARCHAR(50),
    bandwidth_mbps INTEGER,
    status VARCHAR(20),
    created_date TIMESTAMP,
    migrated BOOLEAN DEFAULT FALSE
);

-- Migrate enterprise customers
INSERT INTO customers (
    id, external_id, customer_type, status, first_name, last_name,
    primary_phone, email, created_at, updated_at, version
)
SELECT 
    uuid_generate_v4()::varchar, enterprise_id, 'ENTERPRISE',
    CASE WHEN status = 'ACTIVE' THEN 'ACTIVE' ELSE 'INACTIVE' END,
    SPLIT_PART(contact_name, ' ', 1), SPLIT_PART(contact_name, ' ', 2),
    contact_phone, contact_email,
    COALESCE(created_date, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, 1
FROM staging_enterprise_customers WHERE migrated = FALSE;

-- ============================================================================
-- CREATE DEFAULT ACCOUNTS FOR MIGRATED CUSTOMERS
-- ============================================================================

INSERT INTO accounts (
    id, account_number, customer_id, account_type, service_category,
    status, balance, currency, created_at, updated_at, version
)
SELECT 
    uuid_generate_v4()::varchar,
    'ACC-' || SUBSTRING(c.id FROM 1 FOR 10),
    c.id,
    'BILLING',
    CASE 
        WHEN c.customer_type = 'ENTERPRISE' THEN 'ENTERPRISE'
        WHEN c.primary_phone LIKE '77%' OR c.primary_phone LIKE '78%' THEN 'MOBILE'
        WHEN c.primary_phone LIKE '01%' THEN 'FIXED_LINE'
        ELSE 'BROADBAND'
    END,
    c.status,
    0,
    'YER',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1
FROM customers c
WHERE NOT EXISTS (
    SELECT 1 FROM accounts a WHERE a.customer_id = c.id
);

-- ============================================================================
-- MIGRATION VALIDATION
-- ============================================================================

-- Count migrated customers by source
SELECT 'TITAN' as source, COUNT(*) as count FROM staging_titan_customers WHERE migrated = TRUE
UNION ALL
SELECT 'ORACLE', COUNT(*) FROM staging_oracle_customers WHERE migrated = TRUE
UNION ALL
SELECT 'ADSL/FTTH', COUNT(*) FROM staging_adsl_customers WHERE migrated = TRUE
UNION ALL
SELECT 'WHM', COUNT(*) FROM staging_whm_accounts WHERE migrated = TRUE
UNION ALL
SELECT 'ENTERPRISE', COUNT(*) FROM staging_enterprise_customers WHERE migrated = TRUE;

-- Validate customer-account relationship
SELECT 
    c.customer_type,
    COUNT(DISTINCT c.id) as customers,
    COUNT(DISTINCT a.id) as accounts,
    COUNT(DISTINCT s.id) as subscriptions
FROM customers c
LEFT JOIN accounts a ON a.customer_id = c.id
LEFT JOIN subscriptions s ON s.customer_id = c.id
GROUP BY c.customer_type;
