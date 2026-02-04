CREATE TABLE products (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    price DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL
);

ALTER TABLE products REPLICA IDENTITY FULL;

CREATE TABLE product_categories (
    product_id VARCHAR(255) NOT NULL,
    category_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);

CREATE TABLE images (
    id SERIAL PRIMARY KEY,
    url TEXT NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_product_images FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

CREATE TABLE tags (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_product_tags FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

CREATE TABLE product_attribute_values (
    id VARCHAR(255) PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    attribute_definition_id VARCHAR(255) NOT NULL,
    string_value TEXT,
    integer_value INTEGER,
    double_value DOUBLE PRECISION,
    boolean_value BOOLEAN,
    
    CONSTRAINT fk_pav_product 
        FOREIGN KEY (product_id) 
        REFERENCES products (id) 
        ON DELETE CASCADE,
        
    CONSTRAINT fk_pav_definition 
        FOREIGN KEY (attribute_definition_id) 
        REFERENCES attribute_definition (id) 
        ON DELETE CASCADE
);

CREATE INDEX idx_pav_product ON product_attribute_values(product_id);
CREATE INDEX idx_pav_definition ON product_attribute_values(attribute_definition_id);
