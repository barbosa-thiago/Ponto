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
import javax.ws.rs.BadRequestException;
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

  public Ponto save(String date) {

    LocalDateTime dateTime = formatDate(date);

    Usuario usuario = getUsuarioLogado();

    validatingLunchTime(dateTime, usuario.getId());

    validatingNumberOfRecordsPerDay(dateTime, usuario.getId());

    notSaturdayNorSundayOrThrowException(dateTime);

    uniqueDateTimeOrThrowBadRequest(dateTime, usuario);

    Ponto ponto = new Ponto(null, dateTime, usuario);

    return pontoRepository.save(ponto);
  }


  private LocalDateTime formatDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    return LocalDateTime.parse(date, formatter);
  }

  private Usuario getUsuarioLogado() {
    String username = keycloakService.getUsuarioUsername();
    return usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("erro: nenhum usuario encontrado"));
  }

  private void validatingNumberOfRecordsPerDay(LocalDateTime localDateTime, UUID id) {

    if (pontoRepository.isLimitRecordsPerDayReached(localDateTime, id))
      throw new BadRequestException("não é possível registrar mais de 4 pontos por dia");
  }

  private void notSaturdayNorSundayOrThrowException(LocalDateTime localDateTime) {
    DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
    if(dayOfWeek == DayOfWeek.SATURDAY ||
        dayOfWeek == DayOfWeek.SUNDAY)
      throw new BadRequestException("Ponto aos sábados e domingos não é permitido");
  }

  private void uniqueDateTimeOrThrowBadRequest(LocalDateTime localDateTime, Usuario usuario) {
    if(usuarioRepository.findByIdAndPontoLocalDateTime(usuario.getId(), localDateTime).isPresent())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "data e hora já constam no banco de dados");
  }

  private void validatingLunchTime(LocalDateTime localDateTime, UUID id) {
    List<Ponto> pontos = pontoRepository.findRecordedPontosOnDayByUserId(localDateTime, id);

    if (pontos.size() == 2) {
      pontos.sort(Comparator.comparing(Ponto::getLocalDateTime));
      LocalDateTime dateTime = pontos.get(1).getLocalDateTime();

      if (dateTime.isAfter(localDateTime.minusHours(1)))
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é permitido menos de 1 hora de almoço");
    }
  }
}
