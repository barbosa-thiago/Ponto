package com.ilia.ponto.service;

import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.repository.PontoRepository;
import com.ilia.ponto.utils.PontoPersisterUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PontoService {
  private final PontoRepository pontoRepository;
  private final PontoPersisterUtil pontoPersisterUtil;

  public Ponto save(String date) {

    LocalDateTime dateTime = pontoPersisterUtil.formatDate(date);

    Usuario usuario = pontoPersisterUtil.getUsuarioLogado();

    pontoPersisterUtil.validatingNumberOfRecordsPerDay(dateTime, usuario);

    pontoPersisterUtil.notSaturdayNorSundayOrThrowException(dateTime);

    pontoPersisterUtil.uniqueDateTimeOrThrowBadRequest(dateTime, usuario);

    Ponto ponto = new Ponto(null, dateTime, usuario);

    return pontoRepository.save(ponto);
  }


}
