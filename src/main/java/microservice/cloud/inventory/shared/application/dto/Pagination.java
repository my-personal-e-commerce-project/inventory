package microservice.cloud.inventory.shared.application.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Pagination<T> {

    private List<T> results;
    private Integer last_page;
    private Integer current_page;
}
