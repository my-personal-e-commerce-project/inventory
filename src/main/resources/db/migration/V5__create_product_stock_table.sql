CREATE TABLE product_stock (
    id VARCHAR(255) PRIMARY KEY UNIQUE NOT NULL,
    quantity int DEFAULT 1,
    product_id varchar(255) UNIQUE NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT fk_product_id
    FOREIGN KEY (product_id)
    REFERENCES products (id)
    ON DELETE CASCADE
);
