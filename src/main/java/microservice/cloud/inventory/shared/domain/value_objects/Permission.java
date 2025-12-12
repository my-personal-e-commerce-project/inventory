package microservice.cloud.inventory.shared.domain.value_objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Permission {

    private final List<String> permissions = new ArrayList<>(
        Arrays.asList(
            "create_product",
            "update_product",
            "delete_product",
            "create_category",
            "update_category",
            "delete_category"
        )
    );

    private String value;

    public Permission(String value) {

        if(value == null)
            throw new RuntimeException("The value cannot be null");

        if(value == "")
            throw new RuntimeException("The value cannot be empty");
      
        if(!permissions.contains(value))
            throw new RuntimeException("The value is invalid");

        this.value = value;
    }

    public String value() {
        return value;
    }

    public boolean equals(Permission permission) {
        return this.value == permission.value();
    }

    public static Permission createCategory() {
        return new Permission("create_category");
    }

    public static Permission updateCategory() {
        return new Permission("update_category");
    }

    public static Permission deleteCategory() {
        return new Permission("delete_category");
    }

    public static Permission createProduct() {
        return new Permission("create_product");
    }

    public static Permission updateProduct() {
        return new Permission("update_product");
    }

    public static Permission deleteProduct() {
        return new Permission("update_product");
    }
    
}
