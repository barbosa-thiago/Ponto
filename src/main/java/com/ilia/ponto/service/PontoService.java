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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    Usuario usuario = getUsuarioLogado();
    LocalDateTime dateTimeFormatted = formatDate(dateTime);

    List<Ponto> registroDiarioPontosUsuario =
        pontoRepository.registroPontosDiarioUsuarioLogado(dateTimeFormatted, usuario.getId());

    limiteDiarioDeRegistros(registroDiarioPontosUsuario);
    horarioRetroativoLancaExcecao(registroDiarioPontosUsuario, dateTimeFormatted);
    dataHoraUnicosParaUsuarioOuLancaExcecao(dateTimeFormatted, registroDiarioPontosUsuario);
    validandoHoraioAlmoco(registroDiarioPontosUsuario, dateTimeFormatted);
    sabadoOuDomingoLancamExcecao(dateTimeFormatted);

    Ponto ponto = new Ponto(null, dateTimeFormatted, usuario);

    return pontoRepository.save(ponto);
  }

  private void horarioRetroativoLancaExcecao(List<Ponto> pontos, LocalDateTime localDateTime) {
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

  private void limiteDiarioDeRegistros(List<Ponto> pontos) {
    if (pontos.size() > 3) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "não é permitido registrar mais um ponto hoje, máximo: 4");
    }
  }

  private Usuario getUsuarioLogado() {
    String username = keycloakService.getUsuarioUsername();
    return usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("erro: nenhum usuario encontrado"));
  }

  private void sabadoOuDomingoLancamExcecao(LocalDateTime localDateTime) {
    DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
    if (dayOfWeek == DayOfWeek.SATURDAY ||
        dayOfWeek == DayOfWeek.SUNDAY) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Ponto aos sábados e domingos não é permitido");
    }
  }

  private void dataHoraUnicosParaUsuarioOuLancaExcecao(LocalDateTime localDateTime, List<Ponto> pontos) {
    pontos.stream()
        .filter(ponto -> ponto.getLocalDateTime().isEqual(localDateTime))
        .forEach(ponto -> {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "data e hora já constam no banco de dados para usuario");
        });
  }

  private void validandoHoraioAlmoco(List<Ponto> pontos, LocalDateTime localDateTime) {

    if (pontos.size() < 2) return;

    pontos.sort(Comparator.comparing(Ponto::getLocalDateTime));
    LocalDateTime dateTime = pontos.get(1).getLocalDateTime();

    if (dateTime.isAfter(localDateTime.minusHours(1)) && pontos.size() == 2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Não é permitido menos de 1 hora de almoço");
    }
  }
}
