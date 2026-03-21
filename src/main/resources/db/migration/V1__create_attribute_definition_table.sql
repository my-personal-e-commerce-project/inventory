CREATE TABLE AttributeDefinition (
    id VARCHAR(255) PRIMARY KEY UNIQUE NOT NULL,
    name VARCHAR(255),
    slug VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(255) NOT NULL,
    is_global BOOLEAN DEFAULT FALSE
);

ALTER TABLE AttributeDefinition REPLICA IDENTITY FULL;

CREATE OR REPLACE FUNCTION fn_build_attribute_definition_outbox()
RETURNS TRIGGER AS $$
DECLARE
    v_payload JSONB;
BEGIN
    
    IF (TG_OP = 'INSERT' AND NEW.is_global = TRUE) THEN
        v_payload = jsonb_build_object(
            'id', NEW.id,
            'name', NEW.name,
            'slug', NEW.slug,
            'type', NEW.type,
            'is_global', NEW.is_global
        );

        INSERT INTO outbox (aggregate_type, aggregate_id, type, payload, created_at)
        VALUES ('attribute_definition', NEW.id, 'ATTRIBUTE_CREATED', v_payload, now());
    ELSIF (TG_OP = 'UPDATE' AND OLD.is_global = TRUE) THEN
        v_payload = jsonb_build_object(
            'id', NEW.id,
            'name', NEW.name,
            'slug', NEW.slug,
            'type', NEW.type,
            'is_global', NEW.is_global
        );

        INSERT INTO outbox (aggregate_type, aggregate_id, type, payload, created_at)
        VALUES ('attribute_definition', NEW.id, 'ATTRIBUTE_UPDATED', v_payload, now());
    ELSIF (TG_OP = 'DELETE' AND OLD.is_global = TRUE) THEN
        INSERT INTO outbox (aggregate_type, aggregate_id, type, payload, created_at)
        VALUES ('attribute_definition', NEW.id, 'ATTRIBUTE_DELETED', jsonb_build_object('id', NEW.id, 'deleted', true), now());
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_attribute_definition_changes
AFTER INSERT OR UPDATE OR DELETE ON AttributeDefinition
FOR EACH ROW EXECUTE FUNCTION fn_build_attribute_definition_outbox();
