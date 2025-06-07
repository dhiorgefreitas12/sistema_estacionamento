package br.com.estacionamento.Sistema.de.Estacionamento.controller;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.SpotStatusRequestDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.dto.SpotStatusResponseDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.service.SpotStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spot-status")
@Tag(name = "Spot Status", description = "Consulta status de ocupação de uma vaga por coordenadas geográficas.")
public class SpotStatusController {

    @Autowired
    private SpotStatusService spotStatusService;

    @PostMapping
    @Operation(
            summary = "Consulta status da vaga",
            description = "Retorna se uma vaga está ocupada com base na latitude e longitude informadas.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Retorna o status da vaga",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SpotStatusResponseDTO.class),
                                    examples = {
                                            @ExampleObject(name = "Ocupada", value = "{ \"occupy\": true, \"entry_time\": \"2025-06-07T08:00:00\", \"time_parked\": \"2025-06-07T12:00:00\" }"),
                                            @ExampleObject(name = "Livre", value = "{ \"occupy\": false }")
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<SpotStatusResponseDTO> getSpotStatus(@RequestBody SpotStatusRequestDTO dto) {
        return ResponseEntity.ok(spotStatusService.getStatusByCoordinates(dto));
    }
}
