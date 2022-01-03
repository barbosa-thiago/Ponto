package com.ilia.ponto.service;

import com.ilia.ponto.keycloak.KeycloakService;
import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.repository.PontoRepository;
import com.ilia.ponto.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    validatingNumberOfRecordsPerDay(dateTime, usuario);

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

  private void validatingNumberOfRecordsPerDay(LocalDateTime localDateTime, Usuario usuario) {

    List<Ponto> byUsuarioId = pontoRepository.findByUsuarioId(usuario.getId());

    long count = byUsuarioId.stream()
        .map(ponto ->
            ponto.getLocalDateTime().toLocalDate())
        .filter(date -> date.isEqual(localDateTime.toLocalDate()))
        .count();

    if(count >= 4)
      throw new BadRequestException("não é possível registrar mais de 4 pontos por dia");
  }

  private void uniqueDateTimeOrThrowBadRequest(LocalDateTime localDateTime, Usuario usuario) {
    if(usuarioRepository.findByIdAndPontoLocalDateTime(usuario.getId(), localDateTime).isPresent())
        throw new BadRequestException("data e hora já constam no banco de dados");
  }
}
