package microservice.cloud.inventory.category.infrastructure.adapter;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.application.ports.out.CreateProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@RequiredArgsConstructor
@Component
public class CreateProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronouslyImpl implements CreateProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously {

    private final ProductRepository productRepository;

    @Async
    @Override
    public void execute(Id categoryId, List<CategoryAttribute> data) {
        if(data.size() == 0)
            return;

        List<Id> ids = data.stream().map(e -> {
            return e.id();
        }).toList(); 
      
        productRepository.massCreateProductAttributeValuesByNewRequiredCategoryAttributes(
            categoryId, 
            ids
        );
    }
}
