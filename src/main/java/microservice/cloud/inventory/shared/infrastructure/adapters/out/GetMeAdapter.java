package microservice.cloud.inventory.shared.infrastructure.adapters.out;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
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
        Collection<String> roles = null;

        if(realmAccess != null)
           roles = (Collection<String>) realmAccess.get("roles");

        if (roles == null) roles = Collections.emptyList();

        Set<Permission> permissions = roles.stream()
            .map(Permission::new)
            .collect(Collectors.toSet());

        return new Me(
            Id.fromString(jwt.getSubject()),
            permissions
        );
    }
}
