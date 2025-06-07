package br.com.estacionamento.Sistema.de.Estacionamento.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SpotStatusResponseDTO(
        boolean occupy,
        LocalDateTime entry_time,
        LocalDateTime time_parked
) {
}
