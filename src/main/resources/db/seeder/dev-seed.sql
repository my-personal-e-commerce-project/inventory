-- ==========================================================
-- SEEDER MASIVO USA:  docker exec -i inventory-postgres-1 psql -U root -d inventory < src/main/resources/db/seeder/dev-seed.sql
-- ==========================================================

-- 1. Matamos cualquier proceso que esté usando el slot (el worker de Go)
SELECT pg_terminate_backend(active_pid) 
FROM pg_replication_slots 
WHERE slot_name = 'outbox_worker_slot';

-- 2. Borramos el slot (esto elimina el LSN asociado en el servidor)
SELECT pg_drop_replication_slot('outbox_worker_slot');

-- 3. Borramos la publicación para que no queden filtros viejos
DROP PUBLICATION IF EXISTS dbz_publication;

-- 4. Creamos la publicación de cero apuntando a tu tabla
CREATE PUBLICATION dbz_publication FOR TABLE outbox;

-- 5. OPCIONAL: Si querés que el worker ignore TODO lo viejo y arranque desde "ahora"
-- creamos el slot manualmente antes de prender el worker:
SELECT pg_create_logical_replication_slot('outbox_worker_slot', 'pgoutput');

-- 1. LIMPIEZA TOTAL
TRUNCATE TABLE 
    product_attribute_values, 
    product_categories, 
    CategoryAttribute, 
    products, 
    Category, 
    AttributeDefinition 
RESTART IDENTITY CASCADE;

-- 2. DEFINICIONES DE ATRIBUTOS
INSERT INTO AttributeDefinition (id, name, slug, type, is_global) VALUES
(gen_random_uuid(), 'Marca', 'marca', 'STRING', TRUE),
(gen_random_uuid(), 'Garantía (meses)', 'garantia-meses', 'INTEGER', TRUE),
(gen_random_uuid(), 'Potencia (Watts)', 'potencia-watts', 'INTEGER', FALSE),
(gen_random_uuid(), 'Es Inalámbrico', 'es-inalambrico', 'BOOLEAN', FALSE),
(gen_random_uuid(), 'Capacidad de Batería (mAh)', 'capacidad-bateria', 'DOUBLE', FALSE),
(gen_random_uuid(), 'Resolución Pantalla', 'resolucion-pantalla', 'STRING', FALSE),
(gen_random_uuid(), 'RAM (GB)', 'ram-gb', 'INTEGER', FALSE),
(gen_random_uuid(), 'Capacidad Carga (kg)', 'capacidad-carga-kg', 'DOUBLE', FALSE);

-- 3. CATEGORÍAS
INSERT INTO Category (id, name, slug, parent_id) VALUES
('cat-herr', 'Herramientas', 'herramientas', NULL),
('cat-elec', 'Electrónica', 'electronica', NULL),
('cat-hogar', 'Hogar y Electrodomésticos', 'hogar', NULL);

INSERT INTO Category (id, name, slug, parent_id) VALUES
('cat-herr-elec', 'Herramientas Eléctricas', 'herramientas-electricas', 'cat-herr'),
('cat-smartphones', 'Celulares', 'smartphones', 'cat-elec'),
('cat-laptops', 'Laptops Gamer', 'laptops-gamer', 'cat-elec'),
('cat-lavado', 'Lavarropas', 'lavarropas', 'cat-hogar');

-- 4. RELACIÓN CATEGORÍA-ATRIBUTO
INSERT INTO CategoryAttribute (id, category_id, attribute_definition_id, is_required, is_filterable, is_sortable)
SELECT gen_random_uuid(), 'cat-laptops', id, TRUE, TRUE, TRUE 
FROM AttributeDefinition WHERE slug IN ('ram-gb', 'resolucion-pantalla');

INSERT INTO CategoryAttribute (id, category_id, attribute_definition_id, is_required, is_filterable, is_sortable)
SELECT gen_random_uuid(), 'cat-lavado', id, TRUE, TRUE, TRUE 
FROM AttributeDefinition WHERE slug IN ('capacidad-carga-kg');

-- 5. PRODUCTOS (20+ Productos para Testing de SEO y Relevancia)
INSERT INTO products (id, title, slug, description, images, tags, price, stock) VALUES
-- HERRAMIENTAS (10+)
('h-1', 'Taladro DeWalt DCD771', 'dewalt-dcd771', 'Taladro atornillador inalámbrico 20V.', '{"d1.jpg"}', '{"herramienta", "pro", "bateria"}', 150.00, 10),
('h-2', 'Amoladora Bosch GWS 700', 'bosch-gws-700', 'Amoladora angular 710W profesional.', '{"b1.jpg"}', '{"herramienta", "corte", "taller"}', 85.00, 15),
('h-3', 'Rotomartillo Makita HR2470', 'makita-hr2470', 'Rotomartillo potente para hormigón.', '{"m1.jpg"}', '{"herramienta", "construccion", "heavy"}', 210.00, 8),
('h-4', 'Sierra Circular Skil 5402', 'skil-5402', 'Sierra circular 1400W para madera.', '{"s1.jpg"}', '{"herramienta", "madera", "carpinteria"}', 120.00, 12),
('h-5', 'Lijadora Orbital Black+Decker', 'bd-orbital', 'Lijadora compacta para terminaciones.', '{"bd1.jpg"}', '{"herramienta", "barato", "hogar"}', 45.00, 25),
('h-6', 'Soldadora Inverter Lusqtoff', 'lusqtoff-200', 'Soldadora compacta 200A turbo ventilada.', '{"l1.jpg"}', '{"herramienta", "soldadura", "pro"}', 180.00, 5),
('h-7', 'Compresor de Aire Schulz', 'schulz-2hp', 'Compresor 2HP 25 litros profesional.', '{"sch1.jpg"}', '{"herramienta", "aire", "taller"}', 250.00, 4),
('h-8', 'Atornillador de Impacto Milwaukee', 'milwaukee-m18', 'Torque extremo para mecanica profesional.', '{"mil1.jpg"}', '{"herramienta", "mecanica", "bateria", "pro"}', 299.00, 6),
('h-9', 'Hidrolavadora Karcher K2', 'karcher-k2', 'Limpieza a presión para el auto y hogar.', '{"k2.jpg"}', '{"herramienta", "limpieza", "barato"}', 130.00, 20),
('h-10', 'Cepillo Eléctrico Stanley', 'stanley-750w', 'Cepillo para madera 750W con rebaje.', '{"st1.jpg"}', '{"herramienta", "madera", "stanley"}', 95.00, 9),

