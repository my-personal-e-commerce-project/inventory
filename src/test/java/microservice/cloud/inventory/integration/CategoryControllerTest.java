package microservice.cloud.inventory.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
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
public class CategoryControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
      
    @BeforeEach
    void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE category RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE outbox RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE attributedefinition RESTART IDENTITY CASCADE");
    }

    private String createAttributeDefinition() throws Exception {
        String query = """
            INSERT INTO attributedefinition (id, name, slug, type, is_global) VALUES (
                '1234',
                'test',
                'test',
                'STRING',
                false
            );
            """;

        jdbcTemplate.update(query);
       
        return "1234";
    }

    private Map<String, String> createCategory() throws Exception {
        String id = createAttributeDefinition();

        MvcResult result = mockMvc.perform(
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
                                "attribute_definition_id": "%s",
                                "is_required": true,
                                "is_filterable": true,
                                "is_sortable": true
                              }
                            ]
                        }
                    """.formatted(id))
                .with(jwt().jwt(j -> j.claim("realm_access", Map.of("roles", List.of("create_category")))
                                 .subject("random-user")))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(
                MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        Map<String, String> resultMap = new HashMap<>();

        resultMap.put(
            "slug", 
            com.jayway.jsonpath.JsonPath.read(responseJson, "$.payload.slug")
        );

        resultMap.put(
             "id",
            com.jayway.jsonpath.JsonPath.read(responseJson, "$.payload.id")
        );

        resultMap.put(
             "categoryAttributeId",
            com.jayway.jsonpath.JsonPath.read(responseJson, "$.payload.categoryAttributes[0].id")
        );

        resultMap.put(
             "attributeDefinitionId",
            com.jayway.jsonpath.JsonPath.read(responseJson, "$.payload.categoryAttributes[0].attribute_definition_id")
        );
    
        return resultMap;
    } 

    @Test
    public void should_createANewCategoryAndShowItInOutboxTable() throws Exception {
        Map<String, String> map = createCategory();

        String id = map.get("id");
        String categoryAttributeId = map.get("categoryAttributeId");
        String attributeDefinitionId = map.get("attributeDefinitionId");

        Integer categoryCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM category", Integer.class);
        
        Assertions.assertTrue(categoryCount == 1);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);

        Assertions.assertTrue(outboxCount == 2);

        Integer categoryAttributeCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM categoryattribute", Integer.class);
       
        Assertions.assertTrue(categoryAttributeCount == 1);

        Map<String, Object> outboxEntry = jdbcTemplate.queryForMap(
            "SELECT * FROM outbox WHERE id = 2;"
        );

        Assertions.assertTrue(outboxEntry.get("aggregate_type").equals("category"));
    
        Assertions.assertTrue(outboxEntry.get("type").equals("CATEGORY_UPDATED"));

        JsonNode actual = mapper.readTree(outboxEntry.get("payload").toString());

        JsonNode expected = mapper.readTree("""
            {
                "id": "%s", 
                "name": "test", 
                "slug": "test", 
                "parent_id": null, 
                "attributes": [
                    {
                        "id": "%s", 
                         "attribute": {
                             "id": "%s", 
                             "name": "test", 
                             "slug": "test", 
                             "type": "STRING", 
                             "is_global": false
                         }, 
                        "is_required": true, 
                        "is_sortable": true, 
                        "is_filterable": true
                    }
                ]
            } 
            """.formatted(id, categoryAttributeId, attributeDefinitionId));

        Assertions.assertTrue(actual.equals(expected));
    }

    private void updateCategory(Map<String, String> map) throws Exception {
        String slug = map.get("slug");
        String categoryAttributeId = map.get("categoryAttributeId");
        String attributeDefinitionId = map.get("attributeDefinitionId");

        mockMvc.perform(
            MockMvcRequestBuilders
                .put("/api/v1/categories/%s".formatted(slug))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "test2",
                            "slug": "test2",
                            "parent_id": null,
                            "categoryAttributes": [
                              {
                                "id": "%s",
                                "attribute_definition_id": "%s",
                                "is_required": true,
                                "is_filterable": false,
                                "is_sortable": true
                              }
                            ]
                        }
                    """.formatted(categoryAttributeId, attributeDefinitionId))
                .with(jwt().jwt(j -> j.claim("realm_access", Map.of("roles", List.of("create_category", "update_category")))
                                 .subject("random-user")))
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(
                MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON)
            );
    }

    @Test
    public void should_updateACategoryAndACategoryAttributeAndShowItInOutboxTable() throws Exception {
        Map<String, String> map = createCategory();

        updateCategory(map);

        Integer categoryCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM category", Integer.class);
       
        Assertions.assertTrue(categoryCount == 1);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);
       
        Assertions.assertTrue(outboxCount == 5);

        Map<String, Object> outboxEntry = jdbcTemplate.queryForMap(
            "SELECT * FROM outbox WHERE id = 5");

        Assertions.assertTrue(outboxEntry.get("aggregate_type").equals("category"));
    
        Assertions.assertTrue(outboxEntry.get("type").equals("CATEGORY_UPDATED"));

        JsonNode actual = mapper.readTree(outboxEntry.get("payload").toString());
        JsonNode expected = mapper.readTree("""
            {
                "id": "%s",
                "name": "test2",
                "slug": "test2",
                "parent_id": null,
                "attributes": [
                    {
                        "id": "%s", 
                         "attribute": {
                             "id": "%s", 
                             "name": "test", 
                             "slug": "test", 
                             "type": "STRING", 
                             "is_global": false
                         }, 
                        "is_required": true, 
                        "is_filterable": false,
                        "is_sortable": true
                    }
                ]
            }
            """.formatted(map.get("id"), map.get("categoryAttributeId"), map.get("attributeDefinitionId")));

        Assertions.assertTrue(actual.equals(expected));
    }

    private Map<String, String> createCategoryAttribute(String category_slug, String attributeDefinitionId) throws Exception {
        String query = """
            INSERT INTO attributedefinition (id, name, slug, type, is_global) VALUES (
                '12345',
                'test2',
                'test2',
                'STRING',
                false
            );
            """;

        jdbcTemplate.update(query);
        
        MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/categories/" + category_slug + "/attributes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "attribute_definition_id": "%s",
                            "is_required": false,
                            "is_filterable": true,
                            "is_sortable": true
                        }
                    """.formatted("12345"))
                .with(jwt().jwt(j -> j
                    .claim("realm_access", Map.of("roles", List.of("update_category")))
                    .subject("random-user")
                    ))
            )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        
        Map<String, String> map = new HashMap<>();
        map.put("categoryAttributeId2", com.jayway.jsonpath.JsonPath.read(responseJson, "$.payload.id"));
        map.put("attributeDefinitionId2", "12345");

        return map;
    }

    @Test
    public void should_createANewCategoryAttributeAndShowItInOutboxTable() throws Exception {
        Map<String, String> map = createCategory();

        Map<String, String> map2 = createCategoryAttribute(map.get("slug"), map.get("attributeDefinitionId"));
        
        Integer categoryAttributeCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM categoryattribute", Integer.class);
       
        Assertions.assertTrue(categoryAttributeCount == 2);

        Integer categoryCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM category", Integer.class);
       
        Assertions.assertTrue(categoryCount == 1);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);
      
        Assertions.assertTrue(outboxCount == 6);

        Map<String, Object> outboxEntry = jdbcTemplate.queryForMap(
            "SELECT * FROM outbox WHERE id = 6");

        Assertions.assertTrue(outboxEntry.get("aggregate_type").equals("category"));
    
        Assertions.assertTrue(outboxEntry.get("type").equals("CATEGORY_UPDATED"));
        
        JsonNode actual = mapper.readTree(outboxEntry.get("payload").toString());
        JsonNode expected = mapper.readTree("""
            {
                "id": "%s",
                "name": "test",
                "slug": "test",
                "parent_id": null,
                "attributes": [
                    {
                        "id": "%s", 
                        "attribute": {
                            "id": "%s", 
                            "name": "test2", 
                            "slug": "test2", 
                            "type": "STRING", 
                            "is_global": false
                        }, 
                        "is_required": false, 
                        "is_sortable": true, 
                        "is_filterable": true
                    },{
                        "id": "%s", 
                        "attribute": {
                            "id": "%s", 
                            "name": "test", 
                            "slug": "test", 
                            "type": "STRING", 
                            "is_global": false
                        }, 
                        "is_required": true, 
                        "is_sortable": true, 
                        "is_filterable": true
                    }
                ]
            }
            """.formatted(
                    map.get("id"), 
                    map2.get("categoryAttributeId2"),
                    map2.get("attributeDefinitionId2"),
                    map.get("categoryAttributeId"), 
                    map.get("attributeDefinitionId")
                )
            );

        String actualJson = mapper.writeValueAsString(actual);
        String expectedJson = expected.toString();

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    @Test
    public void should_removeACategoryAttributeAndShowItInOutboxTable() throws Exception {
        Map<String, String> map = createCategory();

        Integer categoryCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM category", Integer.class);
       
        Assertions.assertTrue(categoryCount == 1);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);
      
        Assertions.assertTrue(outboxCount == 2);

        Map<String, Object> outboxEntry = jdbcTemplate.queryForMap(
            "SELECT * FROM outbox WHERE id = 2");

        Assertions.assertTrue(outboxEntry.get("aggregate_type").equals("category"));
    
        Assertions.assertTrue(outboxEntry.get("type").equals("CATEGORY_UPDATED"));

        JsonNode actual = mapper.readTree(outboxEntry.get("payload").toString());

        JsonNode expected = mapper.readTree("""
            {
               "id":"%s",
               "name":"test",
               "slug":"test",
               "parent_id":null,
               "attributes":[
                  {
                     "id":"%s",
                     "attribute":{
                        "id":"%s",
                        "name":"test",
                        "slug":"test",
                        "type":"STRING",
                        "is_global":false
                     },
                     "is_required":true,
                     "is_sortable":true,
                     "is_filterable":true
                  }
               ]
            }
            """.formatted(
                    map.get("id"), 
                    map.get("categoryAttributeId"), 
                    map.get("attributeDefinitionId")
                )
            );
        Assertions.assertTrue(actual.equals(expected));

        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/api/v1/categories/%s/attributes/%s".formatted(map.get("slug"), map.get("categoryAttributeId")))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().jwt(j -> j
                    .claim("realm_access", Map.of("roles", List.of("update_category")))
                    .subject("random-user")
                    ))
            )
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);
      
        Assertions.assertTrue(outboxCount == 4);

        Integer categoryAttributeCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM categoryattribute", Integer.class);
       
        Assertions.assertTrue(categoryAttributeCount == 0);

        outboxEntry = jdbcTemplate.queryForMap(
            "SELECT * FROM outbox WHERE id = 4");

        Assertions.assertTrue(outboxEntry.get("aggregate_type").equals("category"));
    
        Assertions.assertTrue(outboxEntry.get("type").equals("CATEGORY_UPDATED"));

        actual = mapper.readTree(outboxEntry.get("payload").toString());

        expected = mapper.readTree("""
            {
               "id":"%s",
               "name":"test",
               "slug":"test",
               "parent_id":null,
               "attributes":[]
            }
            """.formatted(
                    map.get("id") 
                )
            );
        Assertions.assertTrue(actual.equals(expected));
    }

    @Test
    public void should_deleteACategoryAndShowItInOutboxTable() throws Exception {
        Map<String, String> map = createCategory();

        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/api/v1/categories/%s".formatted(map.get("slug")))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().jwt(j -> j
                    .claim("realm_access", Map.of("roles", List.of("delete_category")))
                    .subject("random-user")
                    ))
            )
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        Integer categoryAttributeCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM categoryattribute", Integer.class);
       
        Assertions.assertTrue(categoryAttributeCount == 0);

        Integer categoryCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM category", Integer.class);
       
        Assertions.assertTrue(categoryCount == 0);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);
      
        Assertions.assertTrue(outboxCount == 4);

        Map<String, Object> outboxEntry = jdbcTemplate.queryForMap(
            "SELECT * FROM outbox WHERE id = 4");

        Assertions.assertTrue(outboxEntry.get("aggregate_type").equals("category"));
    
        Assertions.assertTrue(outboxEntry.get("type").equals("CATEGORY_DELETED"));

        JsonNode actual = mapper.readTree(outboxEntry.get("payload").toString());

        JsonNode expected = mapper.readTree("""
            {"id":"%s","deleted":true}
            """.formatted(
                    map.get("id")
                )
            );
        Assertions.assertTrue(actual.equals(expected));
    }
}
