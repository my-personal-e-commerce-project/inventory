package microservice.cloud.inventory.discount.infrastrcture.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.discount.application.dtos.DiscountReadDTO;
import microservice.cloud.inventory.discount.application.ports.out.DiscountReadRepository;
import microservice.cloud.inventory.discount.infrastrcture.persistence.model.DiscountEntity;
import microservice.cloud.inventory.shared.application.dto.Pagination;

@RequiredArgsConstructor
@Repository
public class DiscountReadRepositoryJdbcAdapter implements DiscountReadRepository {

    private final DiscountJdbcRepository discountJdbcRepository;

    @Override
    public Pagination<DiscountReadDTO> listDiscounts(int page, int size) {
        Pageable pageable = PageRequest.of(0, 20);

        Page<DiscountEntity> discounts = discountJdbcRepository.findAll(pageable);

        return new Pagination<DiscountReadDTO>(
            discounts.getContent()
                .stream()
                .map(this::toMap)
                .toList(),
            discounts.getTotalPages(), 
            page
        );
    }

    private DiscountReadDTO toMap(DiscountEntity entity) {
        return new DiscountReadDTO(
            entity.getId(),
            entity.getName(),
            entity.getDiscountType().toString(),
            entity.getPercentageValue(),
            entity.getDecrementValue(),
            entity.getAllowedCategories()
                .stream()
                .map(c -> c.categoryId())
                .toList(),
            entity.isGlobalCategories(),
            entity.getMinPrice(),
            entity.getMaxPrice(),
            entity.getMinStock(),
            entity.getMaxStock(),
            entity.isAutoApply(),
            entity.getExpiredAt() 
        );
    }
}
