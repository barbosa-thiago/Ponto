package com.ilia.ponto.utils;

import com.ilia.ponto.keycloak.KeycloakService;
import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.repository.PontoRepository;
import com.ilia.ponto.repository.UsuarioRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Stream;
import javax.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PontoPersisterUtil {

  private final PontoRepository pontoRepository;
  private final KeycloakService keycloakService;
  private final UsuarioRepository usuarioRepository;

  public LocalDateTime formatDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    return LocalDateTime.parse(date, formatter);
  }

  public Usuario getUsuarioLogado() {
    String username = keycloakService.getUsuarioUsername();
    return usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("erro: nenhum usuario encontrado"));
  }

  public void validatingNumberOfRecordsPerDay(LocalDateTime localDateTime, Usuario usuario) {

    long count = getPontoByUsuarioIdPontosPerDay(usuario.getId(), localDateTime).count();

    if(count >= 4)
      throw new BadRequestException("não é possível registrar mais de 4 pontos por dia");
  }

  public void notSaturdayNorSundayOrThrowException(LocalDateTime localDateTime) {
    DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
    if(dayOfWeek == DayOfWeek.SATURDAY ||
    dayOfWeek == DayOfWeek.SUNDAY)
      throw new BadRequestException("Ponto aos sábados e domingos não é permitido");
  }

  public void uniqueDateTimeOrThrowBadRequest(LocalDateTime localDateTime, Usuario usuario) {
    if(usuarioRepository.findByIdAndPontoLocalDateTime(usuario.getId(), localDateTime).isPresent())
      throw new BadRequestException("data e hora já constam no banco de dados");
  }

  private Stream<LocalDate> getPontoByUsuarioIdPontosPerDay(UUID id, LocalDateTime localDateTime) {

    return pontoRepository.findByUsuarioId(id)
        .stream()
        .map(ponto ->
            ponto.getLocalDateTime().toLocalDate())
        .filter(date -> date.isEqual(localDateTime.toLocalDate()));
  }
}
