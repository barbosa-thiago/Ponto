package com.ilia.ponto.service;

import com.ilia.ponto.keycloak.KeycloakService;
import com.ilia.ponto.mappers.UsuarioMapper;
import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.repository.UsuarioRepository;
import com.ilia.ponto.requestbody.UsuarioPostBody;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UsuarioService {
  private final UsuarioRepository usuarioRepository;
  private final KeycloakService keycloakService;

  public Usuario save(UsuarioPostBody usuarioPostBody) {

    uniqueUsernameOrThrowBadRequest(usuarioPostBody);

    UUID keycloakUserId = keycloakService.createKeycloakUser(usuarioPostBody);

    Usuario usuario = UsuarioMapper.INSTANCE.mapUsuario(usuarioPostBody);
    usuario.setId(keycloakUserId);

    return usuarioRepository.save(usuario);
  }

  private void uniqueUsernameOrThrowBadRequest(UsuarioPostBody usuarioPostBody) {
    if(usuarioRepository.findByUsername(usuarioPostBody.getUsername()).isPresent())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username precisa ser único");
    }
}
