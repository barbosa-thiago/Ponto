package com.ilia.ponto.service;

import static com.ilia.ponto.util.ObjectCreator.createUsuarioPostBody;
import static com.ilia.ponto.util.ObjectCreator.createValidUsuario;

import com.ilia.ponto.keycloak.KeycloakService;
import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.projection.UsuarioUsernameId;
import com.ilia.ponto.repository.UsuarioRepository;
import com.ilia.ponto.requestbody.UsuarioPostBody;
import com.ilia.ponto.util.ObjectCreator;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    BDDMockito.when(usuarioRepository.save(ArgumentMatchers.any(Usuario.class)))
        .thenReturn(createValidUsuario());

  }

  @Test
  @Order(0)
  void save_PersistsUsuario_WhenSuccessful() {
    Usuario usuario = usuarioService.save(createUsuarioPostBody());

    Assertions.assertThat(usuario.getId()).isNotNull();
  }

  @Test
  void save_ThrowsException_WhenNotSuccessful() {
    BDDMockito.when(usuarioRepository.findByUsername(ArgumentMatchers.anyString()))
        .thenReturn(Optional.of(createValidUsuario()));

    UsuarioPostBody usuarioPostBody = createUsuarioPostBody();
    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> usuarioService.save(usuarioPostBody));
  }

  @Test
  void findAll_ResturnsUsuarioList_WhenSuccessful() {

    Usuario validUsuario = createValidUsuario();

    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
    UsuarioUsernameId projection = factory.createProjection(UsuarioUsernameId.class);
    projection.setId(validUsuario.getId());
    projection.setUsername(validUsuario.getUsername());
    PageImpl<UsuarioUsernameId> usuarioPage =
        new PageImpl<>(List.of(projection));

    BDDMockito.when(usuarioService.findAll(ArgumentMatchers.any(PageRequest.class)))
        .thenReturn(usuarioPage);

    Page<UsuarioUsernameId> all = usuarioService.findAll(PageRequest.of(1, 1));
    Assertions.assertThat(all.toList().get(0).getId())
        .isNotNull();
  }
}