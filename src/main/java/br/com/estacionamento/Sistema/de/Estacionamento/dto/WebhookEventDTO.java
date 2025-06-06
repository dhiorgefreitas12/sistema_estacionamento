package br.com.estacionamento.Sistema.de.Estacionamento.dto;

import br.com.estacionamento.Sistema.de.Estacionamento.enums.EventType;

public record WebhookEventDTO(
        String license_plate,
        String entry_time,
        String exit_time,
        Double lat,
        Double lng,
        EventType event_type
) {
}
