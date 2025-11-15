package microservice.cloud.inventory.shared.domain.value_objects;

import java.util.UUID;

public class Id {

    private final String value;

    public String value() {
        return value;
    }

    public Id generate() {
        return new Id(UUID.randomUUID().toString());
    }

    public Id(String value) {
        this.value = value;
    }
}
