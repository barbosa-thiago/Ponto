package com.ilia.ponto.service;

import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.repository.PontoRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PontoService {
  private final PontoRepository pontoRepository;

  public Ponto save(String date) {
    formatDate(date);
    Ponto ponto = new Ponto(null, formatDate(date));
    return pontoRepository.save(ponto);
  }

  private LocalDateTime formatDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    return LocalDateTime.parse(date);
  }
}
