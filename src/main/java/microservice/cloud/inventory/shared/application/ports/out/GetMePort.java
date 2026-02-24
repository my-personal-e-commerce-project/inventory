package microservice.cloud.inventory.shared.application.ports.out;

import microservice.cloud.inventory.shared.domain.value_objects.Me;

public interface GetMePort {

    public Me execute();
}
