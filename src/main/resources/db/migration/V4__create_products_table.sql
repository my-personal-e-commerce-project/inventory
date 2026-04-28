CREATE TABLE products (
    id VARCHAR(255) PRIMARY KEY UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    images varchar[],
    tags varchar[],
    price DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL
);

CREATE TABLE product_categories (
    product_id VARCHAR(255) NOT NULL,
    category_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES Category (id) ON DELETE CASCADE
);

CREATE TABLE product_attribute_values (
    id VARCHAR(255) PRIMARY KEY UNIQUE NOT NULL,
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
        REFERENCES AttributeDefinition (id) 
        ON DELETE CASCADE,

    CONSTRAINT uq_product_attribute_definition 
        UNIQUE (product_id, attribute_definition_id)
);

CREATE INDEX idx_pav_product ON product_attribute_values(product_id);
CREATE INDEX idx_pav_definition ON product_attribute_values(attribute_definition_id);



CREATE OR REPLACE FUNCTION fn_build_product_outbox()
RETURNS TRIGGER AS $$
DECLARE
    v_payload JSONB;
BEGIN
    IF (TG_OP = 'DELETE') THEN
        INSERT INTO outbox (aggregate_type, aggregate_id, type, payload, created_at)
        VALUES ('product', OLD.id, 'PRODUCT_DELETED', jsonb_build_object('id', OLD.id, 'deleted', true), now());
    ELSE
        SELECT jsonb_build_object(
            'id', NEW.id,
            'title', NEW.title,
            'slug', NEW.slug,
            'description', NEW.description,
            'price', NEW.price,
            'stock', NEW.stock,
            'images', NEW.images,
            'tags', NEW.tags,
            'categories', (
                SELECT coalesce(
                    jsonb_agg(jsonb_build_object('slug', c.slug, 'name', c.name)), 
                    '[]'::jsonb
                )
                FROM product_categories pc
                JOIN category c ON pc.category_id = c.id
                WHERE pc.product_id = NEW.id
            ),
            'attributes', (
                SELECT coalesce(jsonb_agg(attr), '[]'::jsonb) 
                FROM (
                    SELECT 
                        pav.id, 
                        pav.attribute_definition_id, 
                        ad.slug AS attribute_definition_slug,
                        ad.name AS attribute_definition_name,
                        pav.string_value, 
                        pav.integer_value, 
                        pav.double_value, 
                        pav.boolean_value
                    FROM product_attribute_values pav
                    JOIN AttributeDefinition ad ON pav.attribute_definition_id = ad.id
                    WHERE pav.product_id = NEW.id
                ) attr
            )
        ) INTO v_payload;

        IF (TG_OP = 'INSERT') THEN
            INSERT INTO outbox (aggregate_type, aggregate_id, type, payload, created_at)
            VALUES ('product', NEW.id, 'PRODUCT_CREATED', v_payload, now());
        ELSE
            INSERT INTO outbox (aggregate_type, aggregate_id, type, payload, created_at)
            VALUES ('product', NEW.id, 'PRODUCT_UPDATED', v_payload, now());
        END IF;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER trg_product_changes
AFTER INSERT OR UPDATE OR DELETE ON products
FOR EACH ROW EXECUTE FUNCTION fn_build_product_outbox();



CREATE OR REPLACE FUNCTION fn_trigger_product_refresh()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE products SET id = id WHERE id = COALESCE(NEW.product_id, OLD.product_id);
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_refresh_from_pav AFTER INSERT OR UPDATE OR DELETE ON product_attribute_values FOR EACH ROW EXECUTE FUNCTION fn_trigger_product_refresh();

CREATE OR REPLACE FUNCTION fn_trigger_product_refresh_from_cat()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE products SET id = id WHERE id = COALESCE(NEW.product_id, OLD.product_id);
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_refresh_from_prod_cat AFTER INSERT OR UPDATE OR DELETE ON product_categories FOR EACH ROW EXECUTE FUNCTION fn_trigger_product_refresh_from_cat();
