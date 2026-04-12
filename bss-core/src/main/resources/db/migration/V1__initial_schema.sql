-- V1: Initial schema for TMF BSS/OSS platform
-- PostgreSQL 16 with Citus extension for sharding

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
CREATE EXTENSION IF NOT EXISTS "btree_gin";

-- V1.1: Party Management (TMF632)
CREATE TABLE customers (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    external_id VARCHAR(50) UNIQUE,
    customer_type VARCHAR(20) NOT NULL CHECK (customer_type IN ('RESIDENTIAL', 'BUSINESS', 'ENTERPRISE', 'GOVERNMENT')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'TERMINATED')),
    national_id VARCHAR(50),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    primary_phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    street VARCHAR(200),
    city VARCHAR(100) NOT NULL,
    governorate VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    country VARCHAR(50) DEFAULT 'YE',
    kyc_level VARCHAR(20) DEFAULT 'BASIC' CHECK (kyc_level IN ('BASIC', 'FULL', 'PREMIUM')),
    kyc_verified BOOLEAN DEFAULT false,
    churn_risk_score NUMERIC(5,4),
    lifetime_value NUMERIC(15,2),
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_customer_name ON customers USING gin ((first_name || ' ' || last_name) gin_trgm_ops);
CREATE INDEX idx_customer_phone ON customers (primary_phone);
CREATE INDEX idx_customer_national_id ON customers (national_id);
CREATE INDEX idx_customer_characteristics ON customers USING gin (characteristics jsonb_path_ops);
CREATE INDEX idx_customer_status ON customers (status);

-- Privacy consent tracking (GDPR)
CREATE TABLE privacy_consent (
    id UUID PRIMARY KEY,
    party_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    consent_type VARCHAR(50) NOT NULL,
    granted BOOLEAN NOT NULL,
    granted_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ,
    ip_address INET,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_consent_party ON privacy_consent (party_id);

-- V1.2: Billing Account (TMF647)
CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    account_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id UUID NOT NULL REFERENCES customers(id),
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('BILLING', 'SERVICE')),
    service_category VARCHAR(30) CHECK (service_category IN ('FIXED_LINE', 'MOBILE', 'BROADBAND', 'HOSTING', 'ENTERPRISE')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CLOSED')),
    balance NUMERIC(15,2) DEFAULT 0,
    credit_limit NUMERIC(15,2),
    currency VARCHAR(10) DEFAULT 'YER',
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_account_customer ON accounts (customer_id);
CREATE INDEX idx_account_status ON accounts (status);

-- V1.3: Product Catalog (TMF620)
CREATE TABLE product_offerings (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT', 'RETIRED')),
    service_type VARCHAR(30) NOT NULL,
    category_id UUID,
    is_bundle BOOLEAN DEFAULT false,
    bundle_type VARCHAR(30),
    valid_from TIMESTAMPTZ,
    valid_to TIMESTAMPTZ,
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_offering_service_type ON product_offerings (service_type);
CREATE INDEX idx_offering_status ON product_offerings (status);
CREATE INDEX idx_offering_characteristics ON product_offerings USING gin (characteristics jsonb_path_ops);

-- V1.4: Product Orders (TMF622)
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    order_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id UUID NOT NULL REFERENCES customers(id),
    order_type VARCHAR(30) NOT NULL CHECK (order_type IN ('ACQUISITION', 'MODIFICATION', 'TERMINATION', 'SUSPENSION', 'TRANSFER')),
    status VARCHAR(30) NOT NULL DEFAULT 'ACKNOWLEDGED' CHECK (status IN ('ACKNOWLEDGED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'FAILED')),
    priority VARCHAR(20) DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    channel VARCHAR(30),
    notes TEXT,
    completed_at TIMESTAMPTZ,
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_order_customer ON orders (customer_id);
CREATE INDEX idx_order_status ON orders (status);
CREATE INDEX idx_order_type ON orders (order_type);

-- V1.5: Invoices (TMF657)
CREATE TABLE invoices (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    account_id UUID NOT NULL REFERENCES accounts(id),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'FINALIZED', 'PAID', 'OVERDUE', 'CANCELLED')),
    subtotal_amount NUMERIC(15,2) DEFAULT 0,
    discount_amount NUMERIC(15,2) DEFAULT 0,
    tax_amount NUMERIC(15,2) DEFAULT 0,
    total_amount NUMERIC(15,2) DEFAULT 0,
    currency VARCHAR(10) DEFAULT 'YER',
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_invoice_account ON invoices (account_id);
CREATE INDEX idx_invoice_status ON invoices (status);
CREATE INDEX idx_invoice_due_date ON invoices (due_date);

-- V1.6: Payments (TMF671)
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    payment_reference VARCHAR(50) UNIQUE NOT NULL,
    account_id UUID NOT NULL REFERENCES accounts(id),
    invoice_id UUID REFERENCES invoices(id),
    amount NUMERIC(15,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'YER',
    payment_method VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    completed_at TIMESTAMPTZ,
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_payment_account ON payments (account_id);
CREATE INDEX idx_payment_invoice ON payments (invoice_id);
CREATE INDEX idx_payment_status ON payments (status);

-- V1.7: Alarms (TMF642)
CREATE TABLE alarms (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    alarm_id VARCHAR(50) UNIQUE NOT NULL,
    severity VARCHAR(20) NOT NULL CHECK (severity IN ('CRITICAL', 'MAJOR', 'MINOR', 'WARNING', 'INDETERMINATE')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'ACKNOWLEDGED', 'CLEARED', 'CLOSED')),
    source VARCHAR(200),
    resource_id UUID,
    alarm_type VARCHAR(50) NOT NULL,
    description TEXT,
    root_cause TEXT,
    acknowledged_at TIMESTAMPTZ,
    cleared_at TIMESTAMPTZ,
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_alarm_severity ON alarms (severity);
CREATE INDEX idx_alarm_status ON alarms (status);
CREATE INDEX idx_alarm_resource ON alarms (resource_id);

-- V1.8: Trouble Tickets (TMF645)
CREATE TABLE trouble_tickets (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    ticket_number VARCHAR(50) UNIQUE NOT NULL,
    party_id UUID REFERENCES customers(id),
    service_id UUID,
    alarm_id UUID REFERENCES alarms(id),
    severity VARCHAR(20) NOT NULL CHECK (severity IN ('CRITICAL', 'MAJOR', 'MINOR', 'LOW')),
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED' CHECK (status IN ('SUBMITTED', 'IN_PROGRESS', 'WAITING_CUSTOMER', 'RESOLVED', 'CLOSED', 'CANCELLED')),
    ticket_type VARCHAR(50) NOT NULL,
    description TEXT,
    resolution_notes TEXT,
    assigned_to VARCHAR(100),
    sla_deadline TIMESTAMPTZ,
    resolved_at TIMESTAMPTZ,
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_ticket_party ON trouble_tickets (party_id);
CREATE INDEX idx_ticket_status ON trouble_tickets (status);
CREATE INDEX idx_ticket_severity ON trouble_tickets (severity);
