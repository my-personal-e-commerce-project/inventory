package microservice.cloud.inventory.category.application.ports.in;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

public interface DeleteAttributeUseCasePort {

    public void execute(Id id);
}
