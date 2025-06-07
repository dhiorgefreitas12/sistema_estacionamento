package br.com.estacionamento.Sistema.de.Estacionamento.controller;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.PlateStatusRequestDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.dto.PlateStatusResponseDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.service.PlateStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plate-status")
@Tag(name = "Consulta de Placa", description = "Permite consultar o status atual de uma placa no estacionamento")
public class PlateStatusController {

    @Autowired
    private PlateStatusService plateStatusService;

    @PostMapping
    @Operation(
            summary = "Consultar status de uma placa",
            description = "Retorna o tempo de permanência e valor a ser pago até o momento para uma placa que ainda está estacionada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status retornado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Placa não encontrada ou sem sessão ativa"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            }
    )
    public PlateStatusResponseDTO getPlateStatus(@RequestBody PlateStatusRequestDTO request) {
        return plateStatusService.getStatusByLicensePlate(request.license_plate());
    }
}
