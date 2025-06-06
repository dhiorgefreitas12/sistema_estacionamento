package br.com.estacionamento.Sistema.de.Estacionamento.integration.client;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.GarageResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GarageClient {

    private final WebClient webClient;

    public GarageClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:3000").build();
    }

    public GarageResponse fetchGarageData() {
        return webClient.get()
                .uri("/garage")
                .retrieve()
                .bodyToMono(GarageResponse.class)
                .block();
    }
}
