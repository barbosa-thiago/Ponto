package com.ilia.ponto.repository;


import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.model.Usuario;
import java.time.LocalDateTime;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PontoRepositoryTest {

  @Autowired
  PontoRepository pontoRepository;
  @Autowired
  UsuarioRepository usuarioRepository;

  Usuario usuario = new Usuario(UUID.randomUUID(), "john", "john", null);
  Ponto ponto = new Ponto(null, LocalDateTime.now(), usuario);



  @Test
  @Order(0)
  void save_Creates_Ponto_When_Successful() {
    Usuario savesUser = usuarioRepository.save(usuario);
    Ponto save = pontoRepository.save(ponto);

    Assertions.assertThat(save.getId()).isNotNull().isEqualTo(1);

  }

  @Test
  void save_ThrowsException_When_UsuarioIsNull() {
    Ponto ponto = new Ponto(null, LocalDateTime.now(), null);

    Assertions.assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> pontoRepository.save(ponto));

  }
}