CREATE TABLE attribute_definition (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    slug VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    is_global BOOLEAN DEFAULT FALSE,
    is_required BOOLEAN DEFAULT NULL,
    is_filterable BOOLEAN DEFAULT NULL,
    is_sortable BOOLEAN DEFAULT NULL
);

CREATE INDEX idx_attr_def_slug ON attribute_definition(slug);

ALTER TABLE attribute_definition REPLICA IDENTITY FULL;
