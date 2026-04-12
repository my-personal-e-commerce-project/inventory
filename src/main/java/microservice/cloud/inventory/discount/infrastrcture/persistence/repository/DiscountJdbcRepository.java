package microservice.cloud.inventory.discount.infrastrcture.persistence.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import microservice.cloud.inventory.discount.infrastrcture.persistence.model.DiscountEntity;

@Repository
public interface DiscountJdbcRepository extends PagingAndSortingRepository<DiscountEntity, String> {

    List<DiscountEntity> findByIdIn(Set<String> ids);
    long countByIdIn(Set<String> ids);
}
