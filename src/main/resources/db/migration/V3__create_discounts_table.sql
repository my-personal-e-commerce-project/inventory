CREATE TABLE discounts (
    id VARCHAR(255) PRIMARY KEY UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    discount_type VARCHAR(255) NOT NULL,
    decrement_value DOUBLE PRECISION DEFAULT NULL,
    percentage_value DOUBLE PRECISION DEFAULT NULL,

    valid_all_categories BOOLEAN DEFAULT NULL,

    min_price DOUBLE PRECISION DEFAULT NULL,
    max_price DOUBLE PRECISION DEFAULT NULL,
    min_stock int DEFAULT NULL,
    max_stock int DEFAULT NULL,

    auto_apply BOOLEAN DEFAULT NULL,
    expired_at timestamp DEFAULT now()
);

CREATE TABLE discount_categories (
    discount_id VARCHAR(255) NOT NULL,
    category_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (discount_id, category_id),
    CONSTRAINT fk_product FOREIGN KEY (discount_id) REFERENCES discounts (id) ON DELETE CASCADE,
    CONSTRAINT fk_discount FOREIGN KEY (category_id) REFERENCES Category (id) ON DELETE CASCADE
);
