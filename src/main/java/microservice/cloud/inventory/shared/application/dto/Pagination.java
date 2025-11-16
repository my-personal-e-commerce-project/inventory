package microservice.cloud.inventory.shared.application.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class Pagination<T> {

    private List<T> results;
    private Integer page;
    private Integer last_page;
    private Integer current_page;
}
