package com.ilia.ponto.repository;


import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.util.ObjectCreator;
import java.time.LocalDateTime;
import java.util.List;
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

  Ponto ponto = ObjectCreator.createValidPonto();
  Usuario usuario = ponto.getUsuario();



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

  @Test
  void findUserSchedulesPerDay_ReturnsPontoList_WhenSuccessful() {

    usuarioRepository.save(usuario);
    pontoRepository.save(ponto);

    List<Ponto> userSchedulesPerDay =
        pontoRepository.findUserSchedulesPerDay(ponto.getLocalDateTime(), usuario.getId());
    Assertions.assertThat(userSchedulesPerDay.get(0).getId()).isNotNull();

  }
}