package br.com.estacionamento.Sistema.de.Estacionamento.dto;

public record SpotDTO(
        Long id,
        String sector,
        double lat,
        double lng
) {
}
