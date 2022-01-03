package com.ilia.ponto.keycloak;

import com.ilia.ponto.requestbody.UsuarioPostBody;
import java.util.UUID;
import javax.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class KeycloakService {

  private final KeycloakClient keycloakClient;

  public UUID createKeycloakUser(UsuarioPostBody usuarioPostBody) {

    RealmResource keycloakRealmResource = keycloakClient.getKeycloakInstance();

    UserRepresentation user = keycloakClient.defineKeycloakUserToBeCreated(usuarioPostBody);

    Response response = keycloakRealmResource.users().create(user);
    UUID createdId = UUID.fromString(CreatedResponseUtil.getCreatedId(response));
    log.debug("User created with id {}", createdId);
    return createdId;
  }


  public String getUsuarioUsername() {
    KeycloakPrincipal<KeycloakSecurityContext> principal =
        (KeycloakPrincipal<KeycloakSecurityContext>) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

    return principal.getKeycloakSecurityContext().getToken().getPreferredUsername();
  }

  public void deleteUser(UUID id) {
    RealmResource keycloakRealmResource = keycloakClient.getKeycloakInstance();
    keycloakRealmResource.users().delete(id.toString());
  }
}
