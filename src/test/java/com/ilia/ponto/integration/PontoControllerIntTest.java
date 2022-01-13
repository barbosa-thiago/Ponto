package com.ilia.ponto.integration;

import static com.ilia.ponto.util.ObjectCreator.createPontoToBeSaved;
import static com.ilia.ponto.util.ObjectCreator.createUsuarioPostBody;
import static com.ilia.ponto.util.ObjectCreator.createValidUsuario;

import com.ilia.ponto.keycloak.KeycloakService;
import com.ilia.ponto.model.DateInput;
import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.repository.PontoRepository;
import com.ilia.ponto.repository.UsuarioRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureTestDatabase
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PontoControllerIntTest {

  @Autowired
  private WebApplicationContext context;
  @Autowired
  private WebTestClient webTestClient;
  @MockBean
  KeycloakService keycloakService;

  @Autowired
  PontoRepository pontoRepository;
  @Autowired
  UsuarioRepository usuarioRepository;

  Usuario validUsuario = createValidUsuario();

  LocalDateTime localDateTime = createPontoToBeSaved().getLocalDateTime();


  @BeforeEach
  void setup() {

    webTestClient = MockMvcWebTestClient.bindToApplicationContext(this.context).build();

    BDDMockito.when(keycloakService.createKeycloakUser(createUsuarioPostBody()))
        .thenReturn(validUsuario.getId());
    BDDMockito.when(keycloakService.getUsuarioUsername())
        .thenReturn(validUsuario.getUsername());
  }

  @Test
  @Order(-1)
  @DisplayName("save create Ponto when successful")
  void save_CreatePonto_WhenSuccessful() {

    usuarioRepository.save(validUsuario);

    webTestClient.post()
        .uri("/schedules")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new DateInput(localDateTime.toString()))
        .exchange()
        .expectStatus().isCreated();

  }

  @Test
  @Order(0)
  @DisplayName("save_ThrowsException_WhenInvalidDate")
  void findPontosPerUserLast30days_ReturnsPontoPage_WhenSuccessful() {

    Usuario usuario = usuarioRepository.save(validUsuario);

    webTestClient.get()
        .uri(uriBuilder ->
            uriBuilder
                .path("/schedules/listForUser").queryParam("userId", usuario.getId()).build())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody().jsonPath("$.numberOfElements").isEqualTo(1);

  }

  @Test
  @Order(0)
  @DisplayName("save_ThrowsException_WhenDuplicateDateTime")
  void save_ThrowsException_WhenDuplicateDateTime() {

    usuarioRepository.save(validUsuario);

    webTestClient.post()
        .uri("/schedules")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new DateInput(localDateTime.toString()))
        .exchange()
        .expectStatus().isBadRequest();

  }

  @Test
  @Order(0)
  @DisplayName("save_ThrowsException_WhenBackwardDate")
  void save_ThrowsException_WhenBackwardDate() {

    LocalDateTime dateTime = localDateTime.minusMinutes(1);

    usuarioRepository.save(validUsuario);

    webTestClient.post()
        .uri("/schedules")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new DateInput(dateTime.toString()))
        .exchange()
        .expectStatus().isBadRequest();

  }

  @Test
  @Order(0)
  @DisplayName("save_ThrowsException_WhenInvalidDate")
  void save_ThrowsException_WhenInvalidDate() {

    usuarioRepository.save(validUsuario);

        webTestClient.post()
            .uri("/schedules")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new DateInput("8:00:00"))
            .exchange().expectStatus().is4xxClientError();

  }

  @Test
  @Order(0)
  @DisplayName("save_ThrowsException_WhenLimitSchedulesReached")
  void save_ThrowsException_WhenLunchTimeLesThanHour() {

    usuarioRepository.save(validUsuario);

    WebTestClient.RequestBodySpec requestBodySpec = webTestClient.post()
        .uri("/schedules")
        .contentType(MediaType.APPLICATION_JSON);

    LocalDateTime secondSchedule = localDateTime.plusHours(1);
    requestBodySpec
        .bodyValue(new DateInput(secondSchedule.toString()))
        .exchange()
        .expectStatus().isCreated();

    LocalDateTime thirdSchedule = secondSchedule.plusMinutes(30);
    requestBodySpec
        .bodyValue(new DateInput(thirdSchedule.toString()))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  @Order(1)
  @DisplayName("save_ThrowsException_LimitSchedulesPerDayReached")
  void save_ThrowsException_LimitSchedulesPerDayReached() {

    LocalDateTime dateTime = localDateTime.plusHours(1);

    usuarioRepository.save(validUsuario);


    pontoRepository.save(new Ponto(null, dateTime.plusHours(1), validUsuario));
    pontoRepository.save(new Ponto(null, dateTime.plusHours(2), validUsuario));

        webTestClient.post()
            .uri("/schedules")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new DateInput(dateTime.toString()))
            .exchange()
            .expectStatus()
            .isBadRequest();

  }
}
