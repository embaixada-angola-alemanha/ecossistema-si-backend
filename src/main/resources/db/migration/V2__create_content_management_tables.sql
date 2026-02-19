-- Page versions (content versioning)
CREATE TABLE page_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    page_id UUID NOT NULL REFERENCES pages(id) ON DELETE CASCADE,
    idioma VARCHAR(5) NOT NULL,
    version_number INTEGER NOT NULL,
    titulo VARCHAR(300) NOT NULL,
    conteudo TEXT,
    excerto VARCHAR(500),
    meta_titulo VARCHAR(160),
    meta_descricao VARCHAR(320),
    change_summary VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_page_versions_page ON page_versions(page_id);
CREATE INDEX idx_page_versions_page_idioma ON page_versions(page_id, idioma);
CREATE INDEX idx_page_versions_number ON page_versions(page_id, idioma, version_number DESC);

-- Content templates
CREATE TABLE content_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(300),
    tipo_pagina VARCHAR(20) NOT NULL,
    template_html TEXT,
    schema_json TEXT,
    activo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_content_templates_tipo ON content_templates(tipo_pagina);

-- Scheduled publications
CREATE TABLE scheduled_publications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    page_id UUID NOT NULL REFERENCES pages(id) ON DELETE CASCADE,
    scheduled_at TIMESTAMPTZ NOT NULL,
    executed BOOLEAN NOT NULL DEFAULT false,
    executed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_scheduled_publications_pending ON scheduled_publications(scheduled_at) WHERE NOT executed;
CREATE INDEX idx_scheduled_publications_page ON scheduled_publications(page_id);
