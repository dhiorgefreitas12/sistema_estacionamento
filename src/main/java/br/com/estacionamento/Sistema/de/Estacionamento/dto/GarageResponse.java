package br.com.estacionamento.Sistema.de.Estacionamento.dto;

import java.util.List;

public record GarageResponse(
        List<SectorDTO> garage,
        List<SpotDTO> spots
) {}
