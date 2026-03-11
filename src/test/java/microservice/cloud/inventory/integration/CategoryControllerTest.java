package microservice.cloud.inventory.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.DeserializationFeature;
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
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    // TODO: test de borrar un category attribute y que datos expone en la tabla outbox
    // TODO: test de actualizar un category attribute y que datos expone en la tabla outbox
    // TODO: test de actualizar una categoria y que datos expone en la tabla outbox
    // TODO: test de borrar una categoria y que datos expone en la tabla outbox
  
    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE categories RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE outbox RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE attribute_definition RESTART IDENTITY CASCADE");
    }

    private void createCategory() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "test",
                            "slug": "test",
                            "parent_id": null,
                            "categoryAttributes": [
                              {
                                "attributeDefinition": {
                                  "name": "test:new",
                                  "slug": "test:new",
                                  "type": "STRING"
                                },
                                "is_required": true,
                                "is_filterable": true,
                                "is_sortable": true
                              }
                            ]
                        }
                    """)
                .with(jwt().jwt(j -> j.claim("realm_access", Map.of("roles", List.of("create_category")))
                                 .subject("random-user")))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    } 

    private void createCategoryAttribute(String category_id) throws Exception { 
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/categories/" + category_id + "/attributes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "attributeDefinition": {
                                "name": "test:new2",
                                "slug": "test:new2",
                                "type": "STRING"
                            },
                            "is_required": true,
                            "is_filterable": true,
                            "is_sortable": true
                        }
                    """)
                .with(jwt().jwt(j -> j.claim("realm_access", Map.of("roles", List.of("update_category")))
                                 .subject("random-user")))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void should_createACategoryAttributeAndShowItInOutboxTable() throws Exception {
        createCategory();
            
        String sql = "SELECT payload FROM outbox WHERE aggregate_type = 'category' ORDER BY created_at DESC OFFSET 1 LIMIT 2;";
       
        String jsonPayload = jdbcTemplate.queryForObject(sql, String.class);
       
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        Map<String, Object> category = mapper.readValue(jsonPayload, Map.class);
        
        Assertions.assertNotNull(category);
        
        Assertions.assertEquals("test", category.get("name"));

        List<?> attributes = (List<?>) category.get("attributes");
        Assertions.assertEquals(1, attributes.size());
    }

    @Test
    public void should_showTheCompleteAggregateInThePayloadOfTheOutboxTableWhenAddedANewAttribute() throws Exception {
        createCategory();

        String sql = "SELECT payload FROM outbox WHERE aggregate_type = 'category' ORDER BY created_at DESC OFFSET 1 LIMIT 2;";
        
        String jsonPayload = jdbcTemplate.queryForObject(sql, String.class);
       
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        Map<String, Object> category = mapper.readValue(jsonPayload, Map.class);
       
        String category_id = (String) category.get("id");

        createCategoryAttribute(category_id);
        
        sql = "SELECT payload FROM outbox WHERE aggregate_type = 'category' ORDER BY created_at DESC OFFSET 2 LIMIT 3;";

        jsonPayload = jdbcTemplate.queryForObject(sql, String.class);
       
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        Map<String, Object> new_category = mapper.readValue(jsonPayload, Map.class);

        Assertions.assertNotNull(new_category);
    }

    @Test
    public void should_deleteACategoryAttributeAndShowItInOutboxTable() throws Exception {
        createCategory();

        String sql = "SELECT payload FROM outbox WHERE aggregate_type = 'category' ORDER BY created_at DESC OFFSET 1 LIMIT 2;";
        
        String jsonPayload = jdbcTemplate.queryForObject(sql, String.class);
       
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        Map<String, Object> category = mapper.readValue(jsonPayload, Map.class);
       
        String category_id = (String) category.get("id");

        createCategoryAttribute(category_id);
        
        sql = "SELECT payload FROM outbox WHERE aggregate_type = 'category' ORDER BY created_at DESC OFFSET 2 LIMIT 3;";

        jsonPayload = jdbcTemplate.queryForObject(sql, String.class);
       
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        Map<String, Object> new_category = mapper.readValue(jsonPayload, Map.class);

        List<?> attributes = (List<?>) new_category.get("attributes");
        
        String attrId = ""; 
        
        if (!attributes.isEmpty()) {
            Map<String, Object> firstAttribute = (Map<String, Object>) attributes.get(0);
            attrId = (String) firstAttribute.get("id");
        }

        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/api/v1/categories/" + category_id + "/attributes/" + attrId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "attributeDefinition": {
                                "name": "test:new2",
                                "slug": "test:new2",
                                "type": "STRING"
                            },
                            "is_required": true,
                            "is_filterable": true,
                            "is_sortable": true
                        }
                    """)
                .with(jwt().jwt(j -> j.claim("realm_access", Map.of("roles", List.of("update_category")))
                                 .subject("random-user")))
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void should_updateACategoryAttributeAndShowItInOutboxTable() throws Exception {
        createCategory();

        String sql = "SELECT payload FROM outbox WHERE aggregate_type = 'category' ORDER BY created_at DESC OFFSET 1 LIMIT 2;";
        
        String jsonPayload = jdbcTemplate.queryForObject(sql, String.class);
       
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        Map<String, Object> category = mapper.readValue(jsonPayload, Map.class);
       
        String category_id = (String) category.get("id");

        String attrId = ""; 
        String attrDefId = ""; 

        List<?> attributes = (List<?>) category.get("attributes");

        if (attributes != null && !attributes.isEmpty()) {
            Map<String, Object> firstAttribute = (Map<String, Object>) attributes.get(0);
            
            Object idObj = firstAttribute.get("id") != null ? firstAttribute.get("id") : firstAttribute.get("attribute_id");
            attrId = String.valueOf(idObj);

            Map<String, Object> attrDef = (Map<String, Object>) firstAttribute.get("attributeDefinition");
            if (attrDef == null) {
                attrDef = (Map<String, Object>) firstAttribute.get("attribute_definition");
            }

            if (attrDef != null) {
                attrDefId = String.valueOf(attrDef.get("id"));
            } else {
                throw new RuntimeException("No encontré 'attributeDefinition' ni 'attribute_definition'. Keys disponibles: " + firstAttribute.keySet());
            }
        }

        mockMvc.perform(
            MockMvcRequestBuilders
                .put("/api/v1/categories/" + category_id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "test",
                        "slug": "test",
                        "parent_id": null,
                        "categoryAttributes": [
                            {
                                "id": "%s",
                                "attributeDefinition": {
                                    "id": "%s",
                                    "name": "test:new2",
                                    "slug": "test:new2",
                                    "type": "STRING"
                                },
                                "is_required": true,
                                "is_filterable": true,
                                "is_sortable": true
                            }
                        ]
                    }
                    """.formatted(category_id, attrId, attrDefId))
                .with(jwt().jwt(j -> j.claim("realm_access", Map.of("roles", List.of("update_category")))
                                 .subject("random-user")))
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        sql = "SELECT payload FROM outbox WHERE aggregate_type = 'category' ORDER BY created_at DESC OFFSET 1 LIMIT 3;";
        
        jsonPayload = jdbcTemplate.queryForObject(sql, String.class);
      
        System.out.println(jsonPayload);
        System.out.println(jsonPayload);
        System.out.println(jsonPayload);
        System.out.println(jsonPayload);
        System.out.println(jsonPayload);
        System.out.println(jsonPayload);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        Map<String, Object> new_category = mapper.readValue(jsonPayload, Map.class);
        
        Assertions.assertNotNull(new_category);
    }
}