-- LAPTOPS GAMER (5+)
('l-1', 'Razer Blade 16 Stealth', 'razer-blade-16', 'Laptop gamer con RTX 4090 y pantalla OLED.', '{"rz1.jpg"}', '{"laptop", "gamer", "razer", "premium", "alta gama"}', 3500.00, 3),
('l-2', 'HP Victus 15', 'hp-victus-15', 'Laptop gamer económica con RTX 3050.', '{"hpv1.jpg"}', '{"laptop", "gamer", "hp", "barato", "estudiante"}', 850.00, 15),
('l-3', 'Lenovo Legion Slim 5', 'lenovo-legion-5', 'Equilibrio perfecto entre potencia y peso.', '{"len1.jpg"}', '{"laptop", "gamer", "lenovo", "calidad precio"}', 1400.00, 7),
('l-4', 'HP Omen 17', 'hp-omen-17', 'Pantalla gigante de 17 pulgadas y i9.', '{"hpo1.jpg"}', '{"laptop", "gamer", "hp", "pro"}', 2200.00, 5),
('l-5', 'Lenovo LOQ 15', 'lenovo-loq-15', 'Entrada al mundo gaming con Lenovo.', '{"loq.jpg"}', '{"laptop", "gamer", "lenovo", "economico"}', 900.00, 10),

-- LAVARROPAS (3)
('lav-1', 'Lavarropas Samsung EcoBubble', 'samsung-eco-10kg', 'Carga frontal 10kg tecnología inverter.', '{"lav1.jpg"}', '{"lavarropas", "hogar", "samsung", "inverter"}', 800.00, 6),
('lav-2', 'Lavarropas LG Vivace', 'lg-vivace-8kg', 'Inteligencia artificial para el lavado.', '{"lav2.jpg"}', '{"lavarropas", "lg", "tecnologia"}', 750.00, 8),
('lav-3', 'Lavarropas James Carga Superior', 'james-superior', 'Económico y confiable para Uruguay.', '{"lav3.jpg"}', '{"lavarropas", "barato", "hogar", "james"}', 400.00, 20);

-- 6. RELACIÓN PRODUCTO-CATEGORÍA
INSERT INTO product_categories (product_id, category_id) VALUES
('h-1', 'cat-herr-elec'), ('h-2', 'cat-herr-elec'), ('h-3', 'cat-herr-elec'), ('h-4', 'cat-herr-elec'), ('h-5', 'cat-herr-elec'),
('h-6', 'cat-herr-elec'), ('h-7', 'cat-herr-elec'), ('h-8', 'cat-herr-elec'), ('h-9', 'cat-herr-elec'), ('h-10', 'cat-herr-elec'),
('l-1', 'cat-laptops'), ('l-2', 'cat-laptops'), ('l-3', 'cat-laptops'), ('l-4', 'cat-laptops'), ('l-5', 'cat-laptops'),
('lav-1', 'cat-lavado'), ('lav-2', 'cat-lavado'), ('lav-3', 'cat-lavado');

-- 7. VALORES DE ATRIBUTOS (Ejemplos variados)
INSERT INTO product_attribute_values (id, product_id, attribute_definition_id, string_value, integer_value, boolean_value, double_value) VALUES
-- Razer Blade
(gen_random_uuid(), 'l-1', (SELECT id FROM AttributeDefinition WHERE slug = 'marca'), 'Razer', NULL, NULL, NULL),
(gen_random_uuid(), 'l-1', (SELECT id FROM AttributeDefinition WHERE slug = 'ram-gb'), NULL, 32, NULL, NULL),
-- HP Victus
(gen_random_uuid(), 'l-2', (SELECT id FROM AttributeDefinition WHERE slug = 'marca'), 'HP', NULL, NULL, NULL),
(gen_random_uuid(), 'l-2', (SELECT id FROM AttributeDefinition WHERE slug = 'ram-gb'), NULL, 16, NULL, NULL),
-- Lavarropas James
(gen_random_uuid(), 'lav-3', (SELECT id FROM AttributeDefinition WHERE slug = 'marca'), 'James', NULL, NULL, NULL),
(gen_random_uuid(), 'lav-3', (SELECT id FROM AttributeDefinition WHERE slug = 'capacidad-carga-kg'), NULL, NULL, NULL, 6.5);
