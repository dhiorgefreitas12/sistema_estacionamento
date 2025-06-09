package br.com.estacionamento.Sistema.de.Estacionamento.controller;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.RevenueRequestDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.dto.RevenueResponseDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.service.RevenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/revenue")
@Tag(name = "Revenue", description = "API para cálculo de faturamento por setor e data")
public class RevenueController {

    @Autowired
    private RevenueService service;


    @PostMapping
    @Operation(
            summary = "Consulta o faturamento do setor em uma data específica",
            description = "Retorna o valor total faturado para um setor em um dia, baseado nas sessões encerradas.",

            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Faturamento calculado com sucesso"
                    ),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
            }
    )
    public RevenueResponseDTO getRevenue(@RequestBody RevenueRequestDTO dto) {
        return service.getRevenue(dto);
    }
}
