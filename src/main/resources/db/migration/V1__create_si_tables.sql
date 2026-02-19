-- Pages (CMS core)
CREATE TABLE pages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug VARCHAR(200) NOT NULL UNIQUE,
    tipo VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    featured_image_id UUID,
    template VARCHAR(100),
    sort_order INTEGER DEFAULT 0,
    published_at TIMESTAMPTZ,
    parent_id UUID REFERENCES pages(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_pages_slug ON pages(slug);
CREATE INDEX idx_pages_tipo ON pages(tipo);
CREATE INDEX idx_pages_estado ON pages(estado);
CREATE INDEX idx_pages_parent ON pages(parent_id);

-- Page translations (multilingual content)
CREATE TABLE page_translations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    page_id UUID NOT NULL REFERENCES pages(id) ON DELETE CASCADE,
    idioma VARCHAR(5) NOT NULL,
    titulo VARCHAR(300) NOT NULL,
    conteudo TEXT,
    excerto VARCHAR(500),
    meta_titulo VARCHAR(160),
    meta_descricao VARCHAR(320),
    meta_keywords VARCHAR(500),
    og_image_url VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0,
    UNIQUE(page_id, idioma)
);

CREATE INDEX idx_page_translations_page ON page_translations(page_id);
CREATE INDEX idx_page_translations_idioma ON page_translations(idioma);

-- Menus
CREATE TABLE menus (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL UNIQUE,
    localizacao VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

-- Menu items
CREATE TABLE menu_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    menu_id UUID NOT NULL REFERENCES menus(id) ON DELETE CASCADE,
    parent_id UUID REFERENCES menu_items(id),
    label_pt VARCHAR(200) NOT NULL,
    label_en VARCHAR(200),
    label_de VARCHAR(200),
    label_cs VARCHAR(200),
    url VARCHAR(500),
    page_id UUID REFERENCES pages(id),
    sort_order INTEGER DEFAULT 0,
    open_new_tab BOOLEAN DEFAULT false,
    icon VARCHAR(50),
    activo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_menu_items_menu ON menu_items(menu_id);
CREATE INDEX idx_menu_items_parent ON menu_items(parent_id);
CREATE INDEX idx_menu_items_sort ON menu_items(menu_id, sort_order);

-- Media files
CREATE TABLE media_files (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    file_name VARCHAR(300) NOT NULL,
    original_name VARCHAR(300) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    size BIGINT NOT NULL,
    bucket VARCHAR(100) NOT NULL,
    object_key VARCHAR(500) NOT NULL,
    alt_pt VARCHAR(300),
    alt_en VARCHAR(300),
    alt_de VARCHAR(300),
    alt_cs VARCHAR(300),
    width INTEGER,
    height INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_media_files_tipo ON media_files(tipo);
CREATE INDEX idx_media_files_mime ON media_files(mime_type);
