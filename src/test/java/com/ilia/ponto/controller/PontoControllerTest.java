package com.ilia.ponto.controller;

import static com.ilia.ponto.util.ObjectCreator.createValidPonto;

import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.service.PontoService;
import com.ilia.ponto.util.ObjectCreator;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    BDDMockito.when(pontoService.save(ArgumentMatchers.anyString()))
        .thenReturn(createValidPonto());
  }

  @Test
  void save_PersistsPonto_WhenSuccessful() {
    ResponseEntity<Ponto> savedPonto = pontoController.save(LocalDateTime.now().toString());

    Assertions.assertThat(savedPonto.getBody().getId()).isNotNull();
  }
}