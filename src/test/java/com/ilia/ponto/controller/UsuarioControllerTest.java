package com.ilia.ponto.controller;

import static com.ilia.ponto.util.ObjectCreator.createInvalidUsuarioPostBody;
import static com.ilia.ponto.util.ObjectCreator.createUsuarioPostBody;

import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.service.UsuarioService;
import com.ilia.ponto.util.ObjectCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> usuarioController.save(createInvalidUsuarioPostBody()));
  }
}