package microservice.cloud.inventory.shared.domain.value_objects;

public class Permission {

    private String value;

    public Permission(String value) {

        if(value == null)
            throw new RuntimeException("The permission cannot be null");

        if(value == "")
            throw new RuntimeException("The permission cannot be empty");
    
        this.value = value;
    }

    public String value() {
        return value;
    }

    public boolean equals(Permission permission) {
        return this.value.equals(permission.value());
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
