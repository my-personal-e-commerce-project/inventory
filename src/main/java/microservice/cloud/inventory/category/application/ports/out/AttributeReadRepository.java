package microservice.cloud.inventory.category.application.ports.out;

import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public interface AttributeReadRepository {

    public Pagination<CategoryAttribute> findAll(int page, int limit);
}
