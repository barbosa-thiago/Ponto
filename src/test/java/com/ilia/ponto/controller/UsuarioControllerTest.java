package com.ilia.ponto.controller;

import static com.ilia.ponto.util.ObjectCreator.createInvalidUsuarioPostBody;
import static com.ilia.ponto.util.ObjectCreator.createUsuarioPostBody;
import static com.ilia.ponto.util.ObjectCreator.createValidUsuario;

import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.projection.UsuarioUsernameId;
import com.ilia.ponto.requestbody.UsuarioPostBody;
import com.ilia.ponto.service.UsuarioService;
import com.ilia.ponto.util.ObjectCreator;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(SpringExtension.class)
class UsuarioControllerTest {

  @InjectMocks
  UsuarioController usuarioController;

  @Mock
  UsuarioService usuarioService;

  @BeforeEach
  void setup() {

    BDDMockito.when(usuarioService.save(createUsuarioPostBody()))
        .thenReturn(ObjectCreator.createValidUsuario());

    BDDMockito.when(usuarioService.save(ObjectCreator.createInvalidUsuarioPostBody()))
        .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  @Test
  void save_PersistsUsuario_WhenSuccessful() {
    ResponseEntity<Usuario> usuario = usuarioController.save(createUsuarioPostBody());

    Assertions.assertThat(usuario.getBody().getId()).isNotNull();
  }

  @Test
  void save_ThrowsException_WhenSuccessful() {

    UsuarioPostBody invalidUsuarioPostBody = createInvalidUsuarioPostBody();

    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> usuarioController.save(invalidUsuarioPostBody));
  }

  @Test
  void findAll_ReturnsUsuarioList_WhenSuccessful() {

    Usuario validUsuario = createValidUsuario();
    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
    UsuarioUsernameId projection = factory.createProjection(UsuarioUsernameId.class);
    projection.setId(validUsuario.getId());
    projection.setUsername(validUsuario.getUsername());
    PageImpl<UsuarioUsernameId> usuarioPage =
        new PageImpl<>(List.of(projection));

    BDDMockito.when(usuarioService.findAll(ArgumentMatchers.any()))
        .thenReturn(usuarioPage);

    Assertions.assertThat(usuarioController.findAll(null).getBody().toList().get(0).getUsername())
        .isNotNull();
  }
}