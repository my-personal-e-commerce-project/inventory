CREATE TABLE categories (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    slug VARCHAR(255) NOT NULL UNIQUE,
    parent_id VARCHAR(255),
    
    CONSTRAINT fk_category_parent 
        FOREIGN KEY (parent_id) 
        REFERENCES categories (id) 
        ON DELETE SET NULL
);

CREATE TABLE category_attributes (
    id VARCHAR(255) PRIMARY KEY,
    category_id VARCHAR(255) NOT NULL,
    attribute_definition_id VARCHAR(255) NOT NULL,
    is_required BOOLEAN DEFAULT NULL,
    is_filterable BOOLEAN DEFAULT NULL,
    is_sortable BOOLEAN DEFAULT NULL,

    CONSTRAINT fk_cat_attr_category 
        FOREIGN KEY (category_id) 
        REFERENCES categories (id) 
        ON DELETE CASCADE,
        
    CONSTRAINT fk_cat_attr_definition 
        FOREIGN KEY (attribute_definition_id) 
        REFERENCES attribute_definition (id) 
        ON DELETE CASCADE
);

ALTER TABLE categories REPLICA IDENTITY FULL;
