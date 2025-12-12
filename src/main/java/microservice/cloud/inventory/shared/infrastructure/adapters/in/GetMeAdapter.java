package microservice.cloud.inventory.shared.infrastructure.adapters.in;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;

@Component
public class GetMeAdapter implements GetMePort {

    @Override
    public Me execute() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // TODO: conseguir los datos del usuario
       
        String token = authentication.toString();
        System.out.println(token);

        return new Me(new Id("user id"), 
            new ArrayList<Permission>(
                List.of(
                    Permission.createCategory(),
                    Permission.updateCategory(),
                    Permission.deleteCategory(),
                    Permission.createProduct(),
                    Permission.updateProduct(),
                    Permission.deleteProduct()
                )
            )
        );
    }
}
