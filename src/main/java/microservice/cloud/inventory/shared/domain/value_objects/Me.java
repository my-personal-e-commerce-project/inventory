package microservice.cloud.inventory.shared.domain.value_objects;

import java.util.Set;

import microservice.cloud.inventory.shared.domain.exception.UnauthorizedException;

public record Me(
    Id id,
    Set<Permission> permissions
) {
    public void IHavePermission(Permission permission) {
        boolean result = false;

        for (Permission p : permissions) {
            if(p.equals(permission))
                result = true;
        }

        if(!result)
            throw new UnauthorizedException("Invalid permissions. The " + permission.value() + " is required.");
    }
}
