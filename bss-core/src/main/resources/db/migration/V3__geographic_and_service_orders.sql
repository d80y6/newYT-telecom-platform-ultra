-- V3: Geographic addresses, sites, and additional tables

-- V3.1: Geographic Addresses (TMF653)
CREATE TABLE geographic_addresses (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    street_number VARCHAR(20),
    street VARCHAR(200),
    city VARCHAR(100),
    locality VARCHAR(100),
    post_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'YE',
    governorate VARCHAR(100),
    latitude NUMERIC(10,7),
    longitude NUMERIC(10,7),
    ftth_coverage BOOLEAN DEFAULT false,
    adsl_coverage BOOLEAN DEFAULT false,
    coverage_4g BOOLEAN DEFAULT false,
    nearest_olt VARCHAR(100),
    copper_distance_m INTEGER,
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_address_city ON geographic_addresses (city);
CREATE INDEX idx_address_governorate ON geographic_addresses (governorate);
CREATE INDEX idx_address_post_code ON geographic_addresses (post_code);

-- V3.2: Geographic Sites (TMF656)
CREATE TABLE geographic_sites (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    site_name VARCHAR(200) NOT NULL,
    site_type VARCHAR(30) NOT NULL CHECK (site_type IN ('EXCHANGE', 'DATA_CENTER', 'TOWER', 'BUILDING', 'CUSTOMER_PREMISE', 'COLOCATION')),
    address_id UUID REFERENCES geographic_addresses(id),
    parent_site_id UUID REFERENCES geographic_sites(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE', 'DECOMMISSIONED')),
    region VARCHAR(100),
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_site_type ON geographic_sites (site_type);
CREATE INDEX idx_site_status ON geographic_sites (status);
CREATE INDEX idx_site_region ON geographic_sites (region);

-- V3.3: Service Orders (TMF641)
CREATE TABLE service_orders (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    order_number VARCHAR(50) UNIQUE NOT NULL,
    party_id UUID REFERENCES customers(id),
    product_order_id UUID REFERENCES orders(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACKNOWLEDGED' CHECK (status IN ('ACKNOWLEDGED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'FAILED')),
    order_type VARCHAR(30) NOT NULL CHECK (order_type IN ('ACTIVATION', 'DEACTIVATION', 'MODIFICATION', 'SUSPENSION')),
    cfs_type VARCHAR(50),
    rfs_type VARCHAR(50),
    completed_at TIMESTAMPTZ,
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_service_order_party ON service_orders (party_id);
CREATE INDEX idx_service_order_status ON service_orders (status);

-- V3.4: Agreements (TMF684)
CREATE TABLE agreements (
    id UUID PRIMARY KEY,
    href VARCHAR(512),
    name VARCHAR(200) NOT NULL,
    agreement_type VARCHAR(30) NOT NULL CHECK (agreement_type IN ('SLA', 'MSA', 'NDA', 'SERVICE_AGREEMENT')),
    party_id UUID REFERENCES customers(id),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'ACTIVE', 'SUSPENDED', 'TERMINATED')),
    start_date DATE NOT NULL,
    end_date DATE,
    sla_terms JSONB DEFAULT '{}',
    penalty_clauses JSONB DEFAULT '{}',
    characteristics JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_agreement_party ON agreements (party_id);
CREATE INDEX idx_agreement_type ON agreements (agreement_type);
CREATE INDEX idx_agreement_status ON agreements (status);
