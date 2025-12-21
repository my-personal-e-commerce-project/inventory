package microservice.cloud.inventory.product.application.use_cases;

import java.util.ArrayList;
import java.util.List;

import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.application.ports.in.UpdateProductUseCasePort;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class UpdateProductUseCase implements UpdateProductUseCasePort {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private GetMePort getMePort;

    public UpdateProductUseCase(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        GetMePort getMePort
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.getMePort = getMePort;
    }

    @Override
    public Product execute(
        Id id,
        String title, 
        Slug slug, 
        String description,
        List<String> categories,
        Price price,
        Quantity stock,
        List<String> images,
        List<ProductAttributeValue> attributes,
        List<String> tags
    ) {
        Product p = productRepository.findById(new Id(id.value()));
      
        List<CategoryAttribute> attrs = 
           categoryRepository 
            .getCategoryAttributesByCategoryIds(
                categories
            );

        p.update(
            getMePort.execute(),
            title, 
            slug, 
            description, 
            categories, 
            price, 
            stock, 
            images, 
            attributes, 
            tags
        );
        
        p.validAttributes(new ArrayList<>(attrs));

        productRepository.update(p);

        return p;
    }
}
