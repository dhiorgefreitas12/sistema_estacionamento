package br.com.estacionamento.Sistema.de.Estacionamento.dto;

import java.time.LocalDateTime;

public record RevenueResponseDTO(
        double amount,
        String currency,
        LocalDateTime timestamp
) {
}