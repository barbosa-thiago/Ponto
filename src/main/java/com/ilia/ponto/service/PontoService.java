package com.ilia.ponto.service;

import com.ilia.ponto.keycloak.KeycloakService;
import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.repository.PontoRepository;
import com.ilia.ponto.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PontoService {
  private final PontoRepository pontoRepository;
  private final KeycloakService keycloakService;
  private final UsuarioRepository usuarioRepository;

  public Ponto save(String date) {

    LocalDateTime dateTime = formatDate(date);

    uniqueDateOrThrowBadRequest(dateTime);


    Usuario usuario = getUsuarioLogado();

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

  private void uniqueDateOrThrowBadRequest(LocalDateTime localDateTime) {
    if(usuarioRepository.findByPontoLocalDateTime(localDateTime).isPresent())
        throw new BadRequestException("data já consta no banco de dados");
  }
}
