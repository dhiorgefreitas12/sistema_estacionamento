package br.com.estacionamento.Sistema.de.Estacionamento.scheduler;

import br.com.estacionamento.Sistema.de.Estacionamento.integration.client.GarageClient;
import br.com.estacionamento.Sistema.de.Estacionamento.service.GarageService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class GarageStartupLoader implements ApplicationRunner {

    private final GarageClient garageClient;
    private final GarageService garageService;

    public GarageStartupLoader(GarageClient garageClient, GarageService garageService) {
        this.garageClient = garageClient;
        this.garageService = garageService;
    }

    @Override
    public void run(ApplicationArguments args) {
        var response = garageClient.fetchGarageData();
        garageService.saveGarageData(response);
    }
}
