package microservice.cloud.inventory.shared.domain.value_objects;

import org.junit.jupiter.api.Test;
import java.util.Set;
import microservice.cloud.inventory.shared.domain.exception.UnauthorizedException;
import static org.junit.jupiter.api.Assertions.*;

class MeTest {

    @Test
    void shouldPassWhenUserHasPermission() {
        Id userId = Id.generate();
        Permission permission = Permission.createProduct();
        Me me = new Me(userId, Set.of(permission));

        assertDoesNotThrow(() -> me.IHavePermission(permission));
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenUserDoesNotHavePermission() {
        Id userId = Id.generate();
        Permission requiredPermission = Permission.createProduct();
        Permission userPermission = Permission.deleteProduct();
        Me me = new Me(userId, Set.of(userPermission));

        UnauthorizedException exception = assertThrows(
            UnauthorizedException.class, 
            () -> me.IHavePermission(requiredPermission)
        );
        assertEquals(
            "Invalid permissions. The " + requiredPermission.value() + " is required.", 
            exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenPermissionToCheckIsNull() {
        Id userId = Id.generate();
        Me me = new Me(userId, Set.of(Permission.createProduct()));

        RuntimeException exception = assertThrows(
            RuntimeException.class, 
            () -> me.IHavePermission(null)
        );
        assertEquals("Permission cannot be null", exception.getMessage());
    }
}
