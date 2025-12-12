package microservice.cloud.inventory.shared.infrastructure.adapters.in;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;

        Jwt jwt = jwtAuth.getToken();

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        List<String> roles = (List<String>) realmAccess.get("roles");

        return new Me(new Id(jwt.getSubject()), 
            roles.stream().map(r -> new Permission(r)).toList()
        );
    }
}
