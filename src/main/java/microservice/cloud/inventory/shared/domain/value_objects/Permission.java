package microservice.cloud.inventory.shared.domain.value_objects;

public record Permission (
    String value
) {
    public Permission(String value) {
        if(value == null)
            throw new IllegalArgumentException("The permission cannot be null");

        if(value.isBlank())
            throw new IllegalArgumentException("The permission cannot be empty");
    
        this.value = value;
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
        return new Permission("delete_product");
    }
 
    public static Permission createAttributeDefinition() {
        return new Permission("create_attribute_definition");
    }

    public static Permission updateAttributeDefinition() {
        return new Permission("update_attribute_definition");
    }

    public static Permission deleteAttributeDefinition() {
        return new Permission("delete_attribute_definition");
    }

    public static Permission createCoupon() {
        return new Permission("create_coupon");
    }

    public static Permission updateCoupon() {
        return new Permission("update_coupon");
    }

    public static Permission deleteCoupon() {
        return new Permission("delete_coupon");
    }
}
