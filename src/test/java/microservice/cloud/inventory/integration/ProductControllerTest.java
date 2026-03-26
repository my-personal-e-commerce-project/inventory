package microservice.cloud.inventory.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.sql.SQLException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import(BaseIntegrationTest.class)
@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.import-check.enabled=false",
        "spring.config.import=optional:configserver:"
    }
)
public class ProductControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
      
    @BeforeEach
    void tearDown() throws SQLException {
        jdbcTemplate.execute("TRUNCATE TABLE category RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE outbox RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE attributedefinition RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE products RESTART IDENTITY CASCADE");
    }

    @Test
    public void should_return401BecauseYouDoNotHaveProductCreationPermissions() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "test",
                        "slug": "test",
                        "description": "test",
                        "categories": ["test"],
                        "attributes": [],
                        "price": 22.2,
                        "stock": 22,
                        "images": null,
                        "tags": null
                    }
                """)
                .with(
                    jwt().jwt(j -> j.subject("random-user"))
                )
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(
                MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON)
            );
    }
    
    @Test
    public void should_createANewProductAndShowItInOutboxTable() throws Exception {
        jdbcTemplate.update("INSERT INTO category (id, name, slug, parent_id) VALUES ('1234', 'test', 'test', NULL);");

        jdbcTemplate.update("""
            INSERT INTO attributedefinition (id, name, slug, type, is_global) VALUES (
                '1234',
                'test',
                'test',
                'STRING',
                false
            );
            """);

        MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "test",
                        "slug": "test",
                        "description": "test",
                        "categories": ["1234"],
                        "attributes": [
                            {
                                "attribute_definition_id": 1234,
                                "string_value": "test"
                            }
                        ],
                        "price": 22.2,
                        "stock": 22,
                        "images": null,
                        "tags": null
                    }
                """)
                .with(
                    jwt().jwt(j -> j.claim("realm_access", 
                        Map.of("roles", java.util.List.of("create_product"))) 
                        .subject("random-user")
                    )
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(
                MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String productId = com.jayway.jsonpath.JsonPath.read(responseJson, "$.payload.id");
        String categoryAttributeId = com.jayway.jsonpath.JsonPath.read(responseJson, "$.payload.attributes[0].id");

        Integer productCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM products", Integer.class);
       
        Assertions.assertTrue(productCount == 1);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);
        
        Assertions.assertTrue(outboxCount == 4);

        Map<String, Object> outboxEntry = jdbcTemplate.queryForMap(
            "SELECT * FROM outbox WHERE id = 4;"
        );

        Assertions.assertTrue(outboxEntry.get("aggregate_type").equals("product"));
   
        Assertions.assertTrue(outboxEntry.get("type").equals("PRODUCT_UPDATED"));

        JsonNode actual = mapper.readTree(outboxEntry.get("payload").toString());

        JsonNode expected = mapper.readTree("""
            {
                "id": "%s", 
                "slug": "test",
                "tags": null,
                "price": 22.2,
                "stock": 22,
                "title": "test",
                "images": null,
                "attributes": [
                    {
                        "id": "%s",
                        "double_value": null,
                        "string_value": "test",
                        "boolean_value": null,
                        "integer_value": null,
                        "attribute_definition_id": "1234"
                    }
                ],
                "categories": ["test"],
                "description": "test"
            } 
            """.formatted(productId, categoryAttributeId)
        );

        Assertions.assertTrue(actual.equals(expected));
    }
   
    // TODO: crear un nuevo producto, y validar attributos gloables y los category attribute
    // TODO: crear un nuevo producto, y validar category attribute
    // TODO: crear un nuevo producto, y validar product attribute value extras
    
    // TODO: actualizar un producto pero sin los permisos
    // TODO: actualizar un producto y validar attributos gloables y los category attribute
    
    @Test
    public void should_updateProductAndShowItInOutboxTable() throws Exception {
        jdbcTemplate.update("INSERT INTO category (id, name, slug, parent_id) VALUES ('1234', 'test', 'test', NULL);");

        jdbcTemplate.update("""
            INSERT INTO attributedefinition (id, name, slug, type, is_global) VALUES (
                '1234',
                'test',
                'test',
                'STRING',
                false
            );
            """);

        MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "test",
                        "slug": "test",
                        "description": "test",
                        "categories": ["1234"],
                        "attributes": [
                            {
                                "attribute_definition_id": 1234,
                                "string_value": "test"
                            }
                        ],
                        "price": 22.2,
                        "stock": 22,
                        "images": null,
                        "tags": null
                    }
                """)
                .with(
                    jwt().jwt(j -> j.claim("realm_access", 
                        Map.of("roles", java.util.List.of("create_product"))) 
                        .subject("random-user")
                    )
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(
                MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON)
            ).andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String categoryAttributeId = com.jayway.jsonpath.JsonPath.read(responseJson, "$.payload.attributes[0].id");
        String productId = com.jayway.jsonpath.JsonPath.read(responseJson, "$.payload.id");
       
        mockMvc.perform(
            MockMvcRequestBuilders
                .put("/api/v1/products/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "test2",
                        "slug": "test2",
                        "description": "test2",
                        "categories": ["1234"],
                        "attributes": [
                            {
                                "id": "%s",
                                "attribute_definition_id": 1234,
                                "string_value": "test2"
                            }
                        ],
                        "price": 22.2,
                        "stock": 22,
                        "images": null,
                        "tags": ["test"]
                    }
                """.formatted(categoryAttributeId))
                .with(
                    jwt().jwt(j -> j.claim("realm_access", 
                        Map.of("roles", java.util.List.of("update_product"))) 
                        .subject("random-user")
                    )
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(
                MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON)
            );

        Integer productCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM products", Integer.class);
       
        Assertions.assertTrue(productCount == 1);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);
       
        Assertions.assertTrue(outboxCount == 9);

        Map<String, Object> outboxEntry = jdbcTemplate.queryForMap(
            "SELECT * FROM outbox WHERE id = 9;"
        );

        Assertions.assertTrue(outboxEntry.get("aggregate_type").equals("product"));
   
        Assertions.assertTrue(outboxEntry.get("type").equals("PRODUCT_UPDATED"));

        JsonNode actual = mapper.readTree(outboxEntry.get("payload").toString());

        JsonNode expected = mapper.readTree("""
            {
                "id": "%s", 
                "slug": "test2",
                "tags": ["test"],
                "price": 22.2,
                "stock": 22,
                "title": "test2",
                "images": null,
                "attributes": [
                    {
                        "id": "%s",
                        "double_value": null,
                        "string_value": "test2",
                        "boolean_value": null,
                        "integer_value": null,
                        "attribute_definition_id": "1234"
                    }
                ],
                "categories": ["test"],
                "description": "test2"
            } 
            """.formatted(productId, categoryAttributeId)
        );

        Assertions.assertTrue(actual.equals(expected));
    }
    
    // TODO: actualizar un producto y validar category attribute
    // TODO: actualizar un producto y validar product attribute value extras    
    // TODO: actualizar un producto y su product attribute value
    // TODO: actualizar un producto y que valide que haya una sola categoria
    // TODO: actualizar un producto y validar si estan todos los product attribute value en la lista de product attribute value para actualizar
   
    // TODO: crear un nuevo product attribute value sin permisos
    // TODO: crear un nuevo product attribute value y validar su attribute definition id ya existe 
    // TODO: crear un nuevo product attribute value
    
    // TODO: remove product attribute pero sin permisos
    // TODO: remove product attribute pero no esta en este producto
    // TODO: remove product attribute pero es requerido por la categoria
   
    @Test
    public void should_removeProductAttributeValueAndShowItInOutboxTable() throws Exception {
        jdbcTemplate.update("INSERT INTO category (id, name, slug, parent_id) VALUES ('1234', 'test', 'test', NULL);");

        jdbcTemplate.update("""
            INSERT INTO attributedefinition (id, name, slug, type, is_global) VALUES (
                '1234',
                'test',
                'test',
                'STRING',
                false
            );
            """);

        MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "test",
                        "slug": "test",
                        "description": "test",
                        "categories": ["1234"],
                        "attributes": [
                            {
                                "attribute_definition_id": 1234,
                                "string_value": "test"
                            }
                        ],
                        "price": 22.2,
                        "stock": 22,
                        "images": null,
                        "tags": null
                    }
                """)
                .with(
                    jwt().jwt(j -> j.claim("realm_access", 
                        Map.of("roles", java.util.List.of("create_product"))) 
                        .subject("random-user")
                    )
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(
                MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON)
            ).andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String productAttributeId = com.jayway.jsonpath.JsonPath.read(responseJson, "$.payload.attributes[0].id");
        String productId = com.jayway.jsonpath.JsonPath.read(responseJson, "$.payload.id");
       
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/api/v1/products/test/attributes/%s".formatted(productAttributeId))
                .with(
                    jwt().jwt(j -> j.claim("realm_access", 
                        Map.of("roles", java.util.List.of("update_product"))) 
                        .subject("random-user")
                    )
                )
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        Integer productCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM products", Integer.class);
       
        Assertions.assertTrue(productCount == 1);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);
       
        Assertions.assertTrue(outboxCount == 8);

        Map<String, Object> outboxEntry = jdbcTemplate.queryForMap(
            "SELECT * FROM outbox WHERE id = 8;"
        );

        Assertions.assertTrue(outboxEntry.get("aggregate_type").equals("product"));
   
        Assertions.assertTrue(outboxEntry.get("type").equals("PRODUCT_UPDATED"));

        JsonNode actual = mapper.readTree(outboxEntry.get("payload").toString());

        JsonNode expected = mapper.readTree("""
            {
                "id": "%s", 
                "slug": "test",
                "tags": null,
                "price": 22.2,
                "stock": 22,
                "title": "test",
                "images": null,
                "attributes": [],
                "categories": ["test"],
                "description": "test"
            } 
            """.formatted(productId)
        );

        Assertions.assertTrue(actual.equals(expected));
    }
 
    @Test
    public void should_removeProductAndShowItInOutboxTable() throws Exception {
        jdbcTemplate.update("INSERT INTO category (id, name, slug, parent_id) VALUES ('1234', 'test', 'test', NULL);");

        jdbcTemplate.update("""
            INSERT INTO attributedefinition (id, name, slug, type, is_global) VALUES (
                '1234',
                'test',
                'test',
                'STRING',
                false
            );
            """);

        MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "test",
                        "slug": "test",
                        "description": "test",
                        "categories": ["1234"],
                        "attributes": [
                            {
                                "attribute_definition_id": 1234,
                                "string_value": "test"
                            }
                        ],
                        "price": 22.2,
                        "stock": 22,
                        "images": null,
                        "tags": null
                    }
                """)
                .with(
                    jwt().jwt(j -> j.claim("realm_access", 
                        Map.of("roles", java.util.List.of("create_product"))) 
                        .subject("random-user")
                    )
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(
                MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON)
            ).andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String productId = com.jayway.jsonpath.JsonPath.read(responseJson, "$.payload.id");
       
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/api/v1/products/test")
                .with(
                    jwt().jwt(j -> j.claim("realm_access", 
                        Map.of("roles", java.util.List.of("delete_product"))) 
                        .subject("random-user")
                    )
                )
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        Integer productCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM products", Integer.class);
       
        Assertions.assertTrue(productCount == 0);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);
      
        Assertions.assertTrue(outboxCount == 7);

        Map<String, Object> outboxEntry = jdbcTemplate.queryForMap(
            "SELECT * FROM outbox WHERE id = 7;"
        );

        Assertions.assertTrue(outboxEntry.get("aggregate_type").equals("product"));
  
        Assertions.assertTrue(outboxEntry.get("type").equals("PRODUCT_DELETED"));

        JsonNode actual = mapper.readTree(outboxEntry.get("payload").toString());

        JsonNode expected = mapper.readTree("""
            {"id":"%s","deleted":true}
            """.formatted(
                    productId
                )
            );

        Assertions.assertTrue(actual.equals(expected));
    }   
}
