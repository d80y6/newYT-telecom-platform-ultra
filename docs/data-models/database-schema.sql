-- ============================================================================
-- YEMEN PTC BSS/OSS DATABASE SCHEMA
-- PostgreSQL Database Schema for Core Entities
-- ============================================================================

-- ============================================================================
-- EXTENSION SETUP
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- CUSTOMER MANAGEMENT
-- ============================================================================

-- Customers table
CREATE TABLE customers (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    external_id VARCHAR(50) UNIQUE,
    customer_type VARCHAR(20) NOT NULL CHECK (customer_type IN ('RESIDENTIAL', 'BUSINESS', 'ENTERPRISE', 'GOVERNMENT')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'TERMINATED')),
    
    -- Identity information
    national_id VARCHAR(50),
    passport_number VARCHAR(50),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    date_of_birth DATE,
    gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    nationality VARCHAR(50) DEFAULT 'Yemeni',
    
    -- Contact information
    primary_phone VARCHAR(20) NOT NULL,
    secondary_phone VARCHAR(20),
    email VARCHAR(100),
    
    -- Address
    street VARCHAR(200),
    city VARCHAR(100) NOT NULL,
    governorate VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    country VARCHAR(50) DEFAULT 'YE',
    
    -- Demographics
    occupation VARCHAR(100),
    employer VARCHAR(100),
    income_level VARCHAR(20) CHECK (income_level IN ('LOW', 'MEDIUM', 'HIGH')),
    language_preference VARCHAR(10) DEFAULT 'AR' CHECK (language_preference IN ('AR', 'EN')),
    
    -- KYC
    kyc_level VARCHAR(20) DEFAULT 'BASIC' CHECK (kyc_level IN ('BASIC', 'FULL', 'PREMIUM')),
    kyc_verified BOOLEAN DEFAULT FALSE,
    kyc_verified_at TIMESTAMP,
    
    -- Analytics
    segment VARCHAR(50),
    churn_risk_score DECIMAL(5,4),
    lifetime_value DECIMAL(15,2),
    preferred_contact_method VARCHAR(20) CHECK (preferred_contact_method IN ('PHONE', 'EMAIL', 'SMS', 'WHATSAPP')),
    
    -- Metadata
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version INTEGER NOT NULL DEFAULT 1
);

-- Indexes for customers
CREATE INDEX idx_customers_national_id ON customers(national_id);
CREATE INDEX idx_customers_primary_phone ON customers(primary_phone);
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_status ON customers(status);
CREATE INDEX idx_customers_type ON customers(customer_type);
CREATE INDEX idx_customers_governorate ON customers(governorate);
CREATE INDEX idx_customers_created_at ON customers(created_at);

-- Customer interactions
CREATE TABLE customer_interactions (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    customer_id VARCHAR(36) NOT NULL REFERENCES customers(id),
    type VARCHAR(30) NOT NULL CHECK (type IN ('SERVICE_REQUEST', 'COMPLAINT', 'INQUIRY', 'TRANSACTION')),
    subject VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(20) CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    channel VARCHAR(30) CHECK (channel IN ('PHONE', 'EMAIL', 'WEB', 'MOBILE_APP', 'WALK_IN', 'SOCIAL')),
    assigned_to VARCHAR(50),
    resolution TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP
);

CREATE INDEX idx_interactions_customer_id ON customer_interactions(customer_id);
CREATE INDEX idx_interactions_status ON customer_interactions(status);
CREATE INDEX idx_interactions_type ON customer_interactions(type);
CREATE INDEX idx_interactions_created_at ON customer_interactions(created_at);

-- ============================================================================
-- ACCOUNT MANAGEMENT
-- ============================================================================

