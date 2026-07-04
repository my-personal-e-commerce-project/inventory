package microservice.cloud.inventory.shared.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

public class Id {
    private final String value;

    public String value() {
        return value;
    }

    public static Id fromString(String value) {
        return new Id(value);
    }

    public static Id generate() {
        return new Id(UUID.randomUUID().toString());
    }

    private Id(String value) {

        if(value == null)
            throw new RuntimeException("Id cannot be null");

        this.value = value;
    }

    public boolean equals(Id id) {
        if (id == null) return false;
        return id.value().equals(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Id id1 = (Id) o;
        return Objects.equals(value, id1.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
