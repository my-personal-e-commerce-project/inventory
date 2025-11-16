package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.ListAttributesUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.AttributeReadRepository;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public class ListAttributeUseCase implements ListAttributesUseCasePort {

    private AttributeReadRepository attributeReadRepository;

    public ListAttributeUseCase(
        AttributeReadRepository attributeReadRepository
    ) {
        this.attributeReadRepository = attributeReadRepository;
    }

    public Pagination<CategoryAttribute> execute(int page, int limit) {
        Pagination<CategoryAttribute> lists = attributeReadRepository.findAll(page, limit);
        return lists;
    }
}
