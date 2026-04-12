-- V2: Add subscription and service inventory tables (TMF639)

-- V2.1: Subscriptions (active services)
CREATE TABLE subscriptions (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    subscription_number VARCHAR(50) UNIQUE NOT NULL,
    party_id UUID NOT NULL REFERENCES customers(id),
    account_id UUID NOT NULL REFERENCES accounts(id),
    offering_id UUID REFERENCES product_offerings(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'SUSPENDED', 'TERMINATED', 'PENDING')),
    start_date DATE NOT NULL,
    end_date DATE,
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_subscription_party ON subscriptions (party_id);
CREATE INDEX idx_subscription_account ON subscriptions (account_id);
CREATE INDEX idx_subscription_status ON subscriptions (status);

-- V2.2: Resource inventory (network resources)
CREATE TABLE resources (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    resource_identifier VARCHAR(100) UNIQUE NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' CHECK (resource_status IN ('AVAILABLE', 'RESERVED', 'ALLOCATED', 'FAULTY', 'DECOMMISSIONED')),
    category VARCHAR(30) CHECK (category IN ('PHYSICAL', 'LOGICAL', 'VIRTUAL')),
    location VARCHAR(200),
    parent_resource_id UUID REFERENCES resources(id),
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_resource_type ON resources (resource_type);
CREATE INDEX idx_resource_status ON resources (resource_status);
CREATE INDEX idx_resource_parent ON resources (parent_resource_id);

-- V2.3: Service-Resource mapping
CREATE TABLE service_resources (
    id UUID PRIMARY KEY,
    subscription_id UUID NOT NULL REFERENCES subscriptions(id),
    resource_id UUID NOT NULL REFERENCES resources(id),
    relationship_type VARCHAR(30) NOT NULL CHECK (relationship_type IN ('SUPPORTS', 'REQUIRES', 'USES')),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_sr_subscription ON service_resources (subscription_id);
CREATE INDEX idx_sr_resource ON service_resources (resource_id);

-- V2.4: Billing line items
CREATE TABLE billing_line_items (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL REFERENCES invoices(id),
    subscription_id UUID REFERENCES subscriptions(id),
    description VARCHAR(200) NOT NULL,
    quantity INTEGER DEFAULT 1,
    unit_price NUMERIC(15,2) NOT NULL,
    discount_amount NUMERIC(15,2) DEFAULT 0,
    tax_amount NUMERIC(15,2) DEFAULT 0,
    total_amount NUMERIC(15,2) NOT NULL,
    domain VARCHAR(30) CHECK (domain IN ('PSTN', 'FTTH', '4G', 'HOSTING', 'ENTERPRISE', 'MPLS')),
    period_start DATE,
    period_end DATE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_line_item_invoice ON billing_line_items (invoice_id);
CREATE INDEX idx_line_item_domain ON billing_line_items (domain);
