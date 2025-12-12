package microservice.cloud.inventory.shared.domain.value_objects;

import java.util.ArrayList;
import java.util.List;

import microservice.cloud.inventory.shared.domain.exception.UnauthorizedException;

public class Me {

    private List<Permission> permissions = new ArrayList<>();
    private Id id;

    public Me(
        Id id,
        List<Permission> permissions
    ) {
        this.id = id;
        this.permissions = permissions;
    }

    public Id id() {return id;}
    public List<Permission> permissions() {return List.copyOf(permissions);}

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
