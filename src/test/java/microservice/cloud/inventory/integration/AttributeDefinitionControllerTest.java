package microservice.cloud.inventory.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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
public class AttributeDefinitionControllerTest {

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

    private Map<String, String> helper_createAttributeDefinition(boolean is_global) throws Exception {
        MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/attribute_definitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "nose2",
                          "slug": "nose2",
                          "type": "STRING",
                          "is_global": %s
                        }
                    """.formatted(is_global))
                .with(jwt().jwt(j -> j.claim("realm_access", Map.of("roles", List.of("create_attribute_definition")))
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
    
        return resultMap;
    }

    @Test
    public void should_createANewAttributeDefinitionAndShowItInOutboxTableIfItIsGlobal() throws Exception {
        Map<String, String> map = helper_createAttributeDefinition(true);

        Integer attributeDefinitionCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM attributedefinition", Integer.class);
       
        Assertions.assertTrue(attributeDefinitionCount == 1);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);

        Assertions.assertTrue(outboxCount == 1);

        Map<String, Object> outboxEntry = jdbcTemplate.queryForMap(
            "SELECT * FROM outbox WHERE id = 1;"
        );

        Assertions.assertTrue(outboxEntry.get("aggregate_type").equals("attribute_definition"));
    
        Assertions.assertTrue(outboxEntry.get("type").equals("ATTRIBUTE_CREATED"));

        JsonNode actual = mapper.readTree(outboxEntry.get("payload").toString());

        JsonNode expected = mapper.readTree("""
            {
                "id": "%s", 
                "name": "nose2", 
                "slug": "nose2", 
                "type": "STRING",
                "is_global": true
            } 
            """.formatted(map.get("id")));

        Assertions.assertTrue(actual.equals(expected));
    }
   
    @Test
    public void should_createANewAttributeDefinitionAndDoNotShowItInOutboxTableIfItIsNotGlobal() throws Exception {
        helper_createAttributeDefinition(false);

        Integer attributeDefinitionCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM attributedefinition", Integer.class);
       
        Assertions.assertTrue(attributeDefinitionCount == 1);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);

        Assertions.assertTrue(outboxCount == 0);
    }

    private Map<String, String> helper_updateAttributeDefinition(String slug) throws Exception {
        MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders
                .put("/api/v1/attribute_definitions/%s".formatted(slug))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "test22",
                          "slug": "test22",
                          "type": "STRING",
                          "is_global": true
                        }
                    """)
                .with(jwt().jwt(j -> j.claim("realm_access", Map.of("roles", List.of("update_attribute_definition")))
                                 .subject("random-user")))
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
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
    
        return resultMap;
    }

    public void should_updateANewAttributeDefinitionAndShowItInOutboxTableIfItIsGlobal() throws Exception {
        Map<String, String> map = helper_createAttributeDefinition(true);
        helper_updateAttributeDefinition(map.get("slug"));

        Integer attributeDefinitionCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM attributedefinition", Integer.class);
       
        Assertions.assertTrue(attributeDefinitionCount == 1);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);

        Assertions.assertTrue(outboxCount == 2);

        Map<String, Object> outboxEntry = jdbcTemplate.queryForMap(
            "SELECT * FROM outbox WHERE id = 2;"
        );

        Assertions.assertTrue(outboxEntry.get("aggregate_type").equals("attribute_definition"));
    
        Assertions.assertTrue(outboxEntry.get("type").equals("ATTRIBUTE_UPDATED"));

        JsonNode actual = mapper.readTree(outboxEntry.get("payload").toString());

        JsonNode expected = mapper.readTree("""
            {
                "id": "%s", 
                "name": "test22", 
                "slug": "test22", 
                "type": "STRING",
                "is_global": false
            } 
            """.formatted(map.get("id")));

        Assertions.assertTrue(actual.equals(expected));
    }

    public void should_updateANewAttributeDefinitionAndDoNotShowItInOutboxTableIfItIsNotGlobal() throws Exception {
        Map<String, String> map = helper_createAttributeDefinition(false);
        helper_updateAttributeDefinition(map.get("slug"));

        Integer attributeDefinitionCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM attributedefinition", Integer.class);
       
        Assertions.assertTrue(attributeDefinitionCount == 1);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);

        Assertions.assertTrue(outboxCount == 0);
    }
   
    @Test
    public void should_deleteANewAttributeDefinitionAndShowItInOutboxTableIfItIsGlobal() throws Exception {
        Map<String, String> map = helper_createAttributeDefinition(true);

        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/api/v1/attribute_definitions/%s".formatted(map.get("slug")))
                .with(jwt().jwt(j -> j.claim("realm_access", Map.of("roles", List.of("delete_attribute_definition")))
                                 .subject("random-user")))
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(System.out::println);

        Integer attributeDefinitionCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM attributedefinition", Integer.class);
       
        Assertions.assertTrue(attributeDefinitionCount == 0);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);

        Assertions.assertTrue(outboxCount == 2);

        Map<String, Object> outboxEntry = jdbcTemplate.queryForMap(
            "SELECT * FROM outbox WHERE id = 2;"
        );

        Assertions.assertTrue(outboxEntry.get("aggregate_type").equals("attribute_definition"));
    
        Assertions.assertTrue(outboxEntry.get("type").equals("ATTRIBUTE_DELETED"));

        JsonNode actual = mapper.readTree(outboxEntry.get("payload").toString());

        System.out.println(actual);
        JsonNode expected = mapper.readTree("""
            {
                "id": "%s", 
                "deleted": true
            } 
            """.formatted(map.get("id")));

        Assertions.assertTrue(actual.equals(expected));
    }

    @Test
    public void should_deleteANewAttributeDefinitionAndDoesNotShowItInOutboxTableIfItIsNotGlobal() throws Exception {
        Map<String, String> map = helper_createAttributeDefinition(false);

        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/api/v1/attribute_definitions/%s".formatted(map.get("slug")))
                .with(jwt().jwt(j -> j.claim("realm_access", Map.of("roles", List.of("delete_attribute_definition")))
                                 .subject("random-user")))
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        Integer attributeDefinitionCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM attributedefinition", Integer.class);
       
        Assertions.assertTrue(attributeDefinitionCount == 0);

        Integer outboxCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox", Integer.class);

        Assertions.assertTrue(outboxCount == 0);
    }

    @Test
    public void should_listAllAttributeDefinitions() throws Exception{
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

        String query2 = """
            INSERT INTO attributedefinition (id, name, slug, type, is_global) VALUES (
                '12345',
                'test2',
                'test2',
                'STRING',
                true
            );
            """;

        jdbcTemplate.update(query2);

        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/api/v1/attribute_definitions")
                .with(jwt().jwt(j -> j.subject("random-user")))
            )
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
