package com.ilia.ponto.integration;

import static com.ilia.ponto.util.ObjectCreator.createUsuarioPostBody;
import static com.ilia.ponto.util.ObjectCreator.createValidUsuario;

import com.ilia.ponto.keycloak.KeycloakService;
import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.repository.PontoRepository;
import com.ilia.ponto.repository.UsuarioRepository;
import com.ilia.ponto.requestbody.UsuarioPostBody;
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
class UsuarioControllerIntTest {

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



  @BeforeEach
  void setup() {

    webTestClient = MockMvcWebTestClient.bindToApplicationContext(this.context).build();

    BDDMockito.when(keycloakService.createKeycloakUser(createUsuarioPostBody()))
        .thenReturn(validUsuario.getId());
  }

  @Test
  @Order(-1)
  @DisplayName("save create Usuario when successful")
  void save_CreateUsuario_WhenSuccessful() {


    webTestClient.post()
        .uri("/usuario")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(createUsuarioPostBody())
        .exchange()
        .expectStatus().isCreated();

  }

  @Test
  @Order(0)
  @DisplayName("save throws exception when username exists")
  void save_ThrowsException_WhenUsernameExists() {

    UsuarioPostBody usuarioPostBody = createUsuarioPostBody();

    webTestClient.post()
        .uri("/usuario")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(usuarioPostBody)
        .exchange()
        .expectStatus().isBadRequest();

  }

  @Test
  @Order(0)
  @DisplayName("save throws exception when password is null")
  void save_ThrowsException_WhenPasswordIsNull() {

    UsuarioPostBody usuarioPostBody = new UsuarioPostBody(null, "george", "George");

        webTestClient.post()
            .uri("/usuario")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(usuarioPostBody)
            .exchange()
            .expectStatus().isBadRequest();

  }

  @Test
  @Order(0)
  @DisplayName("findAllProjectedBy returns UserPage when successful")
  void findAllProjectedBy_ReturnsUserPage_WhenSuccessful() {

    webTestClient.get()
        .uri("/usuario")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk().expectBody().jsonPath("$.numberOfElements").isEqualTo(1);

  }
}
