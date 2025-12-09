package microservice.cloud.inventory.shared.application.ports.in;

import microservice.cloud.inventory.shared.domain.value_objects.Me;

public interface GetMePort {

    public Me execute();
}
