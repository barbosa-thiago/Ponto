package com.ilia.ponto.repository;


import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.model.Usuario;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsuarioRepositoryTest {

  @Autowired
  UsuarioRepository usuarioRepository;
  @Autowired
      PontoRepository pontoRepository;

  Usuario usuario = new Usuario(UUID.randomUUID(), "john", "john", null);
  Ponto ponto = new Ponto(null, LocalDateTime.now(), usuario);


  @Test
  @Order(0)
  void save_Creates_Usuario_When_Successful() {
    Usuario savesUser = usuarioRepository.save(usuario);

    Assertions.assertThat(savesUser.getId()).isNotNull().isEqualTo(usuario.getId());

  }

  @Test
  void save_ThrowsException_When_UsuarioIdIsNull() {
    usuario.setId(null);

    Assertions.assertThatExceptionOfType(JpaSystemException.class)
        .isThrownBy(() -> usuarioRepository.save(usuario));

  }

  @Test
  void findByUsername_ReturnsUsuarioOptional_WhenSuccessful() {

    usuarioRepository.save(usuario);

    Optional<Usuario> byUsername = usuarioRepository.findByUsername(usuario.getUsername());
    Assertions.assertThat(byUsername.get().getId()).isNotNull();

  }
}