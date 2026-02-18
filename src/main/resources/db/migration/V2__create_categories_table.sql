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

CREATE OR REPLACE FUNCTION fn_build_category_outbox()
RETURNS TRIGGER AS $$
DECLARE
    v_payload JSONB;
BEGIN
    IF (TG_OP = 'DELETE') THEN
        v_payload = jsonb_build_object(
            'id', OLD.id,
            'deleted', true
        );
        
        INSERT INTO outbox (id, aggregate_type, aggregate_id, type, payload, created_at)
        VALUES (gen_random_uuid(), 'category', OLD.id, 'CATEGORY_DELETED', v_payload, now());
    ELSIF (TG_OP = 'INSERT') THEN
        SELECT jsonb_build_object(
            'id', NEW.id,
            'name', NEW.name,
            'slug', NEW.slug,
            'parent_id', NEW.parent_id,
            'attributes', (
                SELECT coalesce(jsonb_agg(attr), '[]'::jsonb)
                FROM (
                    SELECT 
                        ca.id, 
                        ca.is_required, 
                        ca.is_filterable, 
                        ca.is_sortable,
                        row_to_json(ad)::jsonb AS attribute
                    FROM category_attributes ca
                    INNER JOIN attribute_definition ad ON ca.attribute_definition_id = ad.id
                    WHERE ca.category_id = NEW.id
                ) attr
            )
        ) INTO v_payload;

        INSERT INTO outbox (id, aggregate_type, aggregate_id, type, payload, created_at)
        VALUES (gen_random_uuid(), 'category', NEW.id, 'CATEGORY_CREATED', v_payload, now());
    ELSE
        SELECT jsonb_build_object(
            'id', NEW.id,
            'name', NEW.name,
            'slug', NEW.slug,
            'parent_id', NEW.parent_id,
            'attributes', (
                SELECT coalesce(jsonb_agg(attr), '[]'::jsonb)
                FROM (
                    SELECT 
                        ca.id, 
                        ca.is_required, 
                        ca.is_filterable, 
                        ca.is_sortable,
                        row_to_json(ad)::jsonb AS attribute_definition
                    FROM category_attributes ca
                    INNER JOIN attribute_definition ad ON ca.attribute_definition_id = ad.id
                    WHERE ca.category_id = NEW.id
                ) attr
            )
        ) INTO v_payload;

        INSERT INTO outbox (id, aggregate_type, aggregate_id, type, payload, created_at)
        VALUES (gen_random_uuid(), 'category', NEW.id, 'CATEGORY_UPDATED', v_payload, now());
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_category_changes
AFTER INSERT OR UPDATE OR DELETE ON categories
FOR EACH ROW EXECUTE FUNCTION fn_build_category_outbox();

CREATE OR REPLACE FUNCTION fn_refresh_parent_category()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE categories SET id = id WHERE id = COALESCE(NEW.category_id, OLD.category_id);
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_attribute_changes
AFTER INSERT OR UPDATE OR DELETE ON category_attributes
FOR EACH ROW EXECUTE FUNCTION fn_refresh_parent_category();
