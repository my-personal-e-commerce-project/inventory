package microservice.cloud.inventory.shared.domain.value_objects;

import java.util.ArrayList;
import java.util.List;

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
        boolean result = permissions.contains(permission);

        if(!result)
            throw new RuntimeException("Invalid permissions. The product_create permission is required. " + permission.value());
    }
}
