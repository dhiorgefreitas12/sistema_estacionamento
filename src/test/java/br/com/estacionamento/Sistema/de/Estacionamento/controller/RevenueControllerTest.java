package br.com.estacionamento.Sistema.de.Estacionamento.controller;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.RevenueRequestDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.model.ParkingSession;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.ParkingSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RevenueControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ParkingSessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private WebTestClient webClient;

    @BeforeEach
    void setup() {
        webClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

        sessionRepository.deleteAll();
        ParkingSession session1 = new ParkingSession();
        session1.setLicensePlate("ZUL0001");
        session1.setSector("A");
        session1.setPrice(40.0);
        session1.setEntryTime(LocalDateTime.of(2025, 6, 7, 9, 0));
        session1.setExitTime(LocalDateTime.of(2025, 6, 7, 11, 0));
        session1.setSpotId(1L);

        ParkingSession session2 = new ParkingSession();
        session2.setLicensePlate("ZUL0002");
        session2.setSector("A");
        session2.setPrice(32.0);
        session2.setEntryTime(LocalDateTime.of(2025, 6, 7, 12, 0));
        session2.setExitTime(LocalDateTime.of(2025, 6, 7, 14, 0));
        session2.setSpotId(2L);

        sessionRepository.save(session1);
        sessionRepository.save(session2);
    }


    @Test
    void shouldReturnTotalRevenue() {
        RevenueRequestDTO request = new RevenueRequestDTO("2025-06-07", "A");

        webClient.post()
                .uri("/revenue")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.amount").isEqualTo(72.0)
                .jsonPath("$.currency").isEqualTo("BRL")
                .jsonPath("$.timestamp").isNotEmpty();
    }
}
