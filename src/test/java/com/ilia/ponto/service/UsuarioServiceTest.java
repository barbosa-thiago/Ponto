package com.ilia.ponto.service;

import static com.ilia.ponto.util.ObjectCreator.createInvalidUsuarioPostBody;
import static com.ilia.ponto.util.ObjectCreator.createUsuarioPostBody;
import static com.ilia.ponto.util.ObjectCreator.createValidUsuario;
import static org.junit.jupiter.api.Assertions.*;

import com.ilia.ponto.keycloak.KeycloakService;
import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.repository.UsuarioRepository;
import com.ilia.ponto.util.ObjectCreator;
import java.util.Optional;
import java.util.UUID;
import javax.ws.rs.BadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(SpringExtension.class)
class UsuarioServiceTest {

  @InjectMocks
  UsuarioService usuarioService;

  @Mock
  KeycloakService keycloakService;
  @Mock
  UsuarioRepository usuarioRepository;

  @BeforeEach
  void setup() {
    Usuario validUsuario = createValidUsuario();

    BDDMockito.when(keycloakService.createKeycloakUser(ObjectCreator.createUsuarioPostBody()))
        .thenReturn(validUsuario.getId());

    BDDMockito.when(usuarioRepository.save(validUsuario))
        .thenReturn(createValidUsuario());

    BDDMockito.when(usuarioRepository.findByUsername(ArgumentMatchers.anyString()))
        .thenReturn(Optional.empty());
  }

  @Test
  void save_PersistsUsuario_WhenSuccessful() {
    Usuario usuario = usuarioService.save(createUsuarioPostBody());

    Assertions.assertThat(usuario.getId()).isNotNull();
  }

  @Test
  void save_ThrowsException_WhenNotSuccessful() {
    BDDMockito.when(usuarioRepository.findByUsername(ArgumentMatchers.anyString()))
        .thenReturn(Optional.of(createValidUsuario()));

    Assertions.assertThatExceptionOfType(BadRequestException.class)
        .isThrownBy(() -> usuarioService.save(createUsuarioPostBody()));
  }
}