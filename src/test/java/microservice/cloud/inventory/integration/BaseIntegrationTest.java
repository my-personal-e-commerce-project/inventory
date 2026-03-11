package microservice.cloud.inventory.integration;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class BaseIntegrationTest {

    static Network network = Network.newNetwork();

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:15-alpine")
                .withNetwork(network)
                .withNetworkAliases("postgres")
                .withDatabaseName("testdb")
                .withUsername("user")
                .withPassword("pass");
    }
}
