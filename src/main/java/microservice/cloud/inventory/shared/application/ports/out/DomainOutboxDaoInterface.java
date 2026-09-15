package microservice.cloud.inventory.shared.application.ports.out;

public interface DomainOutboxDaoInterface {

    void save(
        String topic,
        String payload
    );
}
