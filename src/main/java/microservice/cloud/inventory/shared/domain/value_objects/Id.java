package microservice.cloud.inventory.shared.domain.value_objects;

import java.util.UUID;

public class Id {

    private final String value;

    public String value() {
        return value;
    }

    public static Id generate() {
        return new Id(UUID.randomUUID().toString());
    }

    public Id(String value) {

        if(value == null)
            throw new RuntimeException("Id cannot be null");

        this.value = value;
    }

    public boolean equals(Id id) {

        return id.value().equals(this.value);
    }
}
