package microservice.cloud.inventory.shared.domain.value_objects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    void shouldCreatePermissionWithValue() {
        Permission permission = new Permission("custom_permission");
        assertEquals("custom_permission", permission.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> new Permission(null)
        );
        assertEquals("The permission cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> new Permission("   ")
        );
        assertEquals("The permission cannot be empty", exception.getMessage());
    }

    @Test
    void shouldCreateStaticPermissionsCorrectly() {
        assertEquals("create_product", Permission.createProduct().value());
        assertEquals("update_product", Permission.updateProduct().value());
        assertEquals("delete_product", Permission.deleteProduct().value());
        
        assertEquals("create_category", Permission.createCategory().value());
        assertEquals("update_category", Permission.updateCategory().value());
        assertEquals("delete_category", Permission.deleteCategory().value());
        
        assertEquals("create_attribute_definition", Permission.createAttributeDefinition().value());
        assertEquals("update_attribute_definition", Permission.updateAttributeDefinition().value());
        assertEquals("delete_attribute_definition", Permission.deleteAttributeDefinition().value());
    }
}
