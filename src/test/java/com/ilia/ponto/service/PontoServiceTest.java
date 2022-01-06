package com.ilia.ponto.service;

import static com.ilia.ponto.util.ObjectCreator.createValidPonto;

import com.ilia.ponto.keycloak.KeycloakService;
import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.repository.PontoRepository;
import com.ilia.ponto.repository.UsuarioRepository;
import com.ilia.ponto.util.ObjectCreator;
import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class PontoServiceTest {

  @InjectMocks
  PontoService pontoService;

  @Mock
  PontoRepository pontoRepository;
  @Mock
  KeycloakService keycloakService;
  @Mock
  UsuarioRepository usuarioRepository;

  @BeforeEach
  void setup() {
    BDDMockito.when(pontoRepository.save(ArgumentMatchers.any(Ponto.class)))
        .thenReturn(createValidPonto());
    BDDMockito.when(keycloakService.getUsuarioUsername())
        .thenReturn(ObjectCreator.createValidUsuario().getUsername());
    BDDMockito.when(usuarioRepository.findByUsername(ArgumentMatchers.anyString()))
        .thenReturn(Optional.of(ObjectCreator.createValidUsuario()));
  }

  @Test
  void save_PersistsPonto_WhenSuccessful() {
    Ponto savedPonto = pontoService.save(LocalDateTime.now().toString());

    Assertions.assertThat(savedPonto.getId()).isNotNull();
  }
}