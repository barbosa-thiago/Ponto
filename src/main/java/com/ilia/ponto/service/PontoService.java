package com.ilia.ponto.service;

import com.ilia.ponto.keycloak.KeycloakService;
import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.repository.PontoRepository;
import com.ilia.ponto.repository.UsuarioRepository;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PontoService {
  private final PontoRepository pontoRepository;
  private final KeycloakService keycloakService;
  private final UsuarioRepository usuarioRepository;

  public Ponto save(String dateTime) {

    Usuario usuario = getLoggedUser();
    LocalDateTime dateTimeFormatted = formatDate(dateTime);

    List<Ponto> registroDiarioPontosUsuario =
        pontoRepository.findUserSchedulesPerDay(dateTimeFormatted, usuario.getId());

    limitSchedulesPerDay(registroDiarioPontosUsuario);
    backwardTimeThrowsException(registroDiarioPontosUsuario, dateTimeFormatted);
    uniqueDateTimeForUserOrThrowException(dateTimeFormatted, registroDiarioPontosUsuario);
    oneHourLunchValidation(registroDiarioPontosUsuario, dateTimeFormatted);
    saturdaySundayThrowsException(dateTimeFormatted);

    Ponto ponto = new Ponto(null, dateTimeFormatted, usuario);

    return pontoRepository.save(ponto);
  }

  public Page<Ponto> findPontosPerUserLast30days(Pageable pageable, UUID id) {
    LocalDateTime now = LocalDateTime.now();
    return pontoRepository.findPontosPerUserLast30days(pageable, id, now);
  }

  private void backwardTimeThrowsException(List<Ponto> pontos, LocalDateTime localDateTime) {
    pontos
        .stream()
        .filter(ponto -> ponto.getLocalDateTime().isAfter(localDateTime))
        .forEach(ponto -> {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "horário anterior ao último registrado");
        });
  }


  private LocalDateTime formatDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    return LocalDateTime.parse(date, formatter);
  }

  private void limitSchedulesPerDay(List<Ponto> pontos) {
    if (pontos.size() > 3) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "não é permitido registrar mais um ponto hoje, máximo: 4");
    }
  }

  private Usuario getLoggedUser() {
    String username = keycloakService.getUsuarioUsername();
    return usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("erro: nenhum usuario encontrado"));
  }

  private void saturdaySundayThrowsException(LocalDateTime localDateTime) {
    DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
    if (dayOfWeek == DayOfWeek.SATURDAY) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Ponto aos sábados não é permitido");
    } else if(dayOfWeek == DayOfWeek.SUNDAY) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Ponto aos domingos não é permitido");
    }
  }

  private void uniqueDateTimeForUserOrThrowException(LocalDateTime localDateTime, List<Ponto> pontos) {
    pontos.stream()
        .filter(ponto -> ponto.getLocalDateTime().isEqual(localDateTime))
        .forEach(ponto -> {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "data e hora já constam no banco de dados para usuario");
        });
  }

  private void oneHourLunchValidation(List<Ponto> pontos, LocalDateTime localDateTime) {

    if (pontos.size() < 2) return;

    pontos.sort(Comparator.comparing(Ponto::getLocalDateTime));
    LocalDateTime dateTime = pontos.get(1).getLocalDateTime();

    if (dateTime.isAfter(localDateTime.minusHours(1)) && pontos.size() == 2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Não é permitido menos de 1 hora de almoço");
    }
  }
}
