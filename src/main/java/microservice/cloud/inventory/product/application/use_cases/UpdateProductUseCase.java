package microservice.cloud.inventory.product.application.use_cases;

import java.util.ArrayList;
import java.util.List;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.product.application.ports.in.UpdateProductUseCasePort;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class UpdateProductUseCase implements UpdateProductUseCasePort {

    private ProductRepository productRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;
    private EventPublishedPort eventPublishedPort;
    private GetMePort getMePort;

    public UpdateProductUseCase(
        ProductRepository productRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        this.productRepository = productRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.eventPublishedPort = eventPublishedPort;
        this.getMePort = getMePort;
    }

    @Override
    public void execute(
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
      
        List<AttributeDefinition> attrs = 
            attributeDefinitionRepository
            .getByCategoryAttributeIds(
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

        eventPublishedPort.publish(p.events());
    }
}
