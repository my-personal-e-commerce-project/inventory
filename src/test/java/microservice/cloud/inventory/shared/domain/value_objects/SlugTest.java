package microservice.cloud.inventory.shared.domain.value_objects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SlugTest {

    @Test
    void shouldCreateSlugFromString() {
        Slug slug = Slug.fromString("custom-slug-value");
        assertEquals("custom-slug-value", slug.value());
    }

    @Test
    void shouldGenerateSlugFromTitle() {
        Slug slug = Slug.create("Hello World! This is a test.");
        assertEquals("hello-world-this-is-a-test", slug.value());
    }

    @Test
    void shouldThrowExceptionWhenSlugIsNull() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> Slug.fromString(null));
        assertEquals("The slug cannot be null", exception.getMessage());
    }

    @Test
    void shouldCompareEqualityCorrectly() {
        Slug slug1 = Slug.fromString("some-slug");
        Slug slug2 = Slug.fromString("some-slug");
        Slug slug3 = Slug.fromString("other-slug");

        assertTrue(slug1.equals(slug2));
        assertFalse(slug1.equals(slug3));
    }
}
