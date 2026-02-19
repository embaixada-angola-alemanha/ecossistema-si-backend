-- Events / Protocol
CREATE TABLE events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo_pt VARCHAR(300) NOT NULL,
    titulo_en VARCHAR(300),
    titulo_de VARCHAR(300),
    titulo_cs VARCHAR(300),
    descricao_pt TEXT,
    descricao_en TEXT,
    descricao_de TEXT,
    descricao_cs TEXT,
    local_pt VARCHAR(300),
    local_en VARCHAR(300),
    data_inicio TIMESTAMPTZ NOT NULL,
    data_fim TIMESTAMPTZ,
    image_id UUID REFERENCES media_files(id),
    estado VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    tipo_evento VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_events_data ON events(data_inicio DESC);
CREATE INDEX idx_events_estado ON events(estado);
CREATE INDEX idx_events_tipo ON events(tipo_evento);

-- Contact information
CREATE TABLE contact_info (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    departamento VARCHAR(100) NOT NULL,
    endereco VARCHAR(300),
    cidade VARCHAR(100),
    codigo_postal VARCHAR(20),
    pais VARCHAR(100),
    telefone VARCHAR(50),
    fax VARCHAR(50),
    email VARCHAR(200),
    horario_pt VARCHAR(500),
    horario_en VARCHAR(500),
    horario_de VARCHAR(500),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    sort_order INTEGER DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_contact_info_activo ON contact_info(activo);
