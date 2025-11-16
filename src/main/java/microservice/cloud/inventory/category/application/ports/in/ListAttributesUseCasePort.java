package microservice.cloud.inventory.category.application.ports.in;

import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public interface ListAttributesUseCasePort {
    
    public Pagination<CategoryAttribute> execute(int page, int limit);
}
