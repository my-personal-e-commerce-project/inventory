package microservice.cloud.inventory.product.domain.entity;

import java.util.Set;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface ProductRepository {

    public void save(Product product);
    public void update(Product product);
    public void delete(Product product);
    public Product findBySlug(Slug slug);
    public ProductAttributeValue findProductAttributeValueById(Id id);
    public void deleteOrphanAttributeValues(Id categoryId, Id attributeDefinitionId);
    public void massCreateDefaultProductAttributeValues(AttributeDefinition attributeDefinition);
    public void updateTheValueTypeOfProductAttributesByAttributeDefinition(Id attributeDefinitionId, DataType type);
    public void getProductIdsWithTheseCategoriesAndPriceRange(Price minPrice, Price maxPrice, Set<String> categoryIds);
}
