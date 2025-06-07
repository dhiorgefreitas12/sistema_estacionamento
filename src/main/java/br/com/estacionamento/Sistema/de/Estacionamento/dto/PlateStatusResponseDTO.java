package br.com.estacionamento.Sistema.de.Estacionamento.dto;

import java.time.LocalDateTime;


public record PlateStatusResponseDTO(
        String license_plate,
        double price_until_now,
        LocalDateTime entry_time,
        LocalDateTime time_parked
) {
}
