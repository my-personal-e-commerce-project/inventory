package microservice.cloud.inventory.category.application.ports.out;

import java.util.List;


import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface CreateProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously {

    public void execute(Id categoryId, List<CategoryAttribute> data);
}
