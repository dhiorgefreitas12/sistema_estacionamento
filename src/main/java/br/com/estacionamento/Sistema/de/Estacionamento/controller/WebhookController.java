package br.com.estacionamento.Sistema.de.Estacionamento.controller;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.WebhookEventDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
@Tag(name = "Webhook", description = "Recebe eventos do estacionamento")
public class WebhookController {

    @Autowired
    private EventService eventService;

    @Operation(
            summary = "Recebe evento de entrada, estacionado ou saída",
            description = "Processa eventos de placa com tipos ENTRY, PARKED ou EXIT"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento processado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<Void> handleEvent(@RequestBody WebhookEventDTO event) {
        eventService.processEvent(event);
        return ResponseEntity.ok().build();
    }
}
