package microservice.cloud.inventory.category.domain.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class Category {
    
    private Id id;
    private String name;
    private Slug slug;
    private Id parent_id;
}
