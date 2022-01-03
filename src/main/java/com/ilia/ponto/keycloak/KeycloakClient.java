package com.ilia.ponto.keycloak;

import com.ilia.ponto.requestbody.UsuarioPostBody;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KeycloakClient {

  private final Environment env;

  protected RealmResource getKeycloakInstance() {
    return KeycloakBuilder.builder()
        .serverUrl(env.getProperty("keycloak.auth-server-url"))
        .realm(env.getProperty("keycloak.realm"))
        .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
        .clientId(env.getProperty("keycloak.resource"))
        .clientSecret(env.getProperty("keycloak.credentials.secret"))
        .build().realm(env.getProperty("keycloak.realm"));
  }

  protected UserRepresentation defineKeycloakUserToBeCreated(UsuarioPostBody usuario) {
    UserRepresentation user = new UserRepresentation();
    user.setFirstName(usuario.getName());
    user.setUsername(usuario.getUsername());
    user.setEnabled(true);
    user.setAttributes(Collections.singletonMap("origin", List.of("demo")));
    user.setRealmRoles(List.of("view"));
    setKeycloakPassword(user, usuario.getPassword());
    return user;
  }

  private void setKeycloakPassword(UserRepresentation user, String password) {
    CredentialRepresentation passwordCredential = new CredentialRepresentation();
    passwordCredential.setTemporary(false);
    passwordCredential.setType(CredentialRepresentation.PASSWORD);
    passwordCredential.setValue(password);
    user.setCredentials(List.of(passwordCredential));
  }
}
