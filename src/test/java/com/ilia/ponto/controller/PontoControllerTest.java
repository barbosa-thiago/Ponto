package com.ilia.ponto.controller;

import static com.ilia.ponto.util.ObjectCreator.createValidPonto;

import com.ilia.ponto.model.DateInput;
import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.service.PontoService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class PontoControllerTest {

  @InjectMocks
  PontoController pontoController;
  @Mock
  PontoService pontoService;

  @BeforeEach
  void setup() {
    Ponto validPonto = createValidPonto();
    PageImpl<Ponto> pontoPage = new PageImpl<>(List.of(validPonto));

    BDDMockito.when(pontoService.save(ArgumentMatchers.anyString()))
        .thenReturn(validPonto);

    BDDMockito.when(pontoService.findPontosPerUserLast30days(ArgumentMatchers.any(), ArgumentMatchers.any(UUID.class)))
        .thenReturn(pontoPage);
  }

  @Test
  void save_PersistsPonto_WhenSuccessful() {

    ResponseEntity<Ponto> savedPonto = pontoController.save(new DateInput(LocalDateTime.now().toString()));

    Assertions.assertThat(savedPonto.getBody().getId()).isNotNull();
  }

  @Test
  void findPontosPerUserLast30days_ReturnsPontoPage_WhenSuccessful() {

    ResponseEntity<Ponto> savedPonto = pontoController.save(new DateInput(LocalDateTime.now().toString()));

    ResponseEntity<Page<Ponto>> pontosPerUserLast30days =
        pontoController.findPontosPerUserLast30days(null, UUID.randomUUID());
    Assertions.assertThat(pontosPerUserLast30days.getBody().toList().get(0).getId()).isNotNull();
  }
}