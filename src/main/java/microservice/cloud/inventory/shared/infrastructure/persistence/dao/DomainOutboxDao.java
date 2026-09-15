package microservice.cloud.inventory.shared.infrastructure.persistence.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.shared.application.ports.out.DomainOutboxDaoInterface;

@Component
@RequiredArgsConstructor
public class DomainOutboxDao implements DomainOutboxDaoInterface {

    private final JdbcTemplate jdbcTemplate;

    public void save(
        String topic,
        String payload
    ) {
        String sql = "INSERT INTO domain_outbox (topic, payload, created_at) VALUES (?, ?::jsonb, NOW())";
        jdbcTemplate.update(sql, topic, payload);
    }
}
