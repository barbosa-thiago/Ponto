package com.ilia.ponto.service;

import static com.ilia.ponto.util.ObjectCreator.createValidPonto;

import com.ilia.ponto.keycloak.KeycloakService;
import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.repository.PontoRepository;
import com.ilia.ponto.repository.UsuarioRepository;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PontoServiceTest {

  @InjectMocks
  PontoService pontoService;

  @Mock
  PontoRepository pontoRepository;
  @Mock
  KeycloakService keycloakService;
  @Mock
  UsuarioRepository usuarioRepository;

  Ponto ponto = createValidPonto();
  Usuario usuario = ponto.getUsuario();
  LocalDateTime localDateTime = LocalDateTime.of(2022, 1, 3, 12, 0, 0);

  @BeforeEach
  void setup() {
    BDDMockito.when(pontoRepository.save(ArgumentMatchers.any(Ponto.class)))
        .thenReturn(ponto);
    BDDMockito.when(pontoRepository
        .registroPontosDiarioUsuarioLogado(
            ArgumentMatchers.any(LocalDateTime.class),
            ArgumentMatchers.eq(usuario.getId())))
        .thenReturn(List.of(ponto));

    BDDMockito.when(keycloakService.getUsuarioUsername())
        .thenReturn(usuario.getUsername());

    BDDMockito.when(usuarioRepository.findByUsername(ArgumentMatchers.anyString()))
        .thenReturn(Optional.of(usuario));
  }

  @Test
  @Order(0)
  @DisplayName("save persists Ponto when successful")
  void save_PersistsPonto_WhenSuccessful() {
    Ponto savedPonto = pontoService.save(localDateTime.toString());

    Assertions.assertThat(savedPonto.getId()).isNotNull();
  }

  @Test
  @DisplayName("save throws ResponseStatusException when backward time")
  void save_ThrowsException_WhenBackwardTime() {
    LocalDateTime dateTime = localDateTime;
    pontoService.save(dateTime.toString());

    String backwardSchedule = ponto.getLocalDateTime().minusMinutes(1).toString();
    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> pontoService.save(backwardSchedule));
  }

  @Test
  @DisplayName("save throws ResponseStatusException when lunch time less than hour")
  void save_ThrowsException_WhenLunchTimeLessThanHour() {
    List<Ponto> pontos = List.of(this.ponto, new Ponto(2L, localDateTime, usuario));

    BDDMockito.when(
        pontoRepository.registroPontosDiarioUsuarioLogado(
            ArgumentMatchers.any(LocalDateTime.class),
            ArgumentMatchers.eq(usuario.getId())))
        .thenReturn(new ArrayList<>(pontos));


    String lunchTime = localDateTime.toString();
    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> pontoService.save(lunchTime));
  }

  @Test
  @DisplayName("save throws ResponseStatusException when LocalDateTime wrong format")
  void save_ThrowsException_WhenDateTimeWrongFormat() {

    String dateTime = localDateTime.toString();

    String replace = dateTime.replace("-", "/");

    Assertions.assertThatExceptionOfType(DateTimeParseException.class)
        .isThrownBy(() -> pontoService.save(replace));
  }

  @Test
  @DisplayName("save throws ResponseStatusException when limit schedules per day reached for user")
  void save_ThrowsException_WhenLimitSchedulesPerDayReached() {

    List<Ponto> pontos =
        List.of(
            this.ponto,
            new Ponto(2L, localDateTime, usuario),
            new Ponto(3L, localDateTime.plusHours(1), usuario),
            new Ponto(4L, localDateTime.plusHours(2), usuario));

    BDDMockito.when(
        pontoRepository.registroPontosDiarioUsuarioLogado(
            ArgumentMatchers.any(LocalDateTime.class),
            ArgumentMatchers.eq(usuario.getId())))
        .thenReturn(new ArrayList<>(pontos));

    String dateTime = localDateTime.toString();

    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> pontoService.save(dateTime));
  }

  @Test
  @DisplayName("save throws exception when duplicate LocalDateTime for user")
  void save_ThrowsException_WhenDuplicateDateTimeForUser() {

    String duplicatedDateTime = ponto.getLocalDateTime().toString();
    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> pontoService.save(duplicatedDateTime));
  }

  @Test
  @DisplayName("save throws exception when sunday or saturdayfixing")
  void save_ThrowsException_WhenSundayOrSaturday() {

    LocalDateTime dateTime = LocalDateTime.now();

    String saturday = dateTime.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)).toString();

    String sunday = dateTime.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).toString();

    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> pontoService.save(saturday));
    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> pontoService.save(sunday));
  }
}