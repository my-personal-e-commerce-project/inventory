package microservice.cloud.inventory.category.application.ports.in;

import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;

public interface CreateAttributeUseCasePort {

    public void execute(CategoryAttribute categoryAttribute);
}
