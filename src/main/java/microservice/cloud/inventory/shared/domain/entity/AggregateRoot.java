package microservice.cloud.inventory.shared.domain.entity;

import java.util.ArrayList;
import java.util.List;

import microservice.cloud.inventory.shared.domain.event.DomainEvent;

public abstract class AggregateRoot {

    private List<DomainEvent> events = new ArrayList<>();

    protected void dispatch(DomainEvent event) {
        events.add(event);
    }

    public List<DomainEvent> events() {

        return events;
    }
    
}
