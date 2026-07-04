package microservice.cloud.inventory.shared.domain.value_objects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IdTest {

    @Test
    void shouldCreateIdFromString() {
        String uuidStr = "d3b07384-d113-49cd-a5d6-8ee4ef8f3c7a";
        Id id = Id.fromString(uuidStr);
        assertEquals(uuidStr, id.value());
    }

    @Test
    void shouldGenerateIdAutomatically() {
        Id id = Id.generate();
        assertNotNull(id.value());
        assertDoesNotThrow(() -> java.util.UUID.fromString(id.value()));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> Id.fromString(null));
        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void shouldCompareEqualityCorrectly() {
        Id id1 = Id.fromString("id-123");
        Id id2 = Id.fromString("id-123");
        Id id3 = Id.fromString("id-456");

        assertTrue(id1.equals(id2));
        assertTrue(id1.equals((Object) id2));
        assertFalse(id1.equals(id3));
        assertFalse(id1.equals((Object) id3));
        assertFalse(id1.equals(null));
        assertFalse(id1.equals("different-type"));
        
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }
}