-- Accounts table
CREATE TABLE accounts (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    account_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id VARCHAR(36) NOT NULL REFERENCES customers(id),
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('BILLING', 'SERVICE')),
    service_category VARCHAR(30) CHECK (service_category IN ('FIXED_LINE', 'MOBILE', 'BROADBAND', 'HOSTING', 'ENTERPRISE')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CLOSED')),
    parent_account_id VARCHAR(36) REFERENCES accounts(id),
    
    -- Billing
    balance DECIMAL(15,2) DEFAULT 0,
    credit_limit DECIMAL(15,2),
    payment_method VARCHAR(30),
    billing_cycle VARCHAR(20),
    currency VARCHAR(10) DEFAULT 'YER',
    
    -- Metadata
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);
CREATE INDEX idx_accounts_status ON accounts(status);
CREATE INDEX idx_accounts_type ON accounts(account_type);
CREATE INDEX idx_accounts_service_category ON accounts(service_category);
CREATE INDEX idx_accounts_billing_cycle ON accounts(billing_cycle);

-- ============================================================================
-- PRODUCT CATALOG
-- ============================================================================

-- Product categories
CREATE TABLE product_categories (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id VARCHAR(36) REFERENCES product_categories(id),
    level INTEGER DEFAULT 0,
    path VARCHAR(500),
    sort_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_categories_parent_id ON product_categories(parent_id);
CREATE INDEX idx_categories_path ON product_categories(path);

-- Product specifications
CREATE TABLE product_specifications (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    version VARCHAR(20),
    brand VARCHAR(100),
    product_number VARCHAR(50),
    lifecycle_status VARCHAR(20) DEFAULT 'ACTIVE',
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Product offerings
CREATE TABLE product_offerings (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT', 'RETIRED')),
    service_type VARCHAR(30) NOT NULL,
    category_id VARCHAR(36) REFERENCES product_categories(id),
    lifecycle_status VARCHAR(20) DEFAULT 'ACTIVE',
    is_bundle BOOLEAN DEFAULT FALSE,
    bundle_type VARCHAR(30),
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version VARCHAR(20)
);

CREATE INDEX idx_offerings_status ON product_offerings(status);
CREATE INDEX idx_offerings_service_type ON product_offerings(service_type);
CREATE INDEX idx_offerings_category_id ON product_offerings(category_id);

-- Product characteristics
CREATE TABLE product_characteristics (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    product_offering_id VARCHAR(36) NOT NULL REFERENCES product_offerings(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    value_type VARCHAR(20) NOT NULL CHECK (value_type IN ('STRING', 'NUMBER', 'BOOLEAN', 'ENUM', 'DATE')),
    value VARCHAR(500),
    unit_of_measure VARCHAR(50),
    min_value DECIMAL(15,2),
    max_value DECIMAL(15,2),
    allowed_values TEXT[],
    sort_order INTEGER DEFAULT 0
);

CREATE INDEX idx_characteristics_offering_id ON product_characteristics(product_offering_id);

-- Product pricing
CREATE TABLE product_prices (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    product_offering_id VARCHAR(36) NOT NULL REFERENCES product_offerings(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price_type VARCHAR(20) NOT NULL CHECK (price_type IN ('ONE_TIME', 'RECURRING', 'USAGE')),
    price DECIMAL(15,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'YER',
    unit_of_measure VARCHAR(50),
    tax_included BOOLEAN DEFAULT FALSE,
    pricing_logic_algorithm VARCHAR(100),
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_prices_offering_id ON product_prices(product_offering_id);
CREATE INDEX idx_prices_type ON product_prices(price_type);

-- Product terms
CREATE TABLE product_terms (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    product_offering_id VARCHAR(36) NOT NULL REFERENCES product_offerings(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    duration_months INTEGER,
    early_termination_fee DECIMAL(15,2),
    auto_renewal BOOLEAN DEFAULT FALSE,
    valid_from TIMESTAMP,
    valid_to TIMESTAMP
);

CREATE INDEX idx_terms_offering_id ON product_terms(product_offering_id);

-- Product bundles
CREATE TABLE product_bundles (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    bundle_offering_id VARCHAR(36) NOT NULL REFERENCES product_offerings(id),
    child_offering_id VARCHAR(36) NOT NULL REFERENCES product_offerings(id),
    quantity INTEGER DEFAULT 1,
    is_mandatory BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0
);

CREATE INDEX idx_bundles_parent ON product_bundles(bundle_offering_id);
CREATE INDEX idx_bundles_child ON product_bundles(child_offering_id);

-- ============================================================================
-- SUBSCRIPTIONS
-- ============================================================================

-- Subscriptions table
CREATE TABLE subscriptions (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    subscription_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id VARCHAR(36) NOT NULL REFERENCES customers(id),
    account_id VARCHAR(36) NOT NULL REFERENCES accounts(id),
    service_type VARCHAR(30) NOT NULL CHECK (service_type IN ('FIXED_LINE', 'MOBILE_CDMA', 'MOBILE_4G', 'ADSL', 'FTTH', 'HOSTING', 'MPLS', 'PRI')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('ACTIVE', 'SUSPENDED', 'TERMINATED', 'PENDING')),
    product_offering_id VARCHAR(36) REFERENCES product_offerings(id),
    
    -- Service identifier
    service_identifier VARCHAR(100), -- MSISDN, DSL username, etc.
    
    -- Plan details
    plan_name VARCHAR(200),
    monthly_fee DECIMAL(15,2),
    included_voice_minutes INTEGER,
    included_data_gb DECIMAL(10,2),
    included_sms INTEGER,
    speed_mbps DECIMAL(10,2),
    
    -- Commitment
    commitment_duration_months INTEGER,
    early_termination_fee DECIMAL(15,2),
    
    -- Dates
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    commitment_end_date TIMESTAMP,
    
    -- Metadata
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_subscriptions_customer_id ON subscriptions(customer_id);
CREATE INDEX idx_subscriptions_account_id ON subscriptions(account_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);
CREATE INDEX idx_subscriptions_service_type ON subscriptions(service_type);
CREATE INDEX idx_subscriptions_service_identifier ON subscriptions(service_identifier);
CREATE INDEX idx_subscriptions_product_offering_id ON subscriptions(product_offering_id);

-- Subscription add-ons
CREATE TABLE subscription_addons (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    subscription_id VARCHAR(36) NOT NULL REFERENCES subscriptions(id),
    product_offering_id VARCHAR(36) NOT NULL REFERENCES product_offerings(id),
    name VARCHAR(200) NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_addons_subscription_id ON subscription_addons(subscription_id);

-- ============================================================================
-- BILLING & RATING
-- ============================================================================

-- Billing cycles
CREATE TABLE billing_cycles (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    cycle_name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200),
    cycle_day INTEGER NOT NULL CHECK (cycle_day BETWEEN 1 AND 28),
    due_days INTEGER NOT NULL DEFAULT 15,
    grace_days INTEGER NOT NULL DEFAULT 5,
    is_active BOOLEAN DEFAULT TRUE
);

-- Invoices
CREATE TABLE invoices (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    account_id VARCHAR(36) NOT NULL REFERENCES accounts(id),
    billing_cycle VARCHAR(50) REFERENCES billing_cycles(cycle_name),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'FINALIZED', 'PAID', 'OVERDUE', 'CANCELLED')),
    
    -- Amounts
    subtotal_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(15,2) DEFAULT 0,
    tax_amount DECIMAL(15,2) DEFAULT 0,
    total_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    paid_amount DECIMAL(15,2) DEFAULT 0,
    currency VARCHAR(10) DEFAULT 'YER',
    
    -- Dates
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    paid_date DATE,
    
    -- PDF
    pdf_url VARCHAR(500),
    pdf_generated_at TIMESTAMP,
    
    -- Metadata
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_invoices_account_id ON invoices(account_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_billing_cycle ON invoices(billing_cycle);
CREATE INDEX idx_invoices_due_date ON invoices(due_date);
CREATE INDEX idx_invoices_issue_date ON invoices(issue_date);

-- Invoice items
CREATE TABLE invoice_items (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    invoice_id VARCHAR(36) NOT NULL REFERENCES invoices(id),
    item_type VARCHAR(20) NOT NULL CHECK (item_type IN ('RECURRING', 'USAGE', 'ONE_TIME', 'ADJUSTMENT', 'DISCOUNT')),
    description VARCHAR(500) NOT NULL,
    subscription_id VARCHAR(36) REFERENCES subscriptions(id),
    quantity DECIMAL(15,4) DEFAULT 1,
    unit_price DECIMAL(15,2) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    tax_amount DECIMAL(15,2) DEFAULT 0,
    discount_amount DECIMAL(15,2) DEFAULT 0,
    period_start DATE,
    period_end DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_invoice_items_invoice_id ON invoice_items(invoice_id);
CREATE INDEX idx_invoice_items_subscription_id ON invoice_items(subscription_id);

-- Payments
CREATE TABLE payments (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    payment_reference VARCHAR(50) UNIQUE NOT NULL,
    account_id VARCHAR(36) NOT NULL REFERENCES accounts(id),
    invoice_id VARCHAR(36) REFERENCES invoices(id),
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'YER',
    payment_method VARCHAR(30) NOT NULL CHECK (payment_method IN ('CASH', 'CARD', 'BANK_TRANSFER', 'CHEQUE', 'MOBILE_MONEY', 'VOUCHER')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    external_reference VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX idx_payments_account_id ON payments(account_id);
CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_created_at ON payments(created_at);

-- Usage events (CDR/EDR)
CREATE TABLE usage_events (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    event_id VARCHAR(100) UNIQUE NOT NULL,
    subscription_id VARCHAR(36) NOT NULL REFERENCES subscriptions(id),
    service_type VARCHAR(30) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    calling_number VARCHAR(20),
    called_number VARCHAR(20),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    duration_seconds INTEGER,
    data_volume_bytes BIGINT,
    unit_of_measure VARCHAR(20),
    location VARCHAR(100),
    rating_group INTEGER,
    rated_amount DECIMAL(15,2),
    rated_units DECIMAL(15,4),
    rating_result VARCHAR(30),
    source_system VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) PARTITION BY RANGE (start_time);

-- Create partitions for usage events (monthly)
CREATE TABLE usage_events_2024_01 PARTITION OF usage_events
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');
CREATE TABLE usage_events_2024_02 PARTITION OF usage_events
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');
-- Continue for all months...

CREATE INDEX idx_usage_events_subscription_id ON usage_events(subscription_id);
CREATE INDEX idx_usage_events_start_time ON usage_events(start_time);
CREATE INDEX idx_usage_events_event_type ON usage_events(event_type);
CREATE INDEX idx_usage_events_service_type ON usage_events(service_type);

-- Recharges
CREATE TABLE recharges (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    transaction_id VARCHAR(50) UNIQUE NOT NULL,
    account_id VARCHAR(36) NOT NULL REFERENCES accounts(id),
    msisdn VARCHAR(20),
    amount DECIMAL(15,2) NOT NULL,
    bonus_amount DECIMAL(15,2) DEFAULT 0,
    previous_balance DECIMAL(15,2),
    new_balance DECIMAL(15,2),
    payment_method VARCHAR(30) NOT NULL,
    voucher_code VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_recharges_account_id ON recharges(account_id);
CREATE INDEX idx_recharges_msisdn ON recharges(msisdn);
CREATE INDEX idx_recharges_status ON recharges(status);
CREATE INDEX idx_recharges_created_at ON recharges(created_at);

-- Balance adjustments
CREATE TABLE balance_adjustments (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    transaction_id VARCHAR(50) UNIQUE NOT NULL,
    account_id VARCHAR(36) NOT NULL REFERENCES accounts(id),
    adjustment_type VARCHAR(10) NOT NULL CHECK (adjustment_type IN ('CREDIT', 'DEBIT')),
    amount DECIMAL(15,2) NOT NULL,
    reason VARCHAR(200) NOT NULL,
    reference_id VARCHAR(100),
    previous_balance DECIMAL(15,2),
    new_balance DECIMAL(15,2),
    created_by VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_adjustments_account_id ON balance_adjustments(account_id);
CREATE INDEX idx_adjustments_created_at ON balance_adjustments(created_at);

-- ============================================================================
-- ORDER MANAGEMENT
-- ============================================================================

-- Orders
CREATE TABLE orders (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id VARCHAR(36) NOT NULL REFERENCES customers(id),
    order_type VARCHAR(30) NOT NULL CHECK (order_type IN ('ACQUISITION', 'MODIFICATION', 'TERMINATION', 'SUSPENSION', 'TRANSFER')),
    status VARCHAR(30) NOT NULL DEFAULT 'ACKNOWLEDGED' CHECK (status IN ('ACKNOWLEDGED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'FAILED', 'REJECTED', 'HELD', 'PENDING')),
    priority VARCHAR(20) DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    channel VARCHAR(30),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    created_by VARCHAR(50),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_type ON orders(order_type);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- Order items
CREATE TABLE order_items (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    order_id VARCHAR(36) NOT NULL REFERENCES orders(id),
    parent_item_id VARCHAR(36) REFERENCES order_items(id),
    item_type VARCHAR(30) NOT NULL CHECK (item_type IN ('COMMERCIAL', 'SERVICE', 'RESOURCE')),
    product_offering_id VARCHAR(36) REFERENCES product_offerings(id),
    subscription_id VARCHAR(36) REFERENCES subscriptions(id),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    action VARCHAR(30) CHECK (action IN ('ADD', 'MODIFY', 'DELETE', 'SUSPEND', 'RESUME')),
    parameters JSONB,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_parent_id ON order_items(parent_item_id);
CREATE INDEX idx_order_items_status ON order_items(status);

-- Order state history
CREATE TABLE order_state_history (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    order_id VARCHAR(36) NOT NULL REFERENCES orders(id),
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    reason VARCHAR(500),
    changed_by VARCHAR(50),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_history_order_id ON order_state_history(order_id);

-- ============================================================================
-- COLLECTIONS & DUNNING
-- ============================================================================

-- Dunning stages
CREATE TABLE dunning_stages (
    id SERIAL PRIMARY KEY,
    stage_number INTEGER UNIQUE NOT NULL,
    stage_name VARCHAR(100) NOT NULL,
    days_past_due_start INTEGER NOT NULL,
    days_past_due_end INTEGER NOT NULL,
    actions TEXT[],
    service_impact VARCHAR(50),
    is_automated BOOLEAN DEFAULT TRUE,
    description TEXT
);

-- Insert default dunning stages
INSERT INTO dunning_stages (stage_number, stage_name, days_past_due_start, days_past_due_end, actions, service_impact, is_automated, description) VALUES
(1, 'Friendly Reminder', 1, 3, ARRAY['SEND_SMS', 'SEND_EMAIL', 'PUSH_NOTIFICATION'], 'NONE', TRUE, 'Automated friendly reminder'),
(2, 'Payment Warning', 4, 7, ARRAY['AUTOMATED_CALL', 'SMS_WITH_LINK', 'FLAG_ACCOUNT'], 'WARNING_BANNER', TRUE, 'Payment warning with automated call'),
(3, 'Service Restriction', 8, 14, ARRAY['BLOCK_OUTGOING', 'THROTTLE_DATA', 'FINAL_DEMAND'], 'PARTIAL', TRUE, 'Partial service restriction'),
(4, 'Full Suspension', 15, 30, ARRAY['SUSPEND_ALL', 'SEND_TO_COLLECTIONS'], 'FULL', FALSE, 'Full service suspension'),
(5, 'Write-Off', 60, 999, ARRAY['TERMINATE_ACCOUNT', 'LEGAL_ACTION', 'BLACKLIST'], 'TERMINATED', FALSE, 'Account termination and write-off');

-- Dunning cases
CREATE TABLE dunning_cases (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    account_id VARCHAR(36) NOT NULL REFERENCES accounts(id),
    invoice_id VARCHAR(36) REFERENCES invoices(id),
    current_stage INTEGER NOT NULL DEFAULT 1 REFERENCES dunning_stages(stage_number),
    overdue_amount DECIMAL(15,2) NOT NULL,
    days_past_due INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'RESOLVED', 'WRITTEN_OFF')),
    last_action_date TIMESTAMP,
    next_action_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_dunning_account_id ON dunning_cases(account_id);
CREATE INDEX idx_dunning_status ON dunning_cases(status);
CREATE INDEX idx_dunning_stage ON dunning_cases(current_stage);

-- Dunning actions log
CREATE TABLE dunning_actions (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    dunning_case_id VARCHAR(36) NOT NULL REFERENCES dunning_cases(id),
    action_type VARCHAR(50) NOT NULL,
    stage INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    result VARCHAR(100),
    executed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_dunning_actions_case_id ON dunning_actions(dunning_case_id);

-- ============================================================================
-- AUDIT & COMPLIANCE
-- ============================================================================

-- Audit trail
CREATE TABLE audit_trail (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(36) NOT NULL,
    action VARCHAR(20) NOT NULL CHECK (action IN ('CREATE', 'READ', 'UPDATE', 'DELETE')),
    old_value JSONB,
    new_value JSONB,
    user_id VARCHAR(50),
    ip_address INET,
    user_agent VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) PARTITION BY RANGE (created_at);

-- Create partitions for audit trail (monthly)
CREATE TABLE audit_trail_2024_01 PARTITION OF audit_trail
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');
-- Continue for all months...

CREATE INDEX idx_audit_entity ON audit_trail(entity_type, entity_id);
CREATE INDEX idx_audit_user_id ON audit_trail(user_id);
CREATE INDEX idx_audit_created_at ON audit_trail(created_at);

-- ============================================================================
-- NETWORK INVENTORY (OSS)
-- ============================================================================

-- Network elements
CREATE TABLE network_elements (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    element_name VARCHAR(100) NOT NULL,
    element_type VARCHAR(50) NOT NULL,
    vendor VARCHAR(50),
    model VARCHAR(100),
    ip_address INET,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    location VARCHAR(200),
    site_id VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ne_type ON network_elements(element_type);
CREATE INDEX idx_ne_status ON network_elements(status);
CREATE INDEX idx_ne_site_id ON network_elements(site_id);

-- Number pool
CREATE TABLE number_pool (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::varchar,
    number VARCHAR(20) NOT NULL,
    number_type VARCHAR(30) NOT NULL CHECK (number_type IN ('MSISDN', 'FIXED_LINE', 'SHORT_CODE', 'TOLL_FREE')),
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'RESERVED', 'ASSIGNED', 'BLOCKED')),
    exchange VARCHAR(50),
    reservation_id VARCHAR(36),
    reservation_expiry TIMESTAMP,
    assigned_to VARCHAR(36),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_number_pool_number ON number_pool(number);
CREATE INDEX idx_number_pool_status ON number_pool(status);
CREATE INDEX idx_number_pool_type ON number_pool(number_type);
CREATE INDEX idx_number_pool_exchange ON number_pool(exchange);

-- ============================================================================
-- FUNCTIONS & TRIGGERS
-- ============================================================================

-- Update timestamp trigger
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply trigger to tables
CREATE TRIGGER update_customers_timestamp BEFORE UPDATE ON customers FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER update_accounts_timestamp BEFORE UPDATE ON accounts FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER update_subscriptions_timestamp BEFORE UPDATE ON subscriptions FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER update_orders_timestamp BEFORE UPDATE ON orders FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER update_invoices_timestamp BEFORE UPDATE ON invoices FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER update_product_offerings_timestamp BEFORE UPDATE ON product_offerings FOR EACH ROW EXECUTE FUNCTION update_timestamp();

-- Account balance update function
CREATE OR REPLACE FUNCTION update_account_balance(p_account_id VARCHAR, p_amount DECIMAL, p_type VARCHAR)
RETURNS VOID AS $$
BEGIN
    IF p_type = 'CREDIT' THEN
        UPDATE accounts SET balance = balance + p_amount WHERE id = p_account_id;
    ELSIF p_type = 'DEBIT' THEN
        UPDATE accounts SET balance = balance - p_amount WHERE id = p_account_id;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Generate invoice number function
CREATE OR REPLACE FUNCTION generate_invoice_number()
RETURNS VARCHAR AS $$
DECLARE
    v_year VARCHAR(4);
    v_sequence INTEGER;
    v_invoice_number VARCHAR(50);
BEGIN
    v_year := EXTRACT(YEAR FROM CURRENT_DATE)::VARCHAR;
    
    SELECT COALESCE(MAX(CAST(SUBSTRING(invoice_number FROM 8) AS INTEGER)), 0) + 1
    INTO v_sequence
    FROM invoices
    WHERE invoice_number LIKE 'INV-' || v_year || '-%';
    
    v_invoice_number := 'INV-' || v_year || '-' || LPAD(v_sequence::VARCHAR, 6, '0');
    
    RETURN v_invoice_number;
END;
$$ LANGUAGE plpgsql;
